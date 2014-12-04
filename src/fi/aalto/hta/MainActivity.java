package fi.aalto.hta;


import java.util.List;

import fi.aalto.hta.db.FavoriteLocation;
import fi.aalto.hta.db.asyn.GetHomeLocation;
import fi.aalto.hta.gps.GeoTracker;
import fi.aalto.hta.interfaces.FinishCallBackDbList;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements FinishCallBackDbList{
	private Button searchButton = null;
	private Button homeButton = null;
	private Button manageButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		searchButton = (Button)findViewById(R.id.buttonMainSearch);
		manageButton = (Button)findViewById(R.id.buttonMainManage);
		homeButton = (Button)findViewById(R.id.buttonMainHome);

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, RouteSearchActivity.class);
				startActivity(intent);
			}
		});

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(MainActivity.this, EditCategories.class);
//				startActivity(intent);
				new GetHomeLocation(MainActivity.this, MainActivity.this).execute();
				
			}
		});

		manageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, EditCategories.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void doneByDbList(List list) {
		if(list == null || list.size() == 0){
			SimpleAlertDialogBuilder builder = new SimpleAlertDialogBuilder(this, "Home location not available", "The home location cannot be acquired");
			builder.show();
			return;
		}
		
		FavoriteLocation location = (FavoriteLocation)list.get(0);
		
		GeoTracker gps = new GeoTracker(MainActivity.this);
		// check if GPS enabled
		if(!gps.canGetLocation()){
        	gps.showSettingsAlert();
        	return;
        } 
        
        
		
		Intent intent = new Intent(this, RouteResultsListActivity.class);
		Location realLocationDest = new Location("");
		realLocationDest.setLatitude(location.getLatitude());
		realLocationDest.setLongitude(location.getLongitude());
		intent.putExtra("locationDest", realLocationDest);
		
		Location realLocationDep = new Location("");
		realLocationDep.setLatitude(gps.getLatitude());
		realLocationDep.setLongitude(gps.getLongitude());
		intent.putExtra("locationDep", realLocationDep);
		startActivity(intent);
	}
}
