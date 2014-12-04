package fi.aalto.hta;

import java.util.List;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

import fi.aalto.hta.interfaces.LocationUpdateCallBack;
import fi.aalto.hta.managers.HSLManager;
import fi.aalto.hta.util.DistancePointSegmentCalculator;
import fi.aalto.hta.vos.VOCoordinates;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;
import fi.aalto.hta.vos.VORoute;



//public class RouteGraphFragment extends MapActivity implements LocationListener {
public class RouteGraphFragment extends SupportMapFragment implements LocationUpdateCallBack{

	public static final String TYPE_WALK = ",walk,";
	public static final String TYPE_BUS = ",1,3,4,5,";
	public static final String TYPE_TRAIN = ",12,";
	public static final String TYPE_METRO = ",6,";
	public static final String TYPE_TRAM = ",2,";
	public int detectedNextStopInt = -1;
	public int detectedNextStopLegInt = -1;
	private List<VOLeg> legs = null;
	private boolean firstTimeLoaded = true;
	private static final String TAG = RouteGraphFragment.class.getName();
	private GoogleMap map = null;
	private Location lastKnownLocation = null;
	private boolean toggleTrack = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = super.onCreateView(inflater, container, savedInstanceState);
		VORoute route = getActivity().getIntent().getExtras().getParcelable("route");

