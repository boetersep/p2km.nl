package cc.boeters.p2000decoder.source.listener.geocoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostcodeQueryBuilder extends QueryBuilder {

	public static PostcodeQueryBuilder newPostcodeQuery() {
		return new PostcodeQueryBuilder();
	}

	static final Logger LOG = LoggerFactory
			.getLogger(PostcodeQueryBuilder.class);

	@Override
	protected String createLikeClause(String column) {
		return "LIKE CONCAT('% ', " + column + " ,' %')";
	}

	@Override
	public Query get() {
		query.insert(0, "SELECT * FROM postcode WHERE");
		return new Query(query.toString(), values);
	}

	@Override
	protected String sanitizeMessage(String msg) {
		msg = msg.replace(";", " ");
		msg = msg.replace(",", " ");
		msg = msg.replace(".", " ");
		msg = msg.replace("/", " ");
		msg = msg.replace("\\", " ");
		msg = msg.replace("+", " ");
		return " " + msg + " ";
	}

}
