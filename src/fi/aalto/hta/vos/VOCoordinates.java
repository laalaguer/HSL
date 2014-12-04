package fi.aalto.hta.vos;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class VOCoordinates implements Parcelable{
	private double x, y;

	public static final Parcelable.Creator<VOCoordinates> CREATOR  = new Parcelable.Creator<VOCoordinates>() {

		public VOCoordinates createFromParcel(Parcel in) {
			return new VOCoordinates(in);
		}

		public VOCoordinates[] newArray(int size) {
			return new VOCoordinates[size];
		}
	};
	
	public VOCoordinates() {}
	public VOCoordinates(Parcel in) {
		x = in.readDouble();
		y = in.readDouble();
	}
	
	public VOCoordinates(Location loc){
		this.x = loc.getLatitude();
		this.y = loc.getLongitude();
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
	}
	
	public Location toLocation(){
		Location loc = new Location("");
		loc.setLatitude(y);
		loc.setLongitude(x);
		return loc;
	}
}
