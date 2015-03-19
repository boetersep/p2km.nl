package cc.boeters.p2000monitor.archive;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.model.CapcodeInfo;
import cc.boeters.p2000monitor.model.Message;

public class PostcodeQueryBuilder {

	public enum MatchType {
		LIKE, EXACT;
	}

	public enum MessageSource {
		MESSAGE(false), REGION(true), SECTOR(true), DESCRIPTION(true);

		private final boolean multiple;

		private MessageSource(boolean multiple) {
			this.multiple = multiple;
		}

		public boolean isMultiple() {
			return multiple;
		}

	}

	public class PostcodeQuery {

		private final String query;
		private final List<String> values;

		public PostcodeQuery(String query, List<String> values) {
			this.query = query;
			this.values = values;
		}

		public String getQuery() {
			return query;
		}

		public List<String> getValues() {
			return values;
		}

		@Override
		public String toString() {
			String sql = query.toString();
			for (String string : values) {
				sql = sql.replaceFirst("\\?", "'" + string + "'");
			}
			return sql;
		}

	}

	public static PostcodeQueryBuilder newPostcodeQuery() {
		return new PostcodeQueryBuilder();
	}

	static final Logger LOG = LoggerFactory
			.getLogger(PostcodeQueryBuilder.class);

	private final List<String> values = new ArrayList<String>();
	private final StringBuilder query;

	private int lastOpenJunction;

	private PostcodeQueryBuilder() {
		query = new StringBuilder();
	}

	public PostcodeQueryBuilder and(PostcodeQueryBuilder... builder) {
		if (builder != null && builder.length > 0) {
			for (PostcodeQueryBuilder postcodeQueryBuilder : builder) {
				query.append(" AND (");
				query.append(postcodeQueryBuilder.query);
				values.addAll(postcodeQueryBuilder.values);
				query.append(" )");
			}
		} else {
			lastOpenJunction = query.length();
			query.append(" AND");
		}
		return this;
	}

	public PostcodeQuery get() {
		query.insert(0, "SELECT * FROM postcode WHERE");
		return new PostcodeQuery(query.toString(), values);
	}

	public PostcodeQueryBuilder mapColumn(String column, MatchType matchType,
			String value) {

		if (matchType == MatchType.LIKE) {
			query.append(" " + column);
			query.append(" LIKE ?");
			values.add("%" + value + "%");
		} else {
			query.append(" " + column);
			query.append(" = ?");
			values.add(value);
		}
		return this;
	}

	public PostcodeQueryBuilder mapColumn(String column,
			MessageSource messageSource, Message message, MatchType matchType) {

		if (messageSource.isMultiple()) {
			List<CapcodeInfo> group = message.getGroup();
			if (!group.isEmpty()) {
				query.append(" (");
				for (CapcodeInfo capcodeInfo : group) {
					switch (messageSource) {
					case DESCRIPTION:
						values.add(capcodeInfo.getDescription());
						break;
					case REGION:
						values.add(capcodeInfo.getRegion());
						break;
					case SECTOR:
						values.add(capcodeInfo.getSector());
						break;
					default:
						break;
					}
					if (matchType == MatchType.LIKE) {
						query.append(" ? LIKE CONCAT('% ', " + column
								+ " ,' %') OR");
					} else {
						query.append(" ? = " + column + " OR");
					}
				}
				query.delete(query.length() - 3, query.length());
				query.append(" )");
			} else {
				query.delete(lastOpenJunction, query.length());
			}
		} else {
			query.append(" (");

			switch (messageSource) {
			case MESSAGE:
				values.add(sanitizeMessage(message.getMessage()));
				break;
			default:
				break;
			}
			if (matchType == MatchType.LIKE) {
				query.append(" ? LIKE CONCAT('% ', " + column + " ,' %')");
			} else {
				query.append(" ? = " + column);
			}
			query.append(" )");

		}

		return this;
	}

	public PostcodeQueryBuilder mapValue(String value, MatchType matchType,
			String column) {

		if (matchType == MatchType.LIKE) {
			query.append(" ? ");
			query.append(" LIKE CONCAT('% ', " + column + " ,' %')");
			values.add(value);
		} else {
			query.append(" " + column);
			query.append(" = ?");
			values.add(value);
		}
		return this;
	}

	public PostcodeQueryBuilder or(PostcodeQueryBuilder... builder) {
		if (builder != null && builder.length > 0) {
			for (PostcodeQueryBuilder postcodeQueryBuilder : builder) {
				query.append(" OR (");
				query.append(postcodeQueryBuilder.query);
				values.addAll(postcodeQueryBuilder.values);
				query.append(" )");
			}
		} else {
			lastOpenJunction = query.length();
			query.append(" OR");
		}
		return this;
	}

	private String sanitizeMessage(String msg) {
		msg = msg.replace(";", " ");
		msg = msg.replace(",", " ");
		msg = msg.replace(".", " ");
		msg = msg.replace("/", " ");
		msg = msg.replace("\\", " ");
		msg = msg.replace("+", " ");
		return " " + msg + " ";
	}

}
