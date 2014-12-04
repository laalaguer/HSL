package fi.aalto.hta;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;
import fi.aalto.hta.vos.VORoute;

public class RouteResultsArrayAdapter extends ArrayAdapter<VORoute>{
	ArrayList<ArrayList<String>> list = null;
	LayoutInflater mInflater = null;
	private VORoute[] routes = null;
	private ListActivity activity = null;


	public RouteResultsArrayAdapter(ListActivity activity, int textViewResourceId, VORoute[] routes) {
		super(activity, textViewResourceId, routes);
		mInflater = LayoutInflater.from(activity);
		this.routes = routes;
		this.activity = activity;
		final ListActivity finalActivity = activity;
		finalActivity.getListView().setOnItemClickListener(mOnItemClickListener);

	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;

		if (null == convertView) {
			row = mInflater.inflate(R.layout.listitem_route_search_results, parent, false);
		} else {
			row = convertView;
		}
		ViewGroup group = (ViewGroup)row.findViewById(R.id.row_layout);
		group.removeAllViews();
		LinearLayout layout = (LinearLayout)mInflater.inflate(R.layout.listitem_route_search_results, null);
		group.addView(layout);


		ViewGroup phaseList = (ViewGroup)layout.findViewById(R.id.layoutRouteStages);
		TextView time = (TextView)layout.findViewById(R.id.textRouteTime);
		int duration = (int)routes[position].getDuration(); 
		if(duration / 3600 > 0){
			time.setText(String.format("%d h %d min", duration / 3600, (duration % 3600)/60));
		}else{
			time.setText(String.format("%d min", duration / 60));
		}
		TextView startTimeView = (TextView)layout.findViewById(R.id.textRouteTimeStart);
		String startTimeString = routes[position].getLegs().get(0).getLocs().get(0).getDepTime();
		String hour, minutes;
		hour = startTimeString.substring(8, 10);
		minutes = startTimeString.substring(10,12);
		startTimeView.setText(String.format("%s:%s", hour, minutes));

		//		phaseList.addView(startTimeView);
		TextView endTimeView = (TextView)layout.findViewById(R.id.textRouteTimeEnd);
		List<VOLeg> legs = routes[position].getLegs();
		List<VOLocation> locs = legs.get(legs.size()-1).getLocs();

		String endTimeString = locs.get(locs.size()-1).getArrTime();
		hour = endTimeString.substring(8, 10);
		minutes = endTimeString.substring(10,12);
		endTimeView.setText(String.format("%s:%s", hour, minutes));


		for(int i = 0;  i < routes[position].getLegs().size(); i++){
			TextView lin =  (TextView)mInflater.inflate(R.layout.listitem_route_phase, null);
			String type = routes[position].getLegs().get(i).getType();
			String code = routes[position].getLegs().get(i).getCode();
			double length = routes[position].getLegs().get(i).getLength();
			String shownCode = RouteAnalyzer.getRouteCode(type, code);
			if(code == null || code.length() == 0){
				//				tv2.setVisibility(View.GONE);
				if(length > 100){
					lin.setText(String.format("%.2f km", length / 1000));
				}else{
					lin.setText("<0.1 km");
				}
			}else{
				lin.setText(shownCode);
			}
			final int finalPos = position;
			final View finalView = row;
			lin.setCompoundDrawablesWithIntrinsicBounds(0,
					RouteTypes.getRouteTypeResource(routes[position].getLegs().get(i).getType()),
					0,0);
			phaseList.addView(lin);
			phaseList.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.getListView().performItemClick(finalView, finalPos, R.id.row_layout);
				}
		
			});
		}
		return row;
	}

	public VORoute[] getRoutes(){
		return this.routes;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			VORoute item = routes[position];
//			Intent intent = new Intent(activity, RouteResultsTabActivity.class);
			Intent intent = new Intent(activity, RouteResultsActivity.class);
			intent.putExtra("route", item);
			activity.startActivity(intent);
		}
	};

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
