package fi.aalto.hta;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import fi.aalto.hta.overlays.RouteLineOverlay;
import fi.aalto.hta.vos.VORoute;

public class RouteGraphActivity extends MapActivity implements LocationListener {
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private MyLocationOverlay myLocationOverlay = null;
	private Button buttonTrack = null;
	private boolean toggleTrack = false;
	private Location lastKnownLoc = null;
	private LocationManager locationManager = null;
	private VORoute route = null;
	private static final String KEY_TRACK = "isTracking";
	private RouteLineOverlay routeLineOverlay = null;
	
	private static final String TAG = RouteGraphActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_route_graph);
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey(KEY_TRACK)){
				toggleTrack = savedInstanceState.getBoolean(KEY_TRACK);
			}
		}
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buttonTrack = (Button)findViewById(R.id.buttonTrack);
		route = getIntent().getExtras().getParcelable("route");
		mapView = (MapView)findViewById(R.id.mapview);
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.clearAnimation();
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		mapOverlays.add(myLocationOverlay);


		
		routeLineOverlay = new RouteLineOverlay(route.getLegs());
		mapOverlays.add(routeLineOverlay);

		buttonTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggleTrack = !toggleTrack;
				if(toggleTrack){
					enableLocationTracking();
					Location loc = new Location("");
//					loc.setLatitude(60.200891);
//					loc.setLongitude(24.934745);
					routeLineOverlay.updateNextPosition(loc);
				}else{
					disableLocationTracking();
				}
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.route_graph, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_TRACK, toggleTrack);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
	@Override
	protected void onPause() {
		disableLocationTracking();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(toggleTrack){
			enableLocationTracking();
		}
	}

	private void enableLocationTracking()
	{
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass(); // does not work in emulator
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(myLocationOverlay.getMyLocation());
				Location loc = new Location("");
				routeLineOverlay.updateNextPosition(myLocationOverlay.getMyLocation());
			}
		});
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, this);
	}

	private void disableLocationTracking(){
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass(); // does not work in emulator
		locationManager.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			lastKnownLoc = location;
			routeLineOverlay.updateNextPosition(location);
			this.mapView.invalidate();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {}


	@Override
	public void onProviderEnabled(String arg0) {}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

}
