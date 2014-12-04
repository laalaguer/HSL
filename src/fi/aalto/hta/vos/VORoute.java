package fi.aalto.hta.vos;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VORoute implements Parcelable {
	private double length;
	private double duration;
	private List<VOLeg> legs;

	public static final Parcelable.Creator<VORoute> CREATOR  = new Parcelable.Creator<VORoute>() {

		public VORoute createFromParcel(Parcel in) {
			return new VORoute(in);
		}

		public VORoute[] newArray(int size) {
			return new VORoute[size];
		}
	};
	public VORoute(){}

	public VORoute(Parcel in) {
		length = in.readDouble();
		duration = in.readDouble();
		legs = new ArrayList<VOLeg>();
		in.readList(legs, VOLeg.class.getClassLoader());

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
	public List<VOLeg> getLegs() {
		return legs;
	}
	public void setLegs(List<VOLeg> legs) {
		this.legs = legs;
	}
	@Override
	public int describeContents() {
		// 
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeDouble(length);
		arg0.writeDouble(duration);
		arg0.writeList(legs);

	}

}
