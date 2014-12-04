package fi.aalto.hta.httpAsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fi.aalto.hta.interfaces.FinishCallBackRoute;
import fi.aalto.hta.vos.VORoute;

public class HttpGeoToRoute extends HttpTransaction{
	private final String TAG = HttpGeoToRoute.class.getName();
	private final String REQUEST_TYPE = "route";
	private final String DETAIL_LEVEL = "&detail=full";
	private final String REQUEST_FROM = "&from=";
	private final String REQUEST_TO = "&to=";
	
	private final String REQUEST_OPTIMIZE = "&optimize=";
	private final String REQUEST_TIME = "&time=";
	private final String REQUEST_DATE = "&date=";
	private final String REQUEST_TIME_TYPE = "&timetype=";
	private final String REQUEST_TRANSPORT_TYPES = "&transport_types=";
	
	public static final String OPT_TYPE_DEFAULT = "default"; 
	public static final String OPT_TYPE_FASTEST = "fastest"; 
	public static final String OPT_TYPE_LEAST_TRANSFERS = "least_transfers"; 
	public static final String OPT_TYPE_LEAST_WALKING = "least_walking"; 
	
	public static final String VEH_TYPE_BUS = "bus";
	public static final String VEH_TYPE_TRAIN = "train";
	public static final String VEH_TYPE_METRO = "metro";
	public static final String VEH_TYPE_TRAM = "tram";
	public static final String VEH_TYPE_FERRY = "ferry";
	public static final String VEH_TYPE_WALK = "walk";
	
	
	
	
	private double latitudeFrom;
	private double latitudeTo;
	private double longitudeFrom;
	private double longitudeTo;
	private String from;
	private String to;
	
	// Additional parameters:
	private String timeType = null;
	private int timeMinutes = 0, timeHour = 0;
	private int dateYear = 0, dateMonth = 0, dateDay = 0;
	private String[] vehicleTypes = null;
	private int optimizationChoice = 0; 
	
	
	
	// Dialog Title
	final static String dialogTitle = "Get Route";
	// Dialog message
	final static String dialogMessage = "Progressing, Please wait";
	// List of Routes
	private String fileContents = null;
	private Collection<List<VORoute>> routeResult = null;
	private ArrayList<VORoute> routes = null;
	// Call back on Activity
	FinishCallBackRoute activity;

	public HttpGeoToRoute(Context mContext,double latitudeFrom, double longitudeFrom,double latitudeTo,double longitudeTo,FinishCallBackRoute activity) {
		super(mContext, dialogTitle, dialogMessage);
		// TODO Auto-generated constructor stub
		this.latitudeFrom = latitudeFrom;
		this.longitudeFrom = longitudeFrom;
		this.latitudeTo = latitudeTo;
		this.longitudeTo = longitudeTo;
		this.activity = activity;
	}
	
	public HttpGeoToRoute(Context mContext,String from, String to, FinishCallBackRoute activity){
		super(mContext, dialogTitle, dialogMessage);
		this.from = from;
		this.to  = to;
		this.activity = activity;
	}

	@Override
	public String buildURIContent(String additionalField) {
		// TODO Auto-generated method stub
		return PREFIX+REQUEST_TYPE_PREFIX+REQUEST_TYPE
				+REQUEST_FROM+this.longitudeFrom+","+this.latitudeFrom
				+REQUEST_TO+this.longitudeTo+","+this.latitudeTo
				+GEO_CODING_TYPE_IN+GEO_CODING_TYPE_OUT
				+DETAIL_LEVEL
				+AUTHENTICATION;
	}
	
	public String buildURIContent() {
		// TODO Auto-generated method stub
//		return PREFIX+REQUEST_TYPE_PREFIX+REQUEST_TYPE
//				+REQUEST_FROM+this.from
//				+REQUEST_TO+this.to
//				+GEO_CODING_TYPE_IN+GEO_CODING_TYPE_OUT
//				+DETAIL_LEVEL
//				+AUTHENTICATION;
		
		StringBuilder builder = new StringBuilder(PREFIX+REQUEST_TYPE_PREFIX+REQUEST_TYPE
				+REQUEST_FROM+this.longitudeFrom+","+this.latitudeFrom
				+REQUEST_TO+this.longitudeTo+","+this.latitudeTo
				+GEO_CODING_TYPE_IN+GEO_CODING_TYPE_OUT
				+DETAIL_LEVEL
				+AUTHENTICATION);
		if(timeType != null){
			builder.append(REQUEST_TIME_TYPE + timeType);
		}
		
		if(timeHour != 0 && timeMinutes != 0){
			builder.append(REQUEST_TIME + String.format("%02d%02d", timeHour, timeMinutes));
		}

		if(dateYear != 0 && dateMonth != 0 && dateDay != 0){
			String date = String.format("%04d%02d%02d", dateYear, dateMonth, dateDay);
			// check:
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			try {
				@SuppressWarnings("unused")
				Date checkDate = sdf.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Wrong date format");
				return null;
			}
			builder.append(REQUEST_DATE + date);
		}
			
		if(vehicleTypes != null){
			StringBuilder types = new StringBuilder("");
			for(String type: vehicleTypes){
				if(types.length() != 0){ // already contains some type
					types.append("|");
				}
				types.append(type);
			}
			if(types.length() > 0){
				builder.append(REQUEST_TRANSPORT_TYPES + Uri.encode(types.toString()));
			}
		}
		
		if(optimizationChoice != 0){
			String nativeType = "";
			switch (optimizationChoice) {
			case 1:
				nativeType = OPT_TYPE_FASTEST;
				break;
			case 2:
				nativeType = OPT_TYPE_LEAST_TRANSFERS;
				break;
			case 3:
				nativeType = OPT_TYPE_LEAST_WALKING;
				break;
			default:
				nativeType = OPT_TYPE_DEFAULT;
				break;
			}
			builder.append(REQUEST_OPTIMIZE + nativeType);
		}

		Log.i(TAG, builder.toString());
		return builder.toString();
	}
	
	public void setTime(int hour, int minutes){
//		time = String.format("%02d%02d", hour, minutes);
		timeHour = hour;
		timeMinutes = minutes;
	}
	
	public void setDate(int year, int month, int day){
//		date = String.format("%04d%02d%02d", year, month, day);
		dateDay = day;
		dateMonth = month;
		dateYear = year;
	}
	
	public void setVehicleTypes(String[] vehicles){
		this.vehicleTypes = vehicles;
	}
	
	public void setTimeType(String timeType){
		this.timeType = timeType;
	}
	
	public void setOptimization(int opt){
		this.optimizationChoice = opt;
	}

	@Override
	protected boolean parseResponse(HttpResponse response) {
		boolean success = false;
		InputStream stream = null;
		try {
			stream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			fileContents = builder.toString();
			Gson gson = new Gson();
			Type collectionType = new TypeToken<Collection<List<VORoute>>>(){}.getType();
			routeResult = gson.fromJson(fileContents, collectionType);
			if(routeResult != null){
				routes = new ArrayList<VORoute>(routeResult.size());
				Iterator<List<VORoute>> it = routeResult.iterator();
				while(it.hasNext()){
					Iterator<VORoute> itInner = it.next().iterator();
					while(itInner.hasNext()){
						routes.add(itInner.next());
					}
				}
				success = true;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				stream.close();
			}catch (Exception e){
				// Nothing
			}
		}
		
		return success;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result == true){
			activity.doneRouteQuery(routes.toArray(new VORoute[routes.size()]));
		}else{
			activity.doneRouteQuery(null);
		}
		super.onPostExecute(result);
	}

}
