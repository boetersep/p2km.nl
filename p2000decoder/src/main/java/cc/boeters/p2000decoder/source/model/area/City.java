package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

public class City extends Area<City> {

	private Integer id;

	private String name;

	private Municipality municipality;

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

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("name", name).toString();
	}

	@Override
	public List<City> getSubAreas() {
		return new ArrayList<City>();
	}

}
