package fi.aalto.hta.vos;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VOLeg implements Parcelable{
	private double length;
	private double duration;
	private String type;
	private String code;
	private List<VOLocation> locs;
	private List<VOCoordinates> shape;

	public static final Parcelable.Creator<VOLeg> CREATOR  = new Parcelable.Creator<VOLeg>() {

		public VOLeg createFromParcel(Parcel in) {
			return new VOLeg(in);
		}

		public VOLeg[] newArray(int size) {
			return new VOLeg[size];
		}
	};

	public VOLeg() {}
	public VOLeg(Parcel in) {
		length = in.readDouble();
		duration = in.readDouble();
		type = in.readString();
		code = in.readString();
		locs = new ArrayList<VOLocation>();
		in.readList(locs, VOLocation.class.getClassLoader());
		shape = new ArrayList<VOCoordinates>();
		in.readList(shape, VOCoordinates.class.getClassLoader());

	}

	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<VOLocation> getLocs() {
		return locs;
	}
	public void setLocs(List<VOLocation> locs) {
		this.locs = locs;
	}
	public List<VOCoordinates> getShape() {
		return shape;
	}
	public void setShape(List<VOCoordinates> shape) {
		this.shape = shape;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(length);
		dest.writeDouble(duration);
		dest.writeString(type);
		dest.writeString(code);
		dest.writeList(locs);
		dest.writeList(shape);

	}




}
