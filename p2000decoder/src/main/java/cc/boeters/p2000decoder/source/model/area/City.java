package cc.boeters.p2000decoder.source.model.area;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
public class City extends Area<City> {

	@XmlID
	@XmlAttribute
	@XmlJavaTypeAdapter(AreaIdAdapter.class)
	private Integer id;

	@XmlAttribute
	private String name;

	@XmlAttribute
	@XmlIDREF
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

	@Override
	public Area<?> getCoveringArea() {
		return getMunicipality();
	}

}
