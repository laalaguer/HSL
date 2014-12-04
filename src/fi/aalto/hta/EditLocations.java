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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import fi.aalto.hta.db.FavoriteLocation;
import fi.aalto.hta.db.asyn.DeleteCategoryAsyn;
import fi.aalto.hta.db.asyn.DeleteLocationAsyn;
import fi.aalto.hta.db.asyn.ListLocationAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbList;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class EditLocations extends Activity implements FinishCallBackDbList,FinishCallBackDbVoid{
	
	// Request for add a new location
	final private int REQUEST_CODE_ADD_LOCATION=1;
	final private int REQUEST_CODE_UPDATE_LOCATION=2;
	static final public String CATEGORY_ID = "category_id";
	static final public String LOCATION_ID = "location_id";
	static final public String UPDATE = "UPDATE";
	// List of locations
	ListView locationsList ;
	// Category Id passed to this activity
	int categoryId;
	// Array List for storing checked/unchecked locations
	ArrayList<Integer> selectedLocations= new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_locations);
		setTitle("Edit Locations");
		locationsList = (ListView) findViewById(R.id.edit_list_of_locations);
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		categoryId = Integer.valueOf(b.getString(EditCategories.EDIT_LOCATION));
		this.updateMyView(categoryId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_locations, menu);
		return true;
	}
	
	public void setupLocation(List<FavoriteLocation> fls){
		
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(FavoriteLocation fl: fls){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Title", fl.title);
			map.put("Pic", fl.image);
			map.put("Street", fl.street_info);
			map.put("Id", String.valueOf(fl._id));
			map.put("Des", fl.description);
			map.put("gps", fl.getGeoLngLat());
			list.add(map);
		}	
		
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.edit_item_location,  
				new String[]{"Title","Pic","Street","Id","Des","gps"}, 
				new int[]{R.id.nest_edit_item_location_title,R.id.nest_edit_item_location_pic,R.id.nest_edit_item_location_street,R.id.nest_edit_item_location_id,R.id.nest_edit_item_location_des,R.id.nest_edit_item_location_gps});
		
		locationsList.setAdapter(adapter);
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
		
		locationsList.setOnItemClickListener(new LocationListItemOnClickListener());
	}
	
	class LocationListItemOnClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(EditLocations.this,CreateNewLocation.class);
			TextView idToPass = (TextView)arg1.findViewById(R.id.nest_edit_item_location_id);
			intent.putExtra(LOCATION_ID, Integer.valueOf(idToPass.getText().toString()));			
			intent.putExtra(UPDATE, true);
			intent.putExtra(CATEGORY_ID, categoryId);
			EditLocations.this.startActivityForResult(intent,REQUEST_CODE_UPDATE_LOCATION);
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.action_new_location) {
			Intent intent = new Intent(EditLocations.this,CreateNewLocation.class);
			intent.putExtra(CATEGORY_ID, categoryId);
			EditLocations.this.startActivityForResult(intent,REQUEST_CODE_ADD_LOCATION);
		} else if (item.getItemId() == R.id.action_delete_location) {
			// do the delete location in background then return;
			showDeleteAlert();
		} else {
			
		}

	    return true;
	  }

	public void checkMyBox(View v){
		View parent = (View)v.getParent();
		TextView idToPass = (TextView)parent.findViewById(R.id.nest_edit_item_location_id);
		int id = Integer.valueOf((String) idToPass.getText());
		
		// if v is checked, see it is check or unchecked
		// if it is checked, add an item to current storing list
		// if it is unchecked, delete an item from current storing list
		CheckBox checkbox = (CheckBox) v;
		if(checkbox.isChecked()){
			selectedLocations.add(id);
		}else{
			selectedLocations.remove(selectedLocations.indexOf(id));
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
	public void doneByDbVoid() {
		// TODO Auto-generated method stub
		this.updateMyView(categoryId);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditLocations.this.updateMyView(categoryId);
		Log.d("Update category","From New");
    }

	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
	}

	/**
     * Function to show settings alert dialog
     * On pressing Confirm button will launch deletion
     * */
    public void showDeleteAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
 
        // Setting Dialog Title
        alertDialog.setTitle("Apply Changes");
 
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to delete selected items?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	new DeleteLocationAsyn(EditLocations.this,EditLocations.this).execute(selectedLocations);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }

}
