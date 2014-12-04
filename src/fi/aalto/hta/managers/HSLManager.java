package fi.aalto.hta.managers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;

public class HSLManager {
	private static final String requestRoot = "http://api.reittiopas.fi/hsl/prod/?request=";
	private static final String requestRoute = "route";
	private static final String requestGeocode = "geocode";
	private static final String requestReverseGeocode = "reverse_geocode";
//	public static final int TIME_TYPE_DEPARTURE = 0;
//	public static final int TIME_TYPE_ARRIVAL = 1;

	public static final String VEH_TYPE_BUS = "bus";
	public static final String VEH_TYPE_TRAIN = "train";
	public static final String VEH_TYPE_METRO = "metro";
	public static final String VEH_TYPE_TRAM = "tram";
	public static final String VEH_TYPE_FERRY = "ferry";
	public static final String VEH_TYPE_WALK = "walk";
	
	public static final String TIME_TYPE_DEPARTURE = "departure";
	public static final String TIME_TYPE_ARRIVAL = "arrival";

	public static final String OPT_TYPE_DEFAULT = "default"; 
	public static final String OPT_TYPE_FASTEST = "fastest"; 
	public static final String OPT_TYPE_LEAST_TRANSFERS = "least_transfers"; 
	public static final String OPT_TYPE_LEAST_WALKING = "least_walking"; 

	public static final String PARAM_DEP_LOC_STRING = "stringLocationDep";
	public static final String PARAM_DEST_LOC_STRING = "stringLocationDest";
	public static final String PARAM_DEP_LOC = "locationDep";
	public static final String PARAM_DEST_LOC = "locationDest";
	
	public static final String PARAM_TIME_HOUR = "timeHour";
	public static final String PARAM_TIME_MINUTES = "timeMinutes";
	
	public static final String PARAM_DATE_DAY = "dateDay";
	public static final String PARAM_DATE_MONTH = "dateMonth";
	public static final String PARAM_DATE_YEAR = "dateYear";
	
	public static final String PARAM_TIME_TYPE = "timeType";
	public static final String PARAM_TRANSPORT_TYPES = "transportTypes";
	public static final String PARAM_OPTIMIZE_TYPE = "optimize";


	private static void setBasicParameters(Builder builder){
		builder.appendQueryParameter("epsg_in", "wgs84");
		builder.appendQueryParameter("epsg_out", "wgs84");
		builder.appendQueryParameter("detail", "full");
		builder.appendQueryParameter("user", "htaxiqing");
		builder.appendQueryParameter("pass", "NASeMerE");
	}

//	public static String getRouteRequestString(Bundle paramBundle) throws ParseException{
//		Builder builder = Uri.parse(requestRoot + requestRoute).buildUpon();
//		setBasicParameters(builder);
//		if(paramBundle.containsKey(PARAM_DEP_LOC_STRING)){
//			builder.appendQueryParameter("from", paramBundle.getString(PARAM_DEP_LOC_STRING));
//		}else if(paramBundle.containsKey(PARAM_DEP_LOC)){
//			Location locationDep = paramBundle.getParcelable(PARAM_DEP_LOC);
//			builder.appendQueryParameter("from", locationDep.getLongitude() + "," + locationDep.getLatitude());
//		}
//
//		if(paramBundle.containsKey(PARAM_DEST_LOC_STRING)){
//			builder.appendQueryParameter("to", paramBundle.getString(PARAM_DEST_LOC_STRING));
//		}else if(paramBundle.containsKey(PARAM_DEST_LOC)){
//			Location locationDest = paramBundle.getParcelable(PARAM_DEST_LOC);
//			builder.appendQueryParameter("to", locationDest.getLongitude() + "," + locationDest.getLatitude());
//		}else{
//		}
//
//		if(paramBundle.containsKey(PARAM_TIME)){
//			builder.appendQueryParameter("time", paramBundle.getString(PARAM_TIME));
//			if(paramBundle.containsKey(PARAM_TIME_TYPE)){
//				int timeType = paramBundle.getInt(PARAM_TIME_TYPE);
//				if(timeType == TIME_TYPE_ARRIVAL){
//					builder.appendQueryParameter("timetype", "arrival");
//				}
//			}
//		}
//
//		if(paramBundle.containsKey(PARAM_DATE)){
//			String date = paramBundle.getString(PARAM_DATE);
//			// check date:
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			Date checkDate = sdf.parse(date);
//			builder.appendQueryParameter("date", date);
//		}
//		if(paramBundle.containsKey(PARAM_TRANSPORT_TYPES)){
//			String[] transportTypes = paramBundle.getStringArray(PARAM_TRANSPORT_TYPES);
//			StringBuilder types = new StringBuilder("");
//			for(String type: transportTypes){
//				if(types.length() != 0){ // already contains some type
//					types.append("|");
//				}
//				types.append(type);
//			}
//			if(types.length() > 0){
//				builder.appendQueryParameter("transport_types", types.toString());
//			}
//		}
//		if(paramBundle.containsKey(PARAM_OPTIMIZE_TYPE)){
//			int optimization = paramBundle.getInt(PARAM_OPTIMIZE_TYPE);
//			String nativeType = "";
//			switch (optimization) {
//			case 1:
//				nativeType = OPT_TYPE_FASTEST;
//				break;
//			case 2:
//				nativeType = OPT_TYPE_LEAST_TRANSFERS;
//				break;
//			case 3:
//				nativeType = OPT_TYPE_LEAST_WALKING;
//				break;
//			default:
//				nativeType = OPT_TYPE_DEFAULT;
//				break;
//			}
//			builder.appendQueryParameter("optimize", nativeType);
//		}
//
//		Log.i("HSLMan", builder.toString());
//		return builder.toString();
//	}

	public static String getReverseGeocodeString(Location location){
		Builder builder = Uri.parse(requestRoot + requestReverseGeocode).buildUpon();
		builder.appendQueryParameter("coordinate", location.getLongitude() + "," + location.getLatitude());
		setBasicParameters(builder);
		Log.i("HSLMan", builder.toString());
		return builder.toString();
	}

	public static String getPositionGeocodeString(String addressDep){
		Builder builder = Uri.parse(requestRoot + requestGeocode).buildUpon();
		builder.appendQueryParameter("key", addressDep);
		setBasicParameters(builder);
		Log.i("HSLMan", builder.toString());
		return builder.toString();
	}


	public static String getRouteCode(String type, String rawCode){
		if(rawCode == null){
			return null;
		}
		String shownCode = "";
		if(type.equals("2")){
			shownCode = rawCode.substring(4,5);
		}else if(type.equals("6")){
			shownCode = "Metro";
		}else if(type.equals("12")){
			shownCode = rawCode.substring(4,5);
		}else{
			shownCode = rawCode.substring(1, 5);
			shownCode = shownCode.replaceAll("^0+", "");
			shownCode = shownCode.trim();
		}
		return shownCode;
	}

}
