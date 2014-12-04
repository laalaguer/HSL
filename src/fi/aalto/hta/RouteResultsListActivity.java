package fi.aalto.hta;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.ListActivity;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import fi.aalto.hta.httpAsync.HttpAddressToGeo;
import fi.aalto.hta.httpAsync.HttpGeoToRoute;
import fi.aalto.hta.interfaces.FinishCallBackGeoCoding;
import fi.aalto.hta.interfaces.FinishCallBackRoute;
import fi.aalto.hta.managers.HSLManager;
import fi.aalto.hta.vos.VORoute;

public class RouteResultsListActivity extends ListActivity implements FinishCallBackRoute, FinishCallBackGeoCoding{
	private LinearLayout progressLayout = null;
	private Button buttonEarlier = null, buttonLater = null;
	private String addressDep = null, addressDest = null;
	private Location locationDep = null, locationDest = null;
	private URI uriDep = null, uriDest = null;

	public final static String EXTRA_PARAMS_BUNDLE = "paramsBundle";
	private int timeHour, timeMinutes;

	private Bundle bundle = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_results_list);
		progressLayout = (LinearLayout)findViewById(R.id.routeLayProgress);
		buttonEarlier = (Button)findViewById(R.id.buttonEarlier);
		buttonLater = (Button)findViewById(R.id.buttonLater);
		addressDep = getIntent().getStringExtra("addressDep");
		addressDest = getIntent().getStringExtra("addressDest");
		locationDep = getIntent().getParcelableExtra("locationDep");
		locationDest = getIntent().getParcelableExtra("locationDest");
		getRoute();

		buttonEarlier.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteResultsArrayAdapter adapter = (RouteResultsArrayAdapter)getListAdapter();
				if(adapter == null){
					return;
				}
				VORoute[] routes = adapter.getRoutes();
				HttpGeoToRoute httpRoute = new HttpGeoToRoute(RouteResultsListActivity.this,
						locationDep.getLatitude(), locationDep.getLongitude(),
						locationDest.getLatitude(), locationDest.getLongitude(),
						RouteResultsListActivity.this);
				Bundle bundle = getIntent().getBundleExtra(EXTRA_PARAMS_BUNDLE);
				VORoute firstRoute = routes[0];
				String firstDeparture = firstRoute.getLegs().get(0).getLocs().get(0).getDepTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Date firstDepartureDate = null;
				try {
					firstDepartureDate = sdf.parse(firstDeparture);
				} catch (ParseException e) {
					SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(RouteResultsListActivity.this, "Time Error", "Cannot calculate the earlier time");
					builder.show();
					return;
				}
				int durationSumSeconds = 0;
				int count = 0;
				for(VORoute route : routes){
					durationSumSeconds += route.getDuration();
					count++;
				}
				int avgMins = durationSumSeconds/count/60;
				Calendar calTime = GregorianCalendar.getInstance();
				calTime.setTime(firstDepartureDate);
				//				setTime(calTime, bundle);
				calTime.add(Calendar.MINUTE, -avgMins);
				//					bundle.putString(HSLManager.PARAM_TIME_TYPE, HSLManager.TIME_TYPE_DEPARTURE);
				bundle.putInt(HSLManager.PARAM_DATE_DAY, calTime.get(Calendar.DAY_OF_MONTH));
				bundle.putInt(HSLManager.PARAM_DATE_MONTH, calTime.get(Calendar.MONTH)+1);
				bundle.putInt(HSLManager.PARAM_DATE_YEAR, calTime.get(Calendar.YEAR));
				bundle.putInt(HSLManager.PARAM_TIME_HOUR, calTime.get(Calendar.HOUR_OF_DAY));
				bundle.putInt(HSLManager.PARAM_TIME_MINUTES, calTime.get(Calendar.MINUTE));
				setExtraParams(bundle, httpRoute);
				httpRoute.buildURI(httpRoute.buildURIContent());
				httpRoute.execute();
			}
		});

		buttonLater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RouteResultsArrayAdapter adapter = (RouteResultsArrayAdapter)getListAdapter();
				if(adapter == null){
					return;
				}
				VORoute[] routes = adapter.getRoutes();
				HttpGeoToRoute httpRoute = new HttpGeoToRoute(RouteResultsListActivity.this,
						locationDep.getLatitude(), locationDep.getLongitude(),
						locationDest.getLatitude(), locationDest.getLongitude(),
						RouteResultsListActivity.this);
				Bundle bundle = getIntent().getBundleExtra(EXTRA_PARAMS_BUNDLE);
				VORoute lastRoute = routes[routes.length-1];
				int legCount = lastRoute.getLegs().size();
				String lastDeparture = lastRoute.getLegs().get(0).getLocs().get(0).getDepTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Date lastDepartureDate = null;
				try {
					lastDepartureDate = sdf.parse(lastDeparture);
				} catch (ParseException e) {
					SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(RouteResultsListActivity.this, "Time Error", "Cannot calculate a later time");
					builder.show();
					return;
				}

				Calendar calTime = GregorianCalendar.getInstance();
				calTime.setTime(lastDepartureDate);
				calTime.add(Calendar.MINUTE, 1);
				//				setTime(calTime, bundle);
				//				calTime.add(Calendar.MINUTE, 15);
				//					bundle.putString(HSLManager.PARAM_TIME_TYPE, HSLManager.TIME_TYPE_DEPARTURE);
				bundle.putInt(HSLManager.PARAM_DATE_DAY, calTime.get(Calendar.DAY_OF_MONTH));
				bundle.putInt(HSLManager.PARAM_DATE_MONTH, calTime.get(Calendar.MONTH)+1);
				bundle.putInt(HSLManager.PARAM_DATE_YEAR, calTime.get(Calendar.YEAR));
				bundle.putInt(HSLManager.PARAM_TIME_HOUR, calTime.get(Calendar.HOUR_OF_DAY));
				bundle.putInt(HSLManager.PARAM_TIME_MINUTES, calTime.get(Calendar.MINUTE));
				setExtraParams(bundle, httpRoute);
				httpRoute.buildURI(httpRoute.buildURIContent());
				httpRoute.execute();
			}
		});
	}

	@Override
	public void doneRouteQuery(VORoute[] routes) {
		if(routes == null){
			return;
		}
		setListAdapter(new RouteResultsArrayAdapter(this,
				R.layout.listitem_route_search_results,
				routes));
	}

	private void getDeparture(){
		HttpAddressToGeo httpGeo = new HttpAddressToGeo(this, addressDep, this);
		httpGeo.buildURI(httpGeo.buildURIContent(null));
		httpGeo.execute();
	}

	private void getDestination(){
		HttpAddressToGeo httpGeo = new HttpAddressToGeo(this, addressDest, this);
		httpGeo.buildURI(httpGeo.buildURIContent(null));
		httpGeo.execute();
	}

	private void getRoute(){

		HttpGeoToRoute httpRoute = new HttpGeoToRoute(this, locationDep.getLatitude(), locationDep.getLongitude(),
				locationDest.getLatitude(), locationDest.getLongitude(), this);
		Bundle bundle = getIntent().getBundleExtra(EXTRA_PARAMS_BUNDLE);
		if(bundle != null){
			setExtraParams(bundle, httpRoute);
		}
		httpRoute.buildURI(httpRoute.buildURIContent());
		httpRoute.execute();
	}
	private void setExtraParams(Bundle bundle, HttpGeoToRoute httpRoute){
		if(bundle.containsKey(HSLManager.PARAM_TIME_HOUR) && bundle.containsKey(HSLManager.PARAM_TIME_MINUTES)){
			int hour = bundle.getInt(HSLManager.PARAM_TIME_HOUR);
			int minutes = bundle.getInt(HSLManager.PARAM_TIME_MINUTES);
			httpRoute.setTime(hour, minutes);
		}
		if(bundle.containsKey(HSLManager.PARAM_DATE_YEAR) &&
				bundle.containsKey(HSLManager.PARAM_DATE_MONTH) &&
				bundle.containsKey(HSLManager.PARAM_DATE_DAY)){
			int year = bundle.getInt(HSLManager.PARAM_DATE_YEAR);
			int month = bundle.getInt(HSLManager.PARAM_DATE_MONTH);
			int day = bundle.getInt(HSLManager.PARAM_DATE_DAY);
			httpRoute.setDate(year,  month, day);
		}

		if(bundle.containsKey(HSLManager.PARAM_TIME_TYPE)){
			httpRoute.setTimeType(bundle.getString(HSLManager.PARAM_TIME_TYPE));
		}

		if(bundle.containsKey(HSLManager.PARAM_OPTIMIZE_TYPE)){
			httpRoute.setOptimization(bundle.getInt(HSLManager.PARAM_OPTIMIZE_TYPE));
		}

		if(bundle.containsKey(HSLManager.PARAM_TRANSPORT_TYPES)){
			String[] vehicles = bundle.getStringArray(HSLManager.PARAM_TRANSPORT_TYPES);
			httpRoute.setVehicleTypes(vehicles);
		}
	}

	private void setTime(Calendar calObj, Bundle bundle){
		int hour, minutes;
		int year, month, day;
		if(bundle.containsKey(HSLManager.PARAM_TIME_HOUR) && bundle.containsKey(HSLManager.PARAM_TIME_MINUTES)){
			hour = bundle.getInt(HSLManager.PARAM_TIME_HOUR);
			minutes = bundle.getInt(HSLManager.PARAM_TIME_MINUTES);
		}else{
			Time now = new Time();
			now.setToNow();
			hour = now.hour;
			minutes = now.minute;
		}
		if(bundle.containsKey(HSLManager.PARAM_DATE_DAY) &&
				bundle.containsKey(HSLManager.PARAM_DATE_MONTH) &&
				bundle.containsKey(HSLManager.PARAM_DATE_YEAR)){
			year = bundle.getInt(HSLManager.PARAM_DATE_YEAR);
			month = bundle.getInt(HSLManager.PARAM_DATE_MONTH);
			day = bundle.getInt(HSLManager.PARAM_DATE_DAY);
		}else{
			Time now = new Time();
			now.setToNow();
			year = now.year;
			month = now.month;
			day = now.monthDay;
		}
		calObj.set(Calendar.HOUR_OF_DAY, hour);
		calObj.set(Calendar.MINUTE, minutes);
		calObj.set(Calendar.YEAR, year);
		calObj.set(Calendar.MONTH, month);
		calObj.set(Calendar.DAY_OF_MONTH, day);
	}

	@Override
	public void doneGeoCoding(double lat, double lon) {
		// TODO Auto-generated method stub

	}
}
