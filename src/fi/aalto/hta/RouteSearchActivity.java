package fi.aalto.hta;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.google.ads.AdView;
import com.smaato.soma.BannerView;

import fi.aalto.hta.httpAsync.HttpAddressToGeo;
import fi.aalto.hta.httpAsync.HttpGeoToAddress;
import fi.aalto.hta.httpAsync.HttpGeoToRoute;
import fi.aalto.hta.interfaces.FinishCallBackGeoCoding;
import fi.aalto.hta.interfaces.FinishCallBackReverseGeo;
import fi.aalto.hta.managers.HSLManager;

public class RouteSearchActivity extends Activity implements LocationListener, FinishCallBackReverseGeo, FinishCallBackGeoCoding{
	private Context context = null;
	private Button buttonSearch;
	private EditText editDeparture, editDestination;
	private LinearLayout progressLayout;
	private Location locationDep, locationDest;
	private boolean locationDepRequested = false, locationDestRequested = false;
	private Button buttonDepExpand, buttonDestExpand;
	private LocationManager locationManager = null;
	private BannerView mBanner = null;
	private RelativeLayout relMainLayout = null;
	private AdView adView = null;
	private Button buttonAdvanced = null;
	//	private EditText editHours, editMinutes;
	private LinearLayout layoutToggleTimeType;
	private LinearLayout layoutAdvancedSettings;
	private TextView textTypeDeparture, textTypeArrival;
	private TextView textTime, textDate;
	private Spinner spinnerOptimizations;
	//	private ToggleButton toggleBus, toggleTrain, toggleTram, toggleMetro, toggleFerry, toggleWalk;
	private ImageButton toggleBus, toggleTrain, toggleTram, toggleMetro, toggleFerry, toggleWalk;


	private static final int CODE_SELECT_DEP = 1;
	private static final int CODE_SELECT_DEST = 2;
	private static final int CODE_SELECT_DEP_CONT = 3;
	private static final int CODE_SELECT_DEST_CONT = 4;
	private static final int REQUEST_FOR_LOCATION_1 = 5;
	private static final int REQUEST_FOR_LOCATION_2 = 6;



	private static final int BUTTON_TYPE_DEP = 0;
	private static final int BUTTON_TYPE_DEST = 1;

