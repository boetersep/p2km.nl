package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Municipality extends Area<City> {

	@XmlID
	@XmlAttribute
	@XmlJavaTypeAdapter(AreaIdAdapter.class)
	private Integer id;

	@XmlAttribute
	private String name;

	@XmlAttribute
	@XmlIDREF
	private Province province;

	@JsonIgnore
	@XmlElement(name = "city")
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

	@JsonIgnore
	public List<City> getCities() {
		return cities;
	}

	@JsonProperty
	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	@Override
	public Area<?> getCoveringArea() {
		return getProvince();
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
