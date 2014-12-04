package fi.aalto.hta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import fi.aalto.hta.R;
import fi.aalto.hta.db.Category;
import fi.aalto.hta.db.asyn.DeleteCategoryAsyn;
import fi.aalto.hta.db.asyn.ListCategoryAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbList;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class EditCategories extends Activity implements FinishCallBackDbList , FinishCallBackDbVoid{

	final private int REQUEST_CODE_ADD_CATEGORY=1;
	final private int REQUEST_CODE_EDIT_LOCATION=2;
	final private int REQUEST_CODE_MODIFY_CATEGORY=3;
	static final public String EDIT_LOCATION = "edit_location"; 
	ListView categoriesList ;
	// Array List for storing checked/unchecked locations
	ArrayList<Integer> selectedCategories= new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_categories);
		setTitle("Edit Categories");
		categoriesList = (ListView) findViewById(R.id.edit_list_of_categories);
		this.updateMyView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_categories, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.action_new_category) {
			EditCategories.this.startActivityForResult(new Intent(EditCategories.this,CreateNewCategory.class),REQUEST_CODE_ADD_CATEGORY);
		} else if (item.getItemId() == R.id.action_delete_cateogry) {
			// do the delete location in background then return;
			showDeleteAlert();
		} else {
		}

	    return true;
	  } 
	
	public void checkMyBox(View v){
		View parent = (View)v.getParent();
		TextView idToPass = (TextView)parent.findViewById(R.id.nest_edit_item_category_id);
		int id = Integer.valueOf((String) idToPass.getText());
		
		// if v is checked, see it is check or unchecked
		// if it is checked, add an item to current storing list
		// if it is unchecked, delete an item from current storing list
		CheckBox checkbox = (CheckBox) v;
		if(checkbox.isChecked()){
			selectedCategories.add(id);
		}else{
			selectedCategories.remove(selectedCategories.indexOf(id));
		}
	}
	
	
	public void goToEditLocations(View v){
		TextView idToPass = (TextView)v.findViewById(R.id.nest_edit_item_category_id);
		Intent intent = new Intent(EditCategories.this,EditLocations.class);
		intent.putExtra(EDIT_LOCATION,idToPass.getText());
		startActivityForResult(intent,REQUEST_CODE_EDIT_LOCATION);
		overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		Log.d("Category is", (String)idToPass.getText());
	}
	
	public void gotoModifyCategory(View v){
		View parent = (View)v.getParent();
		TextView idToPass = (TextView)parent.findViewById(R.id.nest_edit_item_category_id);
		int id = Integer.valueOf((String) idToPass.getText());
		Intent intent = new Intent(EditCategories.this,UpdateCategory.class);
		intent.putExtra("id", id);
		TextView titleToPass = (TextView)parent.findViewById(R.id.nest_edit_item_category_title);
		intent.putExtra("title", titleToPass.getText().toString());
		EditCategories.this.startActivityForResult(intent,REQUEST_CODE_MODIFY_CATEGORY);
	}
		
	
	public void setupCategory(List<Category> category){
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(Category c: category){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Title", c.getTitle());
			map.put("Id", String.valueOf(c._id));
			map.put("Icon", R.drawable.white_content_edit);
			map.put("Entries", c.getNumberOfEntries()+" Place(s)");
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.edit_item_category,  
				new String[]{"Title","Id","Icon","Entries"}, new int[]{R.id.nest_edit_item_category_title,R.id.nest_edit_item_category_id,R.id.nest_edit_image_to_next,R.id.nest_edit_item_category_entries});
		
		categoriesList.setAdapter(adapter);
		categoriesList.setOnItemClickListener(new CategoryListItemOnClickListener());
	}
	
	class CategoryListItemOnClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			EditCategories.this.goToEditLocations(arg1);
		}
		
	}
	
	
	public void updateMyView(){
		new ListCategoryAsyn(this,this).execute();
	}
	
	@Override
	public void doneByDbList(List list) {
		// TODO Auto-generated method stub
		setupCategory(list);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditCategories.this.updateMyView();
		Log.d("Update category","From New");
    }

	@Override
	public void doneByDbVoid() {
		// TODO Auto-generated method stub
		this.updateMyView();
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
            	new DeleteCategoryAsyn(EditCategories.this,EditCategories.this).execute(selectedCategories);
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