		final View mapView = getView();
		legs = route.getLegs();
		map = getMap();
		redrawMap();
		return fragmentView;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final View mapView = getView();
		map = getMap();
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if(firstTimeLoaded == false){
					return;
				}
				firstTimeLoaded = false;
				VORoute route = getActivity().getIntent().getExtras().getParcelable("route");
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for(VOLeg leg: route.getLegs()){
					for(VOCoordinates coord: leg.getShape()){
						builder.include(new LatLng(coord.getY(), coord.getX()));
					}
				}
				CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 20);
				//				map.ani
				map.moveCamera(camUpdate);
				if(map.getCameraPosition().zoom > 15f){
					CameraUpdate zoom =  CameraUpdateFactory.zoomTo(15f);
					map.animateCamera(zoom);
				}
			}
		});
		Bundle arguments = getArguments();
		if(arguments != null){
			boolean contains = arguments.containsKey(RouteResultsActivity.KEY_TRACK);
			Log.d(TAG, "Contains: " + contains);
			
			
			toggleTrack = arguments.getBoolean(RouteResultsActivity.KEY_TRACK);
			if(toggleTrack){
				startLocationUpdates();
				Location lastKnownLoc = arguments.getParcelable(RouteResultsActivity.KEY_LAST_KNOWN_LOC);
				if(lastKnownLoc != null){
					updateLocation(lastKnownLoc);
				}
			}
		}
	}

	private static LatLng getLatLng(VOCoordinates coord){
		return new LatLng(coord.getY(),coord.getX());
	}

	private PolylineOptions getLineOptions(String type)
	{
		String tmpType = "," + type + ","; 
		PolylineOptions options = new PolylineOptions();
		options.width(10);
		options.geodesic(true);
		if(TYPE_WALK.indexOf(tmpType) > -1){
			options.color(Color.BLUE);
		}else if(TYPE_BUS.indexOf(tmpType) > -1){
			options.color(Color.BLUE);
		}else if(TYPE_TRAIN.indexOf(tmpType) > -1){
			options.color(Color.RED);
		}else if(TYPE_METRO.indexOf(tmpType) > -1){
			options.color(Color.CYAN);
		}else if(TYPE_TRAM.indexOf(tmpType) > -1){
			options.color(Color.GREEN);
		}
		return options;
	}

	private void redrawMap(){
		map.clear();
		VORoute route = getActivity().getIntent().getExtras().getParcelable("route");
		List<VOLeg> legs = route.getLegs();


		CircleOptions circleOptions = new CircleOptions();
		//		circleOptions.setDither(true);
		circleOptions.zIndex(1);
		circleOptions.radius(60);
		circleOptions.strokeColor(Color.BLACK);
		circleOptions.strokeWidth(2);
		circleOptions.fillColor(Color.YELLOW);

		CircleOptions circlePaintStart = new CircleOptions();
		circlePaintStart.zIndex(1);
		circlePaintStart.radius(60);
		circlePaintStart.strokeColor(Color.BLACK);
		circlePaintStart.strokeWidth(2);
		circlePaintStart.fillColor(Color.GREEN);

		CircleOptions circlePaintStop = new CircleOptions();
		circlePaintStop.zIndex(1);
		circlePaintStop.radius(60);
		circlePaintStop.strokeColor(Color.BLACK);
		circlePaintStop.strokeWidth(2);
		circlePaintStop.fillColor(Color.GRAY);

		CircleOptions circlePaintFollowedStop = new CircleOptions();
		circlePaintFollowedStop.zIndex(1);
		circlePaintFollowedStop.radius(60);
		circlePaintFollowedStop.strokeColor(Color.BLACK);
		circlePaintFollowedStop.strokeWidth(2);
		circlePaintFollowedStop.fillColor(Color.MAGENTA);

		CircleOptions circlePaintEnd = new CircleOptions();
		circlePaintEnd.zIndex(1);
		circlePaintEnd.radius(60);
		circlePaintEnd.strokeColor(Color.BLACK);
		circlePaintEnd.strokeWidth(2);
		circlePaintEnd.fillColor(Color.RED);

		int i = 0;
		for(VOLeg leg: route.getLegs()){
			List<VOCoordinates> coords = leg.getShape();
			PolylineOptions lineOptions = getLineOptions(leg.getType());
			for(VOCoordinates c:coords){
				// add a geo point onto line option
				lineOptions.add(getLatLng(c));
			}
			lineOptions.zIndex(0);
			map.addPolyline(lineOptions);


			List<VOLocation> stops = leg.getLocs();
			int j = 0;
			for(VOLocation tmpLoc: stops){
				if(detectedNextStopInt == j && detectedNextStopLegInt == i){
					map.addCircle(circlePaintFollowedStop.center(getLatLng(tmpLoc.getCoord())));
				}else if(i == 0 && j == 0){
					map.addCircle(circlePaintStart.center(getLatLng(tmpLoc.getCoord())));
				}else if(stops.size() - 1 == j && legs.get(legs.size()-1) == leg){  // it is the last one
					map.addCircle(circlePaintEnd.center(getLatLng(tmpLoc.getCoord())));
				}else if(j == 0 || stops.size() - 1 == j){
					map.addCircle(circleOptions.center(getLatLng(tmpLoc.getCoord())));
				}else if(!leg.getType().equals(HSLManager.VEH_TYPE_WALK)){
					map.addCircle(circlePaintStop.center(getLatLng(tmpLoc.getCoord())));
				}
				j++;
			}
			i++;
		}
	}
	
	@Override
	public void updateLocation(Location location) {
		this.lastKnownLocation = location;
		updateNextPosition(location);
		redrawMap();
	}
	
	public void updateNextPosition(Location userLocation){
		int[] result = DistancePointSegmentCalculator.updateNextPosition(legs, userLocation);
		detectedNextStopLegInt = result[0];
		detectedNextStopInt = result[1];
	}
	public void updateNextPosition(GeoPoint userLocation){
		Location loc = new Location("");
		loc.setLatitude(userLocation.getLatitudeE6() / 1e6);
		loc.setLongitude(userLocation.getLongitudeE6() / 1e6);
		int[] result = DistancePointSegmentCalculator.updateNextPosition(legs, loc);
		detectedNextStopLegInt = result[0];
		detectedNextStopInt = result[1];
		redrawMap();
	}
	@Override
	public void startLocationUpdates() {
		toggleTrack = true;
		if(map != null){
			map.setMyLocationEnabled(true);
		}
	}
	
	@Override
	public void stopLocationUpdates() {
		toggleTrack = false;
		if(map != null){
			map.setMyLocationEnabled(false);
			detectedNextStopInt = -1;
			detectedNextStopLegInt = 1;
			redrawMap();
		}
	}

	@Override
	public void onPause() {
		stopLocationUpdates();
		super.onPause();

	}
}
