package cc.boeters.p2000monitor.archive;

import static cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.newPostcodeQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.MatchType;
import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.MessageSource;
import cc.boeters.p2000monitor.archive.PostcodeQueryBuilder.PostcodeQuery;
import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.support.annotation.Property;

@Singleton
public class PostcodeDbMessageDecomposer implements MessageDecomposer {

	enum DecomposeMethod {
		POSTCODE, STREETCITY, STREETPARTIALPOSTCODE, STREETCAPCODE, STREETSECTOR;

		public static final EnumSet<DecomposeMethod> ORDERED = EnumSet.of(
				POSTCODE, STREETCITY, STREETCAPCODE, STREETSECTOR,
				STREETPARTIALPOSTCODE);
	}

	public static void main(String[] args) {
		new PostcodeDbMessageDecomposer();
	}

	static final Logger LOG = LoggerFactory
			.getLogger(PostcodeDbMessageDecomposer.class);

	private double succesful;

	private double total;

	private static final Pattern PATTERN_POSTCODE = Pattern
			.compile("([1-9]{1}[0-9]{3}[A-Z]{2})");

	private Connection connection;

	@Inject
	private void createConnection(@Property("mysql.host") String host,
			@Property("mysql.port") String port,
			@Property("mysql.user") String user,
			@Property("mysql.password") String password,
			@Property("mysql.database") String database) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://" + host
					+ "/" + database + "?" + "user=" + user + "&password="
					+ password);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			LOG.error("Unable to connect to MySQL database.", e);
		}
	}

	@Override
	public Map<String, Object> decompose(Message message) {
		total++;

		List<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		for (DecomposeMethod method : DecomposeMethod.ORDERED) {
			try {
				findMetaDataBy(method, message, metadata);
			} catch (SQLException e) {
				throw new RuntimeException("Unable to decompose message.", e);
			}
			if (!metadata.isEmpty()) {
				break;
			}
		}

		if (!metadata.isEmpty()) {
			succesful++;
			return metadata.get(0);
		}

		if (total % 100 == 0) {
			LOG.info("Percent decomposed: " + ((succesful / total) * 100.0));
		}

		return new HashMap<String, Object>(0);
	}

	private List<Map<String, Object>> executeQuery(PostcodeQuery query) {
		List<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		try {
			PreparedStatement prepareStatement = connection
					.prepareStatement(query.getQuery());
			for (int index = 0; index < query.getValues().size(); index++) {
				prepareStatement.setString(index + 1,
						query.getValues().get(index));
			}
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				ResultSetMetaData rowMetadata = executeQuery.getMetaData();
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 0; i < rowMetadata.getColumnCount(); i++) {
					row.put(rowMetadata.getColumnName(i + 1),
							executeQuery.getObject(i + 1));
				}
				metadata.add(row);
			}
			executeQuery.close();
		} catch (SQLException e) {
			LOG.error("No valid query build.", e);
		}
		return metadata;
	}

	private List<Map<String, Object>> filterHouseNo(
			List<Map<String, Object>> result, String message) {

		List<Map<String, Object>> filtered = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> metadata : result) {
			Integer houseNumber = findHouseNumber(message, "street", metadata);
			if (houseNumber != null) {
				metadata.put("housenumber", houseNumber);
				filtered.add(metadata);
			} else {
				houseNumber = findHouseNumber(message, "postcode", metadata);
			}
			if (houseNumber != null) {
				metadata.put("housenumber", houseNumber);
				filtered.add(metadata);
			}

		}
		return filtered;
	}

	public Integer findHouseNumber(String message, String metadataColumn,
			Map<String, Object> metadata) {

		message = message.toLowerCase();
		String metadataColumnValue = (String) metadata.get(metadataColumn);
		metadataColumnValue = metadataColumnValue.toLowerCase();
		int charIndexHouseNumber = message.indexOf(metadataColumnValue);
		if (charIndexHouseNumber > 0) {
			int start = charIndexHouseNumber + metadataColumnValue.length();
			while (message.length() > start && message.charAt(start) == ' ') {
				start++;
			}
			String number = "";
			while (message.length() > start
					&& Character.isDigit(message.charAt(start))) {
				number += message.charAt(start++);
			}

			if (number != ""
					&& !String.valueOf(metadata.get("pnum")).equals(number)) {
				int houseNumber = Integer.valueOf(number);
				boolean isEven = houseNumber % 2 == 0;
				if ("even".equals(metadata.get("numbertype")) == isEven
						|| "mixed".equals(metadata.get("numbertype"))) {
					int minnumber = (int) metadata.get("minnumber");
					int maxnumber = (int) metadata.get("maxnumber");
					if (houseNumber >= minnumber && houseNumber <= maxnumber) {
						return houseNumber;
					}
				}
			}
		}
		return null;
	}

	private void findMetaDataBy(DecomposeMethod method, Message message,
			List<Map<String, Object>> metadata) throws SQLException {

		PostcodeQueryBuilder queryBuilder = null;
		switch (method) {
		case POSTCODE:
			queryBuilder = newPostcodeQuery();
			Matcher matcher = PATTERN_POSTCODE.matcher(message.getMessage());
			if (matcher.find()) {
				queryBuilder = queryBuilder.mapColumn("postcode",
						MatchType.EXACT, matcher.group(0));
			} else {
				queryBuilder = null;
			}
			break;
		case STREETCITY:
			queryBuilder = newPostcodeQuery().mapColumn("street",
					MessageSource.MESSAGE, message, MatchType.LIKE).and(
					newPostcodeQuery()
							.mapColumn("city", MessageSource.MESSAGE, message,
									MatchType.LIKE)
							.or()
							.mapColumn("municipality", MessageSource.MESSAGE,
									message, MatchType.LIKE));
			break;
		case STREETSECTOR:
			if (message.getGroup().isEmpty()) {
				break;
			}
			queryBuilder = newPostcodeQuery()
					.mapColumn("street", MessageSource.MESSAGE, message,
							MatchType.LIKE)
					.and()
					.mapColumn("city", MessageSource.SECTOR, message,
							MatchType.EXACT);
			break;
		case STREETPARTIALPOSTCODE:
			queryBuilder = newPostcodeQuery()
					.mapColumn("street", MessageSource.MESSAGE, message,
							MatchType.LIKE)
					.and()
					.mapColumn("pnum", MessageSource.MESSAGE, message,
							MatchType.LIKE);
			break;
		default:
			break;
		}

		if (queryBuilder != null) {
			PostcodeQuery query = queryBuilder.get();

			List<Map<String, Object>> result = executeQuery(query);
			List<Map<String, Object>> filtered = filterHouseNo(result,
					message.getMessage());

			if (!filtered.isEmpty()) {
				LOG.debug(
						"Found {} metadata rows for method {} for message {}.",
						filtered.size(), method, message);
				result = filtered;
			}

			if (!result.isEmpty()) {
				LOG.debug(
						"Found {} metadata rows for method {} for message {}.",
						filtered.size(), method, message);
				metadata.addAll(result);
			}
		}
	}
}
