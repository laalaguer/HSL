package fi.aalto.hta.vos;

public class VOStopInfo {
	private int code;
	private String code_short;
	private String	name_fi, name_sv;
	private String city_fi, city_sv;
	private VOCoordinates coords;
	private VOCoordinates wgs_coords;
	private String address_fi, address_sv;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getCode_short() {
		return code_short;
	}
	public void setCode_short(String code_short) {
		this.code_short = code_short;
	}
	public String getName_fi() {
		return name_fi;
	}
	public void setName_fi(String name_fi) {
		this.name_fi = name_fi;
	}
	public String getName_sv() {
		return name_sv;
	}
	public void setName_sv(String name_sv) {
		this.name_sv = name_sv;
	}
	public String getCity_fi() {
		return city_fi;
	}
	public void setCity_fi(String city_fi) {
		this.city_fi = city_fi;
	}
	public String getCity_sv() {
		return city_sv;
	}
	public void setCity_sv(String city_sv) {
		this.city_sv = city_sv;
	}
	public VOCoordinates getCoords() {
		return coords;
	}
	public void setCoords(VOCoordinates coords) {
		this.coords = coords;
	}
	public VOCoordinates getWgs_coords() {
		return wgs_coords;
	}
	public void setWgs_coords(VOCoordinates wgs_coords) {
		this.wgs_coords = wgs_coords;
	}
	public String getAddress_fi() {
		return address_fi;
	}
	public void setAddress_fi(String address_fi) {
		this.address_fi = address_fi;
	}
	public String getAddress_sv() {
		return address_sv;
	}
	public void setAddress_sv(String address_sv) {
		this.address_sv = address_sv;
	}
	
	
}
