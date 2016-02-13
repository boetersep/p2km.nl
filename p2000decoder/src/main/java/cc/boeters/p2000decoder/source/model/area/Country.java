package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import jersey.repackaged.com.google.common.base.MoreObjects;

public class Country extends Area<Province> {

	private Integer id;

	private String name;

	private List<Province> provinces;

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

	public Country() {
		provinces = new ArrayList<Province>();
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("provinces", provinces).toString();
	}

	public Province getProvinceById(Integer id) {
		for (Province province : provinces) {
			if (id.equals(province.getId())) {
				return province;
			}
		}
		return null;
	}

	@Override
	public List<Province> getSubAreas() {
		return getProvinces();
	}

}
