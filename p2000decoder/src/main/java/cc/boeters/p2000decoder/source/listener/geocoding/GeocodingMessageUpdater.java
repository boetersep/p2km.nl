package cc.boeters.p2000decoder.source.listener.geocoding;

import static cc.boeters.p2000decoder.source.listener.geocoding.HectopaalQueryBuilder.newHectopaalQuery;
import static cc.boeters.p2000decoder.source.listener.geocoding.PostcodeQueryBuilder.newPostcodeQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.source.listener.MessageUpdater;
import cc.boeters.p2000decoder.source.listener.geocoding.QueryBuilder.MatchType;
import cc.boeters.p2000decoder.source.listener.geocoding.QueryBuilder.MessageSource;
import cc.boeters.p2000decoder.source.listener.geocoding.QueryBuilder.Query;
import cc.boeters.p2000decoder.source.model.message.Message;

public class GeocodingMessageUpdater extends MessageUpdater {

	public GeocodingMessageUpdater(DataSource dataSource, MongoDatabase db, WebSocketServerFactory webSocketFactory) {
		super(dataSource, db, webSocketFactory);
	}

	enum GeocodingMethod {
		POSTCODE, ROAD_CITY_STREET, ROAD_CITY_STREET_RPE, ROAD_HECTO, ROAD_HECTO_RPE, STREETCAPCODE, STREETCITY, STREETPARTIALPOSTCODE, STREETSECTOR;

		public static final List<GeocodingMethod> ORDERED = Arrays.asList(POSTCODE, STREETCITY, STREETCAPCODE,
				STREETSECTOR, STREETPARTIALPOSTCODE, ROAD_HECTO_RPE, ROAD_HECTO, ROAD_CITY_STREET_RPE,
				ROAD_CITY_STREET);
	}

	static final Logger LOG = LoggerFactory.getLogger(GeocodingMessageUpdater.class);

	private static final Pattern PATTERN_POSTCODE = Pattern.compile("([1-9]{1}[0-9]{3}[A-Z]{2})");

	private double succesful;

	private double total;

	@Override
	public Map<String, Object> getUpdateData(Message message) {
		Map<String, Object> decomposed = new HashMap<String, Object>(2);
		total++;

		List<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		for (GeocodingMethod method : GeocodingMethod.ORDERED) {
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
			decomposed.put("source", metadata);
			if (metadata.get(0).containsKey("lat") && metadata.get(0).containsKey("lon")) {
				Map<String, Double> location = new HashMap<String, Double>(2);
				location.put("lat", (Double) metadata.get(0).get("lat"));
				location.put("lon", (Double) metadata.get(0).get("lon"));
				decomposed.put("location", location);
			}
		}

		if (total % 100 == 0) {
			LOG.info("Percent decomposed: " + ((succesful / total) * 100.0));
		}

		return decomposed;
	}

	private List<Map<String, Object>> executeQuery(Query query) {
		List<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		try {
			Connection connection = getDataSource().getConnection();
			PreparedStatement prepareStatement = connection.prepareStatement(query.getQuery());
			prepareStatement.closeOnCompletion();
			for (int index = 0; index < query.getValues().size(); index++) {
				prepareStatement.setString(index + 1, query.getValues().get(index));
			}
			ResultSet executeQuery = prepareStatement.executeQuery();
			while (executeQuery.next()) {
				ResultSetMetaData rowMetadata = executeQuery.getMetaData();
				Map<String, Object> row = new HashMap<String, Object>();
				for (int i = 0; i < rowMetadata.getColumnCount(); i++) {
					row.put(rowMetadata.getColumnName(i + 1), executeQuery.getObject(i + 1));
				}
				metadata.add(row);
			}
			executeQuery.close();
			connection.close();
		} catch (SQLException e) {
			LOG.error("No valid query build.", e);
		}
		return metadata;
	}

