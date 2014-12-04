package fi.aalto.hta;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import fi.aalto.hta.interfaces.LocationUpdateCallBack;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VORoute;

//public class RouteInfoListFragment extends Fragment implements LocationListener {
public class RouteInfoListFragment extends ListFragment implements LocationUpdateCallBack{
	//		public class RouteInfoListFragment extends Fragment{
	private LinearLayout progressLayout = null;
	private Button buttonTrack = null;

	private static final String KEY_TRACK = "isTracking";
	public static final String KEY_LAST_KNOWN_LOC = "lastKnownLoc";
	private static final String TAG = RouteInfoListFragment.class.getName();
	private LocationManager locationManager = null;
	private FragmentRouteInfoArrayAdapter adapter = null;
	private LinearLayout layout = null;
	private Location lastKnownLocation = null;
	private boolean toggleTrack = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_route_info, container, false);
		layout = (LinearLayout)inflater.inflate(R.layout.fragment_route_info, container, false);

		//		VORoute route = getArguments().getParcelable("route");

		return fragmentView;
		//		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		VORoute route = getActivity().getIntent().getExtras().getParcelable("route");
		adapter = new FragmentRouteInfoArrayAdapter((Activity)getActivity(), R.layout.listitem_route_info_part,
				route.getLegs().toArray(new VOLeg[route.getLegs().size()]));
		setListAdapter(adapter);
		//		if(savedInstanceState != null){
		//			toggleTrack = savedInstanceState.getBoolean(KEY_TRACK);
		//			lastKnownLocation = savedInstanceState.getParcelable(KEY_LAST_KNOWN_LOC);
		//		}

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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_TRACK, toggleTrack);
		outState.putParcelable(KEY_LAST_KNOWN_LOC, lastKnownLocation);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onResume() {
		super.onResume();
		//		adapter.notifyDataSetChanged();
	}
	@Override
	public void onPause() {
		stopLocationUpdates();
		super.onPause();
	}
	@Override
	public void updateLocation(Location location) {
		this.lastKnownLocation = location;
		adapter.updateNextPosition(location);
	}
	@Override
	public void startLocationUpdates() {
		toggleTrack = true;
	}

	@Override
	public void stopLocationUpdates() {
		adapter.disableNextPosition();
		toggleTrack = false;
	}
}
