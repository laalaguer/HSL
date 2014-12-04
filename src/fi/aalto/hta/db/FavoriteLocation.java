package fi.aalto.hta.db;

import android.graphics.Bitmap;

public class FavoriteLocation {
	public int _id;
	public double latitude;
	public double longitude;
	public String street_info;
	public String description;
	public int type;
	public Bitmap image;
	public String title;
	
	public static class TYPE {static int BAR=1, HOME=2,SCHOOL=3,FRIEND=4,GENERAL=5;}
	
	public FavoriteLocation(){
		latitude = 0.0;
		longitude = 0.0;
		title = "not available";
		street_info = "not available";
		description = "not available";
		type = TYPE.GENERAL;
	}
	
	public FavoriteLocation(double lat,double lon,String street, String des,int type, Bitmap image, String title){
		this.street_info = street;
		this.description = des;
		this.type = type;
		this.latitude = lat;
		this.longitude = lon;
		this.image = image;
		this.title = title;
	}
	
	public String getGeo(){
		return "("+ this.latitude + "," + this.longitude+")";
	}
	public String getGeoLngLat(){
		// return lat,long pairs, with a "," in middle
		return this.longitude+","+this.latitude;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	public double getLongitude(){
		return this.longitude;
	}
	
	public void setGeo(double x, double y){
		this.latitude = x; 
		this.longitude = y;
	}
	
	public void setStreet(String info){
		this.street_info = info;
	}
	
	public void setDescription(String des){
		this.description = des;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public void setId(int id){
		this._id = id;
	}
	
	public void setImage(Bitmap image){
		this.image = image;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
}
