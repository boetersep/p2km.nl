package cc.boeters.p2000monitor.model;

public class CapcodeInfo {

	private int capcode;

	private String discipline, region, sector, description, shortdesc;

	public int getCapcode() {
		return capcode;
	}

	public String getDescription() {
		return description;
	}

	public String getDiscipline() {
		return discipline;
	}

	public String getRegion() {
		return region;
	}

	public String getSector() {
		return sector;
	}

	public String getShortdesc() {
		return shortdesc;
	}

	public void setCapcode(int capcode) {
		this.capcode = capcode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public void setShortdesc(String shortdesc) {
		this.shortdesc = shortdesc;
	}

	@Override
	public String toString() {
		return "CapcodeInfo [capcode=" + capcode + ", discipline=" + discipline
				+ ", region=" + region + ", sector=" + sector
				+ ", description=" + description + ", shortdesc=" + shortdesc
				+ "]";
	}

}
