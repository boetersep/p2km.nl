package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Municipality extends Area<City> {

	private Integer id;

	private String name;

	private Province province;

	private List<City> cities;

	public Municipality() {
		cities = new ArrayList<City>();
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public Province getProvince() {
		return province;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("cities", cities).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, name);
	}

	@Override
	public List<City> getSubAreas() {
		return getCities();
	}

}
