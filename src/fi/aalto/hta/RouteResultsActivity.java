package fi.aalto.hta;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import fi.aalto.hta.gps.GeoTracker;
import fi.aalto.hta.interfaces.LocationUpdateCallBack;



public class RouteResultsActivity extends FragmentActivity implements LocationListener{
	private static final String TAG = RouteResultsActivity.class.getName();
	private ViewPager pager;
	private TabsAdapter tabsAdapter;
	private int tabIndex = 0;
	private ActionBar bar = null;
	private boolean toggleTrack = false;
	public static final String KEY_TRACK = "isTracking";
	public static final String KEY_LAST_KNOWN_LOC = "lastKnownLoc";
	private LocationManager locationManager = null;
	private Location lastKnownLocation = null;
	private Button buttonTrack = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate called");
		setContentView(R.layout.activity_route_results);
		if(savedInstanceState != null){
			if(savedInstanceState.containsKey(KEY_TRACK)){
				toggleTrack = savedInstanceState.getBoolean(KEY_TRACK);
				Log.d(TAG, "toggleTrack exists, state: " + toggleTrack);
			}
			if(savedInstanceState.containsKey(KEY_LAST_KNOWN_LOC)){
				lastKnownLocation = savedInstanceState.getParcelable(KEY_LAST_KNOWN_LOC);
			}
		}
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		int orientation = getResources().getConfiguration().orientation;
		if((screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) &&
				orientation == Configuration.ORIENTATION_LANDSCAPE ||
				(screenLayout & Configuration.SCREENLAYOUT_SIZE_NORMAL) > 0 &&
				orientation == Configuration.ORIENTATION_LANDSCAPE ||
				(screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE)){
			//			(screenLayout & Configuration.SCREENLAYOUT_SIZE_NORMAL) > 0 &&
			//			orientation == Configuration.ORIENTATION_LANDSCAPE ||

			//			View routeInfoFragment = (View)findViewById(R.id.fragmentRouteInfo);
			//			routeInfoFragment 
		}else{
			//create a new ViewPager and set to the pager we have created in Ids.xml

			pager = (ViewPager)findViewById(R.id.pager);

			bar = getActionBar();
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			bar.setTitle(R.string.title_activity_main);

			//Attach the Tabs to the fragment classes and set the tab title.
			tabsAdapter = new TabsAdapter(this, pager);
			Bundle args = new Bundle();
			args.putAll(getIntent().getExtras());
			args.putBoolean(KEY_TRACK, toggleTrack);
			args.putParcelable(KEY_LAST_KNOWN_LOC, lastKnownLocation);
			tabsAdapter.addTab(bar.newTab().setText("Info"), RouteInfoListFragment.class, args);
			tabsAdapter.addTab(bar.newTab().setText("Map"), RouteGraphFragment.class, args);

			//			tabsAdapter.addTab(bar.newTab().setText("Info"), RouteInfoListFragment.class, args);


		}
		if (savedInstanceState != null) {
			tabIndex = savedInstanceState.getInt("tab", 0);
			if(bar!= null){
				bar.setSelectedNavigationItem(tabIndex);
			}
		}
		buttonTrack = (Button)findViewById(R.id.buttonTrack);
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

	}

	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {
		return super.onCreateView(parent, name, context, attrs);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_results, menu);
		return true;
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState called");
		int tmpTabIndex = getActionBar().getSelectedNavigationIndex();
		if(tmpTabIndex >= 0){
			outState.putInt("tab", tmpTabIndex);
		}else{
			outState.putInt("tab", tabIndex);
		}
		outState.putBoolean(KEY_TRACK, toggleTrack);
		if(toggleTrack){
			outState.putParcelable(KEY_LAST_KNOWN_LOC, lastKnownLocation);
		}
		super.onSaveInstanceState(outState);
	}

	// create TabsAdapter to create tabs and behavior
	public static class TabsAdapter extends FragmentStatePagerAdapter
	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		//		private final manamFragmentManager = get
		SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public void onPageScrollStateChanged(int state) {}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i=0; i<mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}



		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1) {
			this.mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab arg0,
				android.app.FragmentTransaction arg1) {}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			registeredFragments.remove(position);
			super.destroyItem(container, position, object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment) super.instantiateItem(container, position);
			registeredFragments.put(position, fragment);
			return fragment;
			//			return super.instantiateItem(arg0, arg1);
		}

		public int getItemPosition(Object object){
			return PagerAdapter.POSITION_NONE;
		}

		public Fragment getActiveFragment(ViewPager container, int position) {
			return registeredFragments.get(position);
		}

		private static String makeFragmentName(int viewId, int index) {
			return "android:switcher:" + viewId + ":" + index;
		}

	}

	private void disableLocationTracking(){
		buttonTrack.setText(getString(R.string.startTracking));
		locationManager.removeUpdates(this);
		LocationUpdateCallBack fragInfo = null;
		LocationUpdateCallBack fragMap = null;
		if(tabsAdapter != null){ // small screen
			fragInfo = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 0);
			fragMap = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 1);
		}else{  // biig screen
			fragInfo = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteInfo);
			fragMap = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteGraph);
		}
		if(fragInfo != null){
			fragInfo.stopLocationUpdates();
		}
		if(fragMap != null){
			fragMap.stopLocationUpdates();
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			lastKnownLocation = location;
			LocationUpdateCallBack fragInfo = null;
			LocationUpdateCallBack fragMap = null;
			if(tabsAdapter != null){ // small screen
				//			LocationUpdateCallBack fragInfo = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteInfo);
				//			LocationUpdateCallBack fragMap = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteGraph);
				fragInfo = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 0);
				fragMap = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 1);
			}else{  // biig screen
				fragInfo = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteInfo);
				fragMap = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteGraph);
			}
			fragInfo.updateLocation(location);
			fragMap.updateLocation(location);
			//			routeLineOverlay.updateNextPosition(location);
			//			this.mapView.invalidate();
		}
	}


	@Override
	public void onProviderDisabled(String arg0) {}


	@Override
	public void onProviderEnabled(String arg0) {}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

	private void enableLocationTracking()
	{
		buttonTrack.setText(getString(R.string.stopTracking));
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, this);
		LocationUpdateCallBack fragInfo = null;
		LocationUpdateCallBack fragMap = null;
		if(tabsAdapter != null){ // small screen
			fragInfo = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 0);
			fragMap = (LocationUpdateCallBack)((TabsAdapter)tabsAdapter).getActiveFragment(pager, 1);
		}else{  // biig screen
			fragInfo = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteInfo);
			fragMap = (LocationUpdateCallBack)getSupportFragmentManager().findFragmentById(R.id.fragmentRouteGraph);
		}

		GeoTracker gps = new GeoTracker(RouteResultsActivity.this);
		// check if GPS enabled
		if(!gps.canGetLocation()){
			gps.showSettingsAlert();
			return;
		} 
		Location realLocation = new Location("");		
		realLocation.setLatitude(gps.getLatitude());
		realLocation.setLongitude(gps.getLongitude());
		if(fragInfo != null){
			fragInfo.startLocationUpdates();
			fragInfo.updateLocation(realLocation);
		}else{
			Log.d(TAG, "Start Location Updates: Info fragment cannot be reached");
		}
		if(fragMap != null){
			fragMap.startLocationUpdates();
			fragMap.updateLocation(realLocation);
		}else{
			Log.d(TAG, "StartLocationUpdates : Map fragment cannot be reached");
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		if(toggleTrack){
			enableLocationTracking();
//			buttonTrack.setText(getString(R.string.stopTracking));
		}else{
//			buttonTrack.setText(getString(R.string.startTracking));
		}
	}
	@Override
	protected void onPause() {
		disableLocationTracking();
		super.onPause();
	}
}