	private boolean isTypeArrival = false;
	private boolean expand = false;
	private static boolean animWorkingFlag = false;
	private boolean isBusOn = false,
			isTramOn = false,
			isTrainOn = false,
			isMetroOn = false,
			isFerryOn = false,
			isWalkOn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_search);
		this.context = RouteSearchActivity.this;
		buttonSearch = (Button)findViewById(R.id.buttonSearch);
		editDeparture = (EditText)findViewById(R.id.editTextDeparture);
		editDestination = (EditText)findViewById(R.id.editTextDestination);
		progressLayout = (LinearLayout)findViewById(R.id.routeLayProgress);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		buttonDepExpand = (Button)findViewById(R.id.buttonDepExpand);
		buttonDestExpand = (Button)findViewById(R.id.buttonDestExpand);
		relMainLayout = (RelativeLayout)findViewById(R.id.layoutRelMain);
		buttonAdvanced = (Button)findViewById(R.id.buttonAdvancedOptions);
		textTypeArrival = (TextView)findViewById(R.id.textSwitchArrival);
		textTypeDeparture = (TextView)findViewById(R.id.textSwitchDeparture);
		layoutToggleTimeType = (LinearLayout)findViewById(R.id.layoutToggleTimeType);
		layoutAdvancedSettings = (LinearLayout)findViewById(R.id.layoutAdvancedSettings);
		textTime = (TextView)findViewById(R.id.textTime);
		textDate = (TextView)findViewById(R.id.textDate);		
		spinnerOptimizations = (Spinner) findViewById(R.id.spinnerOptimizations);

		toggleBus = (ImageButton)findViewById(R.id.imgToggleButtonBus);
		toggleTrain = (ImageButton)findViewById(R.id.imgToggleButtonTrain);
		toggleMetro = (ImageButton)findViewById(R.id.imgToggleButtonMetro);
		toggleTram = (ImageButton)findViewById(R.id.imgToggleButtonTram);
		toggleFerry = (ImageButton)findViewById(R.id.imgToggleButtonFerry);
		toggleWalk = (ImageButton)findViewById(R.id.imgToggleButtonWalk);
		//		toggleBus.getBackground().setLevel(5000);
		//		toggleMetro.getBackground().setLevel(5000);

		toggleBus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isBusOn = !isBusOn;
				isWalkOn = false;
				if(isBusOn){
					toggleBus.setImageResource(R.drawable.bus_blue);
					toggleWalk.setImageResource(R.drawable.man_white);
				}else{
					toggleBus.setImageResource(R.drawable.bus_white);
				}
			}
		});

		toggleTrain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isTrainOn = !isTrainOn;
				isWalkOn = false;
				if(isTrainOn){
					toggleTrain.setImageResource(R.drawable.train_blue);
					toggleWalk.setImageResource(R.drawable.man_white);
				}else{
					toggleTrain.setImageResource(R.drawable.train_white);
				}
			}
		});

		toggleMetro.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isMetroOn = !isMetroOn;
				isWalkOn = false;
				if(isMetroOn){
					toggleMetro.setImageResource(R.drawable.metro_blue);
					toggleWalk.setImageResource(R.drawable.man_white);
				}else{
					toggleMetro.setImageResource(R.drawable.metro_white);
				}
			}
		});

		toggleTram.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isTramOn = !isTramOn;
				isWalkOn = false;
				if(isTramOn){
					toggleTram.setImageResource(R.drawable.tram_blue);
					toggleWalk.setImageResource(R.drawable.man_white);
				}else{
					toggleTram.setImageResource(R.drawable.tram_white);
				}
			}
		});

		toggleFerry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isFerryOn = !isFerryOn;
				isWalkOn = false;
				if(isFerryOn){
					toggleFerry.setImageResource(R.drawable.boat_blue);
					toggleWalk.setImageResource(R.drawable.man_white);
				}else{
					toggleFerry.setImageResource(R.drawable.boat_white);
				}
			}
		});

		toggleWalk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isWalkOn = !isWalkOn;
				isBusOn = false;
				isTrainOn = false;
				isTramOn = false;
				isMetroOn = false;
				isFerryOn = false;

				if(isWalkOn){
					toggleWalk.setImageResource(R.drawable.man_blue);
					toggleTrain.setImageResource(R.drawable.train_white);
					toggleBus.setImageResource(R.drawable.bus_white);
					toggleMetro.setImageResource(R.drawable.metro_white);
					toggleFerry.setImageResource(R.drawable.boat_white);
					toggleTram.setImageResource(R.drawable.tram_white);
				}else{
					toggleWalk.setImageResource(R.drawable.man_white);
				}
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.optimizations, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerOptimizations.setAdapter(adapter);

		textTypeArrival.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isTypeArrival = true;
				//					textTypeArrival.setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_blue_light));
				//					textTypeDeparture.setBackgroundDrawable(getResources().getDrawable(R.drawable.light_orange));
				textTypeArrival.setBackgroundDrawable(getResources().getDrawable(R.color.selected_text_background_color));
				textTypeDeparture.setBackgroundDrawable(getResources().getDrawable(R.color.unselected_text_background_color));
			}
		});

		textTypeDeparture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isTypeArrival = false;
				//					textTypeArrival.setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_blue_light));
				//					textTypeDeparture.setBackgroundDrawable(getResources().getDrawable(R.drawable.light_orange));
				textTypeDeparture.setBackgroundDrawable(getResources().getDrawable(R.color.selected_text_background_color));
				textTypeArrival.setBackgroundDrawable(getResources().getDrawable(R.color.unselected_text_background_color));
			}
		});


		buttonDepExpand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopup(RouteSearchActivity.this, BUTTON_TYPE_DEP);
			}
		});

		buttonDestExpand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopup(RouteSearchActivity.this, BUTTON_TYPE_DEST);
			}
		});

		editDeparture.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				locationDep = null;
				return false;
			}
		});
		editDestination.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				locationDest = null;
				return false;
			}
		});
		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(editDeparture.getText().toString().length() == 0  ||
						editDestination.getText().toString().length() == 0){
					SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(context, "Information Missing", "Please specify the departure and destination information first");
					builder.show();
					return;
				}
				
				if(locationDep == null){
					getDeparture();
				}else if(locationDest == null){
					getDestination();
				}else{
					doSearch();
				}
			}
		});


		buttonAdvanced.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				expand=!expand;
				Animation a = expand(layoutAdvancedSettings, expand);
				layoutAdvancedSettings.startAnimation(a);
				if(expand){
					v.setBackgroundDrawable(getResources().getDrawable(android.R.color.holo_blue_dark));
				}else{
					v.setBackgroundDrawable(getResources().getDrawable(R.drawable.dark_background));
				}
			}
		});

		//		// Advertisements:
		//		mBanner = (BannerView)findViewById(R.id.bannerView);
		//		mBanner.getAdSettings().setPublisherId(923869308);
		//		mBanner.getAdSettings().setAdspaceId(65776991);
		//		mBanner.getUserSettings().setKeywordList("Helsinki,Transport");
		//		mBanner.asyncLoadNewBanner();
		//		mBanner.setAutoReloadFrequency(20);

		//		adView = new AdView(this, AdSize.BANNER, "a15165d400b79b7");
		//		relMainLayout.addView(adView);
		//		adView.loadAd(new AdRequest());
		Log.i("asdf", Secure.getString(getContentResolver(),
				Secure.ANDROID_ID));
		Time now = new Time();
		now.setToNow();
		textTime.setText(String.format("%02d:%02d", now.hour, now.minute));
		textDate.setText(String.format("%02d.%02d.%4d", now.monthDay, now.month + 1, now.year));

		textTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Time time = new Time();
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
				Date date = null;
				try {
					date = dateFormat.parse(textTime.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Log.e("RouteSearchAct", "Error while parsing time");
					e.printStackTrace();
					return;
				}
				Calendar cal = GregorianCalendar.getInstance(); 
				cal.setTime(date);  

				OnTimeSetListener listener = new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						textTime.setText(String.format("%02d:%02d", hourOfDay, minute));
					}
				};
				TimePickerDialog tp = new TimePickerDialog(RouteSearchActivity.this, listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
				tp.show();
			}
		});

		textDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Time time = new Time();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
				Date date = null;
				try {
					date = dateFormat.parse(textDate.getText().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Log.e("RouteSearchAct", "Error while parsing date");
					e.printStackTrace();
					return;
				}
				Calendar cal = GregorianCalendar.getInstance(); 
				cal.setTime(date);  

				OnDateSetListener listener = new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						textDate.setText(String.format("%02d.%02d.%d", dayOfMonth, monthOfYear+1, year));

					}
				};
				DatePickerDialog dp = new DatePickerDialog(RouteSearchActivity.this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				dp.show();
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == CODE_SELECT_DEP && resultCode == RESULT_OK){
			locationDep = (Location)data.getExtras().get("location");
			HttpGeoToAddress httpGeo = new HttpGeoToAddress(this,
					locationDep.getLatitude(),
					locationDep.getLongitude(), 
					this);
			httpGeo.buildURI(httpGeo.buildURIContent(null));
			locationDepRequested = true;
			locationDestRequested = false;
			httpGeo.execute();
		}else if(requestCode == CODE_SELECT_DEST && resultCode == RESULT_OK){
			locationDest = (Location)data.getExtras().get("location");
			HttpGeoToAddress httpGeo = new HttpGeoToAddress(this,
					locationDest.getLatitude(),
					locationDest.getLongitude(), 
					this);
			httpGeo.buildURI(httpGeo.buildURIContent(null));
			locationDepRequested = false;
			locationDestRequested = true;
			httpGeo.execute();
		}else if(requestCode == CODE_SELECT_DEP_CONT && resultCode == RESULT_OK){
			editDeparture.setText(data.getStringExtra("address"));
		}else if(requestCode == CODE_SELECT_DEST_CONT && resultCode == RESULT_OK){
			editDestination.setText(data.getStringExtra("address"));
		}

		if (requestCode == REQUEST_FOR_LOCATION_1 && resultCode == RESULT_OK){
			Bundle b = data.getExtras();
			editDeparture.setText(b.getString("Street"));
			//			geo1.setText(b.getString("Geo"));

		}else{
			if (requestCode == REQUEST_FOR_LOCATION_2 && resultCode == RESULT_OK){
				Bundle b = data.getExtras();
				editDestination.setText(b.getString("Street"));
				//				geo2.setText(b.getString("Geo"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public synchronized void onLocationChanged(Location location) {
		if (location != null) {
			if(locationDepRequested){
				locationDep = location;
				try {
					String uriString = HSLManager.getReverseGeocodeString(locationDep);
					URI uri = new URI(uriString);
					Log.i("Addr", uriString);
					HttpGeoToAddress httpAddress = new HttpGeoToAddress(this, locationDep.getLatitude(),
							locationDep.getLongitude(), this);
					
					httpAddress.buildURI(httpAddress.buildURIContent(null));
					httpAddress.execute();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (locationDestRequested){
				locationDest = location;
				try {
					String uriString = HSLManager.getReverseGeocodeString(locationDest);
					URI uri = new URI(uriString);
					Log.i("Addr", uriString);
					HttpGeoToAddress httpAddress = new HttpGeoToAddress(this, locationDest.getLatitude(),
							locationDest.getLongitude(), this);
					httpAddress.buildURI(httpAddress.buildURIContent(null));
					httpAddress.execute();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	protected void onPause() {
		locationManager.removeUpdates(this);
		super.onPause();
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.gps_disabled_description));
		builder.setCancelable(false);
		builder.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});
		builder.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void showPopup(final Activity context, final int buttonType) {
		LinearLayout viewGroup = (LinearLayout)context.findViewById(R.id.layoutPopup);
		LayoutInflater layoutInflater = context.getLayoutInflater();
		View layout = layoutInflater.inflate(R.layout.popup, viewGroup);

		TextView textButtonMap = (TextView)layout.findViewById(R.id.textButtonMap);
		TextView textButtonCurrentPos = (TextView)layout.findViewById(R.id.textButtonCurrentPos);
		TextView textButtonContact = (TextView)layout.findViewById(R.id.textButtonContact);
		TextView textButtonFavorite = (TextView)layout.findViewById(R.id.textButtonFavorite);

		final PopupWindow popup = new PopupWindow(context);

		textButtonMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(context, SelectPositionMapActivity.class);
				if(buttonType == BUTTON_TYPE_DEP){
					startActivityForResult(intent, RouteSearchActivity.CODE_SELECT_DEP);
				}else if(buttonType == BUTTON_TYPE_DEST){
					startActivityForResult(intent, RouteSearchActivity.CODE_SELECT_DEST);
				}
			}
		});
		textButtonCurrentPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
					buildAlertMessageNoGps();
				}else{
					if(buttonType == BUTTON_TYPE_DEP){
						locationDepRequested = true;
						locationDestRequested = false;
					}else if(buttonType == BUTTON_TYPE_DEST){
						locationDestRequested = true;
						locationDepRequested = false;
					}
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, RouteSearchActivity.this);
				}
			}
		});
		textButtonContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
				if(buttonType == BUTTON_TYPE_DEP){
					RouteSearchActivity.this.startActivityForResult(intent, CODE_SELECT_DEP_CONT);
				}else if(buttonType == BUTTON_TYPE_DEST){
					RouteSearchActivity.this.startActivityForResult(intent, CODE_SELECT_DEST_CONT);
				}
			}
		});

		textButtonFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popup.dismiss();
				Intent intent = new Intent(context, ViewCategories.class);
				if(buttonType == BUTTON_TYPE_DEP){
					startActivityForResult(intent, RouteSearchActivity.REQUEST_FOR_LOCATION_1);
				}else if(buttonType == BUTTON_TYPE_DEST){
					startActivityForResult(intent, RouteSearchActivity.REQUEST_FOR_LOCATION_2);
				}
			}
		});
		// Creating the PopupWindow
		popup.setContentView(layout);
		popup.setWindowLayoutMode(0, LayoutParams.WRAP_CONTENT);
		popup.setWidth(buttonDepExpand.getWidth());
		popup.setFocusable(true);

		int[] location = new int[2];
		if(buttonType == BUTTON_TYPE_DEP){
			buttonDepExpand.getLocationOnScreen(location);
		}else if (buttonType == BUTTON_TYPE_DEST){
			buttonDestExpand.getLocationOnScreen(location);
		}

		popup.setAnimationStyle(R.style.AnimationPopup);
		popup.showAtLocation(layout, Gravity.NO_GRAVITY, location[0], location[1] + buttonDepExpand.getHeight());
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}


	public static Animation expand(final View v, final boolean expand) {
		try {
			Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
			m.setAccessible(true);
			m.invoke(v,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST)
					);
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int initialHeight = v.getMeasuredHeight();
		//	    final int initialHeight = 100;

		if (expand) {
			v.getLayoutParams().height = 0;
			v.setVisibility(View.VISIBLE);
		} else {
			//	        v.getLayoutParams().height = initialHeight;
		}


		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				int newHeight = 0;
				if(expand){
					newHeight = (int)(initialHeight * interpolatedTime);
				}else{
					newHeight = (int)(initialHeight * (1 - interpolatedTime));
				}
				v.getLayoutParams().height = newHeight;
				v.requestLayout();

				if(interpolatedTime == 1 && !expand){
					v.setVisibility(View.GONE);
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setDuration(350);
		return a;
	}

	@Override
	public void doneReverseGeo(String street) {
		if(locationDepRequested){
			editDeparture.setText(street);
		}else if(locationDestRequested){
			editDestination.setText(street);
		}
		locationDepRequested = false;
		locationDestRequested = false;

	}
	
	@Override
	public void doneGeoCoding(double lat, double lon) {
		if(locationDep == null){
			locationDep = new Location("");
			locationDep.setLatitude(lat);
			locationDep.setLongitude(lon);
			if(locationDest == null){
				getDestination();
			}else{
				doSearch();
			}

		}else if(locationDest == null){
			locationDest = new Location("");
			locationDest.setLatitude(lat);
			locationDest.setLongitude(lon);
			doSearch();
		}
	}
	
	public void doSearch(){
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		Date time = null; 
		try{
			time = timeFormat.parse(textTime.getText().toString());
		}catch(ParseException e){
			SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(context, "Invalid Time", "The specified time is invalid");
			builder.show();
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try{
			date = dateFormat.parse(textDate.getText().toString());
		}catch(ParseException e){
			SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(context, "Invalid Date", "The specified date is invalid");
			builder.show();
			return;
		}
		Calendar calTime = GregorianCalendar.getInstance(); 
		calTime.setTime(time);

		Calendar calDate = GregorianCalendar.getInstance(); 
		calDate.setTime(date);


		Intent intent = new Intent(getApplicationContext(), RouteResultsListActivity.class);
		Bundle paramBundle = new Bundle();
		intent.putExtra("addressDep", RouteSearchActivity.this.editDeparture.getText().toString());
		intent.putExtra("addressDest", RouteSearchActivity.this.editDestination.getText().toString());
		intent.putExtra("locationDep", locationDep);
		intent.putExtra("locationDest", locationDest);

		paramBundle.putInt(HSLManager.PARAM_TIME_HOUR, calTime.get(Calendar.HOUR_OF_DAY));
		paramBundle.putInt(HSLManager.PARAM_TIME_MINUTES, calTime.get(Calendar.MINUTE));
		paramBundle.putInt(HSLManager.PARAM_DATE_YEAR, calDate.get(Calendar.YEAR));
		paramBundle.putInt(HSLManager.PARAM_DATE_MONTH, calDate.get(Calendar.MONTH)+1);
		paramBundle.putInt(HSLManager.PARAM_DATE_DAY, calDate.get(Calendar.DAY_OF_MONTH));

		paramBundle.putString(HSLManager.PARAM_TIME_TYPE, isTypeArrival ? HSLManager.TIME_TYPE_ARRIVAL: HSLManager.TIME_TYPE_DEPARTURE);
		paramBundle.putInt(HSLManager.PARAM_OPTIMIZE_TYPE, spinnerOptimizations.getSelectedItemPosition());
		ArrayList<String> vehicleTypes = new ArrayList<String>();

		if(isBusOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_BUS);
		}
		if(isTrainOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_TRAIN);
		}
		if(isMetroOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_METRO);
		}
		if(isTramOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_TRAM);
		}
		if(isFerryOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_FERRY);
		}
		if(isWalkOn){
			vehicleTypes.add(HSLManager.VEH_TYPE_WALK);
		}
		paramBundle.putStringArray(HSLManager.PARAM_TRANSPORT_TYPES, vehicleTypes.toArray(new String[vehicleTypes.size()]));
		intent.putExtra(RouteResultsListActivity.EXTRA_PARAMS_BUNDLE, paramBundle);
		startActivity(intent);
	}
	private void getDeparture(){
		HttpAddressToGeo httpGeo = new HttpAddressToGeo(this, editDeparture.getText().toString(), this);
		httpGeo.buildURI(httpGeo.buildURIContent(null));
		httpGeo.execute();
	}

	private void getDestination(){
		HttpAddressToGeo httpGeo = new HttpAddressToGeo(this, editDestination.getText().toString(), this);
		httpGeo.buildURI(httpGeo.buildURIContent(null));
		httpGeo.execute();
	}
}

