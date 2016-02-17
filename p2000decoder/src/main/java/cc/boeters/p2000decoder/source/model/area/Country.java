package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jersey.repackaged.com.google.common.base.MoreObjects;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Country extends Area<Province> {

	@XmlID
	@XmlAttribute
	@XmlJavaTypeAdapter(AreaIdAdapter.class)
	private Integer id;

	@XmlAttribute
	private String name;

	@JsonIgnore
	@XmlElement(name = "province")
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

	@JsonIgnore
	public List<Province> getProvinces() {
		return provinces;
	}

	@JsonProperty
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

	@Override
	public Area<?> getCoveringArea() {
		return null;
	}

}
