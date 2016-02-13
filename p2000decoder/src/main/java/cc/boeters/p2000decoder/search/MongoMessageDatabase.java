package cc.boeters.p2000decoder.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import cc.boeters.p2000decoder.source.model.area.City;
import cc.boeters.p2000decoder.source.model.area.Municipality;
import cc.boeters.p2000decoder.source.model.area.Province;

public class MongoMessageDatabase implements MessageDatabase {

	private final MongoDatabase database;

	public MongoMessageDatabase(MongoDatabase database) {
		this.database = database;
	}

	@Override
	public Map<String, Object> find(Integer capcode, Long timestamp) {
		FindIterable<Document> messages = database.getCollection("messages")
				.find(Filters.and(Filters.eq("capcode", capcode), Filters.eq("timestamp", timestamp)));
		return messages.first();
	}

	@Override
	public List<Map<String, Object>> findByProvince(Province province) {
		return findMessageByArea(province.getName(), "geodata.source.province");
	}

	private List<Map<String, Object>> findMessageByArea(Object value, String fieldName) {
		List<Map<String, Object>> returnResult = new ArrayList<Map<String, Object>>();
		MongoCursor<Document> iterator = database.getCollection("messages").find(Filters.eq(fieldName, value)).limit(10)
				.sort(new Document("timestamp", -1)).iterator();
		while (iterator.hasNext()) {
			returnResult.add(iterator.next());
		}
		return returnResult;
	}

	@Override
	public List<Map<String, Object>> findByMunicipality(Municipality municipality) {
		return findMessageByArea(municipality.getId(), "geodata.source.municipality_id");
	}

	@Override
	public List<Map<String, Object>> findByCity(City city) {
		return findMessageByArea(city.getId(), "geodata.source.city_id");

	}

}
