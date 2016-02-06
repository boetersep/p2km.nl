package cc.boeters.p2000decoder.source.listener.geocoding;

import java.util.ArrayList;
import java.util.List;

import cc.boeters.p2000decoder.source.model.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.Message;

public abstract class QueryBuilder {

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

	public class Query {

		private final String query;
		private final List<String> values;

		public Query(String query, List<String> values) {
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

	protected final List<String> values = new ArrayList<String>();

	protected final StringBuilder query;

	private int lastOpenJunction;

	protected QueryBuilder() {
		query = new StringBuilder();
	}

	public QueryBuilder and(QueryBuilder... builder) {
		if (builder != null && builder.length > 0) {
			for (QueryBuilder QueryBuilder : builder) {
				query.append(" AND (");
				query.append(QueryBuilder.query);
				values.addAll(QueryBuilder.values);
				query.append(" )");
			}
		} else {
			lastOpenJunction = query.length();
			query.append(" AND");
		}
		return this;
	}

	protected abstract String createLikeClause(String column);

	public abstract Query get();

	public QueryBuilder mapColumn(String column, MatchType matchType, String value) {

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

	public QueryBuilder mapColumn(String column, MessageSource messageSource, Message message, MatchType matchType) {

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
						query.append(" ? " + createLikeClause(column) + " OR");
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
				query.append(" ? " + createLikeClause(column));
			} else {
				query.append(" ? = " + column);
			}
			query.append(" )");

		}

		return this;
	}

	public QueryBuilder mapValue(String value, MatchType matchType, String column) {

		if (matchType == MatchType.LIKE) {
			query.append(" ? ");
			query.append(" " + createLikeClause(column));
			values.add(value);
		} else {
			query.append(" " + column);
			query.append(" = ?");
			values.add(value);
		}
		return this;
	}

	public QueryBuilder or(QueryBuilder... builder) {
		if (builder != null && builder.length > 0) {
			for (QueryBuilder QueryBuilder : builder) {
				query.append(" OR (");
				query.append(QueryBuilder.query);
				values.addAll(QueryBuilder.values);
				query.append(" )");
			}
		} else {
			lastOpenJunction = query.length();
			query.append(" OR");
		}
		return this;
	}

	protected abstract String sanitizeMessage(String msg);

}
