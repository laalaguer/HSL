package fi.aalto.hta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import fi.aalto.hta.R;
import fi.aalto.hta.db.FavoriteLocation;
import fi.aalto.hta.db.asyn.ListLocationAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public class ViewLocations extends Activity implements FinishCallBackDbList{

	ListView locations ;
	// Category Id passed to this activity
	int categoryId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_locations);
		setTitle("Locations");
		locations = (ListView) findViewById(R.id.list_of_locations);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		categoryId = Integer.valueOf(b.getString(ViewCategories.CATEGORY_ID));
		this.updateMyView(categoryId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_locations, menu);
		return true;
	}
	
	public void popToast(View v){
		View parentView = (View)v.getParent();
		TextView geoInfo = (TextView)parentView.findViewById(R.id.my_item_geo);
		TextView titleInfo = (TextView)parentView.findViewById(R.id.my_item_title);
		TextView desInfo = (TextView)parentView.findViewById(R.id.my_item_description);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   dialog.cancel();
	           }
	       });
		
		builder.setTitle(titleInfo.getText());
		builder.setMessage("Geo Location: "+ geoInfo.getText()+"\n"+"Description:"+desInfo.getText());
		builder.show();
	}
	
	public void setupLocation(List<FavoriteLocation> fls){
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(FavoriteLocation fl: fls){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Title", fl.title);
			map.put("Pic", fl.image);
			map.put("Street", fl.street_info);
			map.put("Id", String.valueOf(fl._id));
			map.put("Geo", fl.getGeoLngLat());
			map.put("Des", fl.description);
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_item_location,  
				new String[]{"Title","Pic","Street","Id","Geo","Des"}, new int[]{R.id.my_item_title,R.id.my_item_pic,R.id.my_item_street,R.id.my_item_id,R.id.my_item_geo,R.id.my_item_description});
		
		locations.setAdapter(adapter);
		adapter.setViewBinder(new ViewBinder(){
            public boolean setViewValue(View view,Object data,String textRepresentation){
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv=(ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                }
                else return false;
            }
        });
		
		locations.setOnItemClickListener(new CategoryClickListener());
	}
	
	class  CategoryClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long row) {
			
			
			TextView street = (TextView)v.findViewById(R.id.my_item_street);
			TextView geo = (TextView)v.findViewById(R.id.my_item_geo);
			Intent intent = new Intent();
			intent.putExtra("Street", street.getText());
			intent.putExtra("Geo", geo.getText());
			
			ViewLocations.this.setResult(RESULT_OK, intent);
			ViewLocations.this.finish();
			
		}
		
	}

	public void updateMyView(int categoryId){
		new ListLocationAsyn(this,this).execute(categoryId);
	}
	
	@Override
	public void doneByDbList(List list) {
		// TODO Auto-generated method stub
		setupLocation(list);
	}  
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}
	
	

}
