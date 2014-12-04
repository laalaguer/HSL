package fi.aalto.hta.vos;

public class VOAddress {
	private String locType;
	private int locTypeId;
	private String name, lang, city;
	private String coords;
	public String getLocType() {
		return locType;
	}
	public void setLocType(String locType) {
		this.locType = locType;
	}
	public int getLocTypeId() {
		return locTypeId;
	}
	public void setLocTypeId(int locTypeId) {
		this.locTypeId = locTypeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCoords() {
		return coords;
	}
	public void setCoords(String coords) {
		this.coords = coords;
	}

}
