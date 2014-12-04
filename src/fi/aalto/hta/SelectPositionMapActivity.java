package fi.aalto.hta;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import fi.aalto.hta.overlays.LocationSelectOverlay;

public class SelectPositionMapActivity extends MapActivity{
	LocationSelectOverlay locationSelectOverlay = null;
	MapView mapView = null;
	Button buttonOk = null;
	Button buttonCancel = null;
	public static final int HELSINKI_LAT = 60197862;
	public static final int HELSINKI_LON = 24929295;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_select_position);
		
		mapView = (MapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setCenter(new GeoPoint(HELSINKI_LAT, HELSINKI_LON));
		mapView.getController().setZoom(13);
		buttonOk = (Button)findViewById(R.id.button_ok);
		buttonCancel = (Button)findViewById(R.id.button_cancel);
		Drawable drawable = getResources().getDrawable(R.drawable.blue_dot);
		locationSelectOverlay = new LocationSelectOverlay(drawable, SelectPositionMapActivity.this);
		
		mapView.getOverlays().add(locationSelectOverlay);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Location loc = locationSelectOverlay.getPosition();
				if(loc == null){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.positionNotSpecified),
							Toast.LENGTH_SHORT).show();
				}else{
					Intent intent = new Intent();
					intent.putExtra("location", loc);
					SelectPositionMapActivity.this.setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SelectPositionMapActivity.this.setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