	private List<Map<String, Object>> filterHouseNo(List<Map<String, Object>> result, String message) {

		List<Map<String, Object>> filtered = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> metadata : result) {
			Integer houseNumber = findHouseNumber(message, "street", metadata);
			if (houseNumber == null) {
				houseNumber = findHouseNumber(message, "postcode", metadata);
			}
			if (houseNumber != null) {
				metadata.put("housenumber", houseNumber);
				filtered.add(metadata);
			}
		}
		return filtered;
	}

	public Integer findHouseNumber(String message, String metadataColumn, Map<String, Object> metadata) {

		if (!metadata.containsKey(metadataColumn)) {
			return null;
		}

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
			while (message.length() > start && Character.isDigit(message.charAt(start))) {
				number += message.charAt(start++);
			}

			if (number != "" && !String.valueOf(metadata.get("pnum")).equals(number)) {
				int houseNumber = Integer.valueOf(number);
				boolean isEven = houseNumber % 2 == 0;
				if ("even".equals(metadata.get("numbertype")) == isEven || "mixed".equals(metadata.get("numbertype"))) {
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

	private void findMetaDataBy(GeocodingMethod method, Message message, List<Map<String, Object>> metadata)
			throws SQLException {

		QueryBuilder queryBuilder = null;
		switch (method) {
		case POSTCODE:
			queryBuilder = newPostcodeQuery();
			Matcher matcher = PATTERN_POSTCODE.matcher(message.getMessage());
			if (matcher.find()) {
				queryBuilder = queryBuilder.mapColumn("postcode", MatchType.EXACT, matcher.group(0));
			} else {
				queryBuilder = null;
			}
			break;
		case STREETCITY:
			queryBuilder = newPostcodeQuery().mapColumn("street", MessageSource.MESSAGE, message, MatchType.LIKE)
					.and(newPostcodeQuery().mapColumn("city", MessageSource.MESSAGE, message, MatchType.LIKE).or()
							.mapColumn("municipality", MessageSource.MESSAGE, message, MatchType.LIKE));
			break;
		case STREETSECTOR:
			if (message.getGroup().isEmpty()) {
				break;
			}
			queryBuilder = newPostcodeQuery().mapColumn("street", MessageSource.MESSAGE, message, MatchType.LIKE).and()
					.mapColumn("city", MessageSource.SECTOR, message, MatchType.EXACT);
			break;
		case STREETPARTIALPOSTCODE:
			queryBuilder = newPostcodeQuery().mapColumn("street", MessageSource.MESSAGE, message, MatchType.LIKE).and()
					.mapColumn("pnum", MessageSource.MESSAGE, message, MatchType.LIKE);
			break;
		case ROAD_HECTO_RPE:
			queryBuilder = newHectopaalQuery().mapColumn("weg", MessageSource.MESSAGE, message, MatchType.LIKE)
					.and(newHectopaalQuery()
							.mapColumn("hectometrering_comma", MessageSource.MESSAGE, message, MatchType.LIKE).or()
							.mapColumn("hectometrering_dot", MessageSource.MESSAGE, message, MatchType.LIKE))
					.and().mapColumn("rpe_code", MessageSource.MESSAGE, message, MatchType.LIKE);
			break;
		case ROAD_HECTO:
			queryBuilder = newHectopaalQuery().mapColumn("weg", MessageSource.MESSAGE, message, MatchType.LIKE)
					.and(newHectopaalQuery()
							.mapColumn("hectometrering_comma", MessageSource.MESSAGE, message, MatchType.LIKE).or()
							.mapColumn("hectometrering_dot", MessageSource.MESSAGE, message, MatchType.LIKE));
			break;
		case ROAD_CITY_STREET_RPE:
			queryBuilder = newHectopaalQuery().mapColumn("weg", MessageSource.MESSAGE, message, MatchType.LIKE)
					.and(newHectopaalQuery().mapColumn("city", MessageSource.MESSAGE, message, MatchType.LIKE).or()
							.mapColumn("street", MessageSource.MESSAGE, message, MatchType.LIKE))
					.and().mapColumn("rpe_code", MessageSource.MESSAGE, message, MatchType.LIKE);
			;
			break;
		case ROAD_CITY_STREET:
			queryBuilder = newHectopaalQuery().mapColumn("weg", MessageSource.MESSAGE, message, MatchType.LIKE)
					.and(newHectopaalQuery().mapColumn("city", MessageSource.MESSAGE, message, MatchType.LIKE).or()
							.mapColumn("street", MessageSource.MESSAGE, message, MatchType.LIKE));
			break;
		default:
			break;
		}

		if (queryBuilder != null) {
			Query query = queryBuilder.get();

			List<Map<String, Object>> result = executeQuery(query);
			List<Map<String, Object>> filtered = filterHouseNo(result, message.getMessage());

			if (!filtered.isEmpty()) {
				LOG.debug("Found {} metadata rows for method {} for message {}.", filtered.size(), method, message);
				result = filtered;
			}

			if (!result.isEmpty()) {
				LOG.debug("Found {} metadata rows for method {} for message {}.", filtered.size(), method, message);
				metadata.addAll(result);
			}
		}
	}

	@Override
	public String getName() {
		return "geodata";
	}
}
