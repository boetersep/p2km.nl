package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

import jersey.repackaged.com.google.common.base.MoreObjects;

public class Province extends Area<Municipality> {

	private Integer id;

	private String name;

	private Country country;

	private List<Municipality> municipalities;

	public Province() {
		municipalities = new ArrayList<Municipality>();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public List<Municipality> getMunicipalities() {
		return municipalities;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setMunicipalities(List<Municipality> municipalities) {
		this.municipalities = municipalities;
	}

	public Country getCountry() {
		return country;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("name", name).add("municipalities", municipalities)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Province other = (Province) obj;
		return Objects.equal(this.id, other.id) && Objects.equal(this.name, other.name);
	}

	public Municipality getMunicipalityById(Integer id) {
		for (Municipality municipality : municipalities) {
			if (id.equals(municipality.getId())) {
				return municipality;
			}
		}
		return null;
	}

	@Override
	public List<Municipality> getSubAreas() {
		return getMunicipalities();
	}

}
