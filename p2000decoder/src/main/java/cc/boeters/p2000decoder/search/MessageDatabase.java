package cc.boeters.p2000decoder.search;

import java.util.List;
import java.util.Map;

import cc.boeters.p2000decoder.source.model.area.City;
import cc.boeters.p2000decoder.source.model.area.Municipality;
import cc.boeters.p2000decoder.source.model.area.Province;

public interface MessageDatabase {

	Map<String, Object> find(Integer capcode, Long timestamp);

	List<Map<String, Object>> findByProvince(Province province);

	List<Map<String, Object>> findByMunicipality(Municipality municipality);

	List<Map<String, Object>> findByCity(City city);

}
