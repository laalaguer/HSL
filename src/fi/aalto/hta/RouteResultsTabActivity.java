package fi.aalto.hta;

import fi.aalto.hta.vos.VORoute;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class RouteResultsTabActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_results_tab);
		VORoute route = getIntent().getExtras().getParcelable("route");
		
		TabHost tabHost = getTabHost();

		TabSpec infoSpec = tabHost.newTabSpec("Info");
		infoSpec.setIndicator("Info");
		Intent infoIntent = new Intent(this, RouteInfoListActivity.class);
		infoIntent.putExtra("route", route);
		infoSpec.setContent(infoIntent);
		tabHost.addTab(infoSpec);

		TabSpec mapSpec = tabHost.newTabSpec("Map");
		mapSpec.setIndicator("Map");
		Intent mapIntent = new Intent(this, RouteGraphActivity.class);
		mapIntent.putExtra("route", route);
		mapSpec.setContent(mapIntent);
		tabHost.addTab(mapSpec);
	}
}
