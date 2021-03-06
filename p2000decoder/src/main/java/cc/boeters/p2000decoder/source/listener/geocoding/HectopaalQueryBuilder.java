package cc.boeters.p2000decoder.source.listener.geocoding;

public class HectopaalQueryBuilder extends QueryBuilder {

	public static HectopaalQueryBuilder newHectopaalQuery() {
		return new HectopaalQueryBuilder();
	}

	@Override
	protected String createLikeClause(String column) {
		if ("hectometrering_comma".equals(column)) {
			return "LIKE CONCAT('% ', FORMAT(hectometrering / 10, 1, 'de_DE') ,'%')";
		}
		if ("hectometrering_dot".equals(column)) {
			return "LIKE CONCAT('% ', FORMAT(hectometrering / 10, 1) ,'%')";
		}
		return "LIKE CONCAT('% ', " + column + " ,' %')";
	}

	@Override
	public Query get() {
		query.insert(0, "SELECT * FROM hectopunten WHERE");
		return new Query(query.toString(), values);
	}

	@Override
	protected String sanitizeMessage(String msg) {
		msg = msg.replace(";", " ");
		msg = msg.replace("/", " ");
		msg = msg.replace("\\", " ");
		msg = msg.replace("+", " ");
		msg = msg.replace(" LINKS ", " L ");
		msg = msg.replace(" RECHTS ", " R ");
		return " " + msg + " ";
	}

}
