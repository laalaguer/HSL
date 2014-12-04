package fi.aalto.hta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fi.aalto.hta.util.DistancePointSegmentCalculator;
import fi.aalto.hta.vos.VOLeg;
import fi.aalto.hta.vos.VOLocation;

public class RouteInfoArrayAdapter extends ArrayAdapter<VOLeg>{
	ArrayList<ArrayList<String>> list = null;
	LayoutInflater mInflater = null;
	private VOLeg[] legs = null;
	private int detectedNextStopLegInt = -1;
	private int detectedNextStopInt = -1;
	
	@SuppressWarnings("unused")
	private ListActivity activity = null;


	public RouteInfoArrayAdapter(ListActivity activity, int textViewResourceId, VOLeg[] legs) {
		super(activity, textViewResourceId, legs);
		mInflater = LayoutInflater.from(activity);
		this.legs = legs;
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;

		if (null == convertView) {
			row = mInflater.inflate(R.layout.listitem_route_info_part, parent, false);
		} else {
			row = convertView;
		}
		TextView textPartTimeStart = (TextView)row.findViewById(R.id.textPartTime);
		TextView textPartTimeEnd = (TextView)row.findViewById(R.id.textPartTimeEnd);
		TextView textLineCode = (TextView)row.findViewById(R.id.textLineCode);
		TextView textStopsList = (TextView)row.findViewById(R.id.textStopsList);
		String hour, minutes;
		hour = legs[position].getLocs().get(0).getDepTime().substring(8, 10);
		minutes = legs[position].getLocs().get(0).getDepTime().substring(10,12);
		textPartTimeStart.setText(String.format("%s:%s", hour, minutes));
		textLineCode.setCompoundDrawablesWithIntrinsicBounds(0,
				RouteTypes.getRouteTypeResource(legs[position].getType()),
				0,0);
		String endTimeString = legs[position].getLocs().get(legs[position].getLocs().size()-1).getArrTime();
		hour = endTimeString.substring(8, 10);
		minutes = endTimeString.substring(10,12);
		textPartTimeEnd.setText(String.format("%s:%s", hour, minutes));
		String type = legs[position].getType();
		String code = legs[position].getCode();
		String shownCode = RouteAnalyzer.getRouteCode(type, code);
		if(shownCode != null){
			textLineCode.setText(shownCode);
		}else{
			textLineCode.setText("");
		}
		StringBuilder multilineStr = new StringBuilder();
		Boolean isNext = false;
		if(legs[position].getType().equals("walk")){
			isNext = position == detectedNextStopLegInt && detectedNextStopInt == 1;
			if(position == legs.length - 1){
				multilineStr.append("Walk");
			}else{
				VOLocation loc = legs[position].getLocs().get(legs[position].getLocs().size()-1);
				multilineStr.append(String.format("Walk to %s (%s) %s%n", loc.getName(), loc.getShortCode(), isNext? "(Next)" : ""));
			}
			
		}else{
			Iterator<VOLocation> it = legs[position].getLocs().iterator();
			int i = 0;
			while(it.hasNext()){
				isNext = position == detectedNextStopLegInt && detectedNextStopInt == i;
				VOLocation loc = it.next();
				hour = loc.getDepTime().substring(8, 10);
				minutes = loc.getDepTime().substring(10,12);
				
				multilineStr.append(String.format("%s:%s %s (%s) %s%n", hour, minutes, loc.getName(), loc.getShortCode(), isNext? "(Next)" : ""));
				i++;
			}
		}
		textStopsList.setText(multilineStr.toString());


		return row;
	}

	public VOLeg[] getArray(){
		return this.legs;
	}
	
	public void updateNextPosition(Location userLocation){
		int[] result = DistancePointSegmentCalculator.updateNextPosition(new ArrayList<VOLeg>(Arrays.asList(legs)), userLocation);
		detectedNextStopLegInt = result[0];
		detectedNextStopInt = result[1];
		notifyDataSetChanged();
	}

}
