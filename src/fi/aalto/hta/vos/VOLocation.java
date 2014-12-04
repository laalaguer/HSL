package fi.aalto.hta.vos;

import android.os.Parcel;
import android.os.Parcelable;

public class VOLocation  implements Parcelable{
	private VOCoordinates coord;
	private String arrTime;
	private String depTime;
	private String name;
	private String code;
	private String shortCode;
	private String stopAddress;
	
	public static final Parcelable.Creator<VOLocation> CREATOR  = new Parcelable.Creator<VOLocation>() {

		public VOLocation createFromParcel(Parcel in) {
			return new VOLocation(in);
		}

		public VOLocation[] newArray(int size) {
			return new VOLocation[size];
		}
	};
	
	public VOLocation() {}
	public VOLocation(Parcel in) {
		coord = in.readParcelable(VOCoordinates.class.getClassLoader());
		arrTime = in.readString();
		depTime = in.readString();
		name = in.readString();
		code = in.readString();
		shortCode = in.readString();
		stopAddress = in.readString();	
	}
	
	public VOCoordinates getCoord() {
		return coord;
	}
	public void setCoord(VOCoordinates coord) {
		this.coord = coord;
	}
	public String getArrTime() {
		return arrTime;
	}
	public void setArrTime(String arrTime) {
		this.arrTime = arrTime;
	}
	public String getDepTime() {
		return depTime;
	}
	public void setDepTime(String depTime) {
		this.depTime = depTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getStopAddress() {
		return stopAddress;
	}
	public void setStopAddress(String stopAddress) {
		this.stopAddress = stopAddress;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(coord, flags);
		dest.writeString(arrTime);
		dest.writeString(depTime);
		dest.writeString(name);
		dest.writeString(code);
		dest.writeString(shortCode);
		dest.writeString(stopAddress);
	
	}
	
	
}
