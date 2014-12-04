package fi.aalto.hta;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fi.aalto.hta.R;
import fi.aalto.hta.db.FavoriteLocation;
import fi.aalto.hta.db.asyn.CreateLocationAsyn;
import fi.aalto.hta.db.asyn.DeleteLocationAsyn;
import fi.aalto.hta.db.asyn.GetOneLocationAsyn;
import fi.aalto.hta.gps.GeoTracker;
import fi.aalto.hta.httpAsync.HttpAddressToGeo;
import fi.aalto.hta.httpAsync.HttpGeoToAddress;
import fi.aalto.hta.interfaces.FinishCallBackDbList;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;
import fi.aalto.hta.interfaces.FinishCallBackGeoCoding;
import fi.aalto.hta.interfaces.FinishCallBackReverseGeo;

public class CreateNewLocation extends Activity implements FinishCallBackDbVoid, FinishCallBackReverseGeo, FinishCallBackGeoCoding, FinishCallBackDbList{
	
	// location object to be stored to DB
	FavoriteLocation fl = new FavoriteLocation();
    // GeoTracker class
    GeoTracker gps;
    // Request code for camera, useful when implement OnActivityResult
    final int CAMERA_REQUEST_CODE = 1; 
    // Picture of show the camera caputer
    ImageView cameraCapture;
    // Bitmap to store into Favorite Location or ImageView
    Bitmap mImageBitmap;
    // Edit text 
    EditText title,addr,detail;
    // Gps Field
    TextView gpsField;
    // CategoryId
    int categoryId;
    // boolean if update
    boolean isUpdate;
    int updateLocationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_location);
		setTitle("Create New Location");
		cameraCapture = (ImageView) findViewById(R.id.create_item_pic);
		BitmapDrawable bd = (BitmapDrawable)cameraCapture.getDrawable();
		mImageBitmap = bd.getBitmap();
		title = (EditText) findViewById(R.id.create_item_title_field);
		addr = (EditText) findViewById(R.id.create_item_street_field);
		detail = (EditText) findViewById(R.id.create_item_des_field);
		gpsField = (TextView)findViewById(R.id.create_item_gps_field);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		categoryId = b.getInt(EditLocations.CATEGORY_ID,0);
		fl.type = categoryId;
		// if is updating the location, not create new;
		isUpdate = b.getBoolean(EditLocations.UPDATE);
		if (isUpdate){
			// 1. get location from db
			updateLocationId = b.getInt(EditLocations.LOCATION_ID);
			new GetOneLocationAsyn(this, this).execute(updateLocationId);
			setTitle("Modify Location");
			// 2. populate it to surface
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_location, menu);
		return true;
	}
	
	@Override 
	protected void  onActivityResult (int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
			if(requestCode == CAMERA_REQUEST_CODE){
				handleSmallCameraPhoto(data);
			}
		}
		
	}
	
	
	// 1. Collect Favorite location info
	//	  Check if all the fields are filled
	// 2. Store in DB 
	// 3. Finish this activity
	public void createLocation(View v){
		if (isEmpty(title)){
			Toast.makeText(v.getContext(), "At least fill Name", Toast.LENGTH_LONG).show();
		}else{
			if(isEmpty(addr)){
				Toast.makeText(v.getContext(), "At least fill address", Toast.LENGTH_LONG).show();
			}else{
				fl.setDescription(detail.getText().toString());
				fl.setStreet(addr.getText().toString());
				fl.setTitle(title.getText().toString());
				fl.setImage(mImageBitmap);
				
				if(isUpdate){
					ArrayList<Integer> a = new ArrayList<Integer>();
					a.add(updateLocationId);
					// delete the old location in db
					new DeleteLocationAsyn(this, this).execute(a);
				}
					// Anyway, create a new location in db
				new CreateLocationAsyn(v.getContext(),CreateNewLocation.this).execute(fl);
				

			}
		}

	}
	
	
	private boolean isEmpty(EditText et){
		if (et.getText().toString().trim().length()>0){
			return false;
		}
		else{
			return true;
		}
	}
	
	// 1. Check if GPS is available
	// 2. Store Geo info into Favorite Location Object
	// 3. Show the Geo Message on Screen
	public void fetchGeo(View v){
		// create class object
        gps = new GeoTracker(CreateNewLocation.this);
 
        // check if GPS enabled
        if(gps.canGetLocation()){
        	// Set Geo Information
        	fl.setGeo(gps.getLatitude(), gps.getLongitude());
        	// Update field of text view
        	View parent = (View)v.getParent();
        	TextView field = (TextView)parent.findViewById(R.id.create_item_gps_field);
        	field.setText(fl.getGeo());
        	// convert Geo to street address
        	HttpGeoToAddress hgta = new HttpGeoToAddress(this, fl.getLatitude(), fl.getLongitude(), this);
        	if (hgta.isOnline()==false){
        		hgta.showSettingsAlert();
        	}else{
        		String uriString = hgta.buildURIContent(null);
            	hgta.buildURI(uriString);
            	hgta.execute();
        	}        	
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
	}
	
	// If Address can be reverse geo to geo location
	public void fetchAddress(View v){
		View parent = (View)v.getParent();
		EditText street = (EditText) parent.findViewById(R.id.create_item_street_field);
		String streetAddress = street.getText().toString().trim();
		if (streetAddress!=""){
			try {
				String streetAddressEncoded = URLEncoder.encode(streetAddress,"UTF-8");
				if (streetAddress!=null){
					HttpAddressToGeo hatg = new HttpAddressToGeo(this, streetAddressEncoded, this);
					if(hatg.isOnline()==false){
						hatg.showSettingsAlert();
					}else{
						String uriString = hatg.buildURIContent(null);
						hatg.buildURI(uriString);
						hatg.execute();
						Log.d("FetchAddress", uriString);
					}
					
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}		
	}
	
	// 1.invoke camera activity
	public void invokeCamera(View view){
		// invoke camera
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
	}
	
	// 1. store pic into Favourite Object
	// 2. show pic on the screen
	private void handleSmallCameraPhoto(Intent intent) {
	    Bundle extras = intent.getExtras();
	    mImageBitmap = (Bitmap) extras.get("data");
	    cameraCapture.setImageBitmap(mImageBitmap);
	}

	
	// How this Activity is finished
	@Override
	public void doneByDbVoid() {
		if(isUpdate){
			isUpdate = false;
		}else{
			this.setResult(RESULT_OK);
			this.finish();
		}

	}

	@Override
	public void doneReverseGeo(String street) {
		// TODO Auto-generated method stub
		this.addr.setText(street);
	}

	@Override
	public void doneGeoCoding(double lat, double lon) {
		// TODO Auto-generated method stub
		fl.latitude = lat;
		fl.longitude = lon;
		this.gpsField.setText(String.valueOf(lat)+","+String.valueOf(lon));
	}

	@Override
	public void doneByDbList(List list) {
		// TODO Auto-generated method stub
		ArrayList<FavoriteLocation> fls = (ArrayList)list;
		FavoriteLocation fl = fls.get(0);
		title.setText(fl.title);
		addr.setText(fl.street_info);
		detail.setText(fl.description);
		gpsField.setText(fl.getGeoLngLat());
		cameraCapture.setImageBitmap(fl.image);
		
		this.fl.latitude = fl.getLatitude();
		this.fl.longitude = fl.getLongitude();
		this.fl.image = fl.image;
	}

}
