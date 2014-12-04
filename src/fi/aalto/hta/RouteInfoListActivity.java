package fi.aalto.hta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;
import fi.aalto.hta.vos.VORoute;

import android.app.ListActivity;
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
import android.widget.LinearLayout;

public class RouteInfoListActivity extends ListActivity implements LocationListener {
	private LinearLayout progressLayout = null;
	private Button buttonTrack = null;
	private boolean toggleTrack = false;
	private static final String KEY_TRACK = "isTracking";
	private LocationManager locationManager = null;
	private RouteInfoArrayAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_info);
		VORoute route = getIntent().getExtras().getParcelable("route");
		adapter = new RouteInfoArrayAdapter(this, R.layout.listitem_route_info_part,
				route.getLegs().toArray(new VOLeg[route.getLegs().size()]));
		setListAdapter(adapter);
		
		buttonTrack = (Button)findViewById(R.id.buttonTrack);
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey(KEY_TRACK)){
				toggleTrack = savedInstanceState.getBoolean(KEY_TRACK);
			}
		}
		
		buttonTrack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggleTrack = !toggleTrack;
				if(toggleTrack){
					enableLocationTracking();
				}else{
					disableLocationTracking();
				}
			}
		});
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_TRACK, toggleTrack);
		super.onSaveInstanceState(outState);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_results_list, menu);
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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, this);
	}

	private void disableLocationTracking(){
		locationManager.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			adapter.updateNextPosition(location);
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {}


	@Override
	public void onProviderEnabled(String arg0) {}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

}
