package fi.aalto.hta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fi.aalto.hta.db.Category;
import fi.aalto.hta.db.asyn.ListCategoryAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public class ViewCategories extends Activity implements FinishCallBackDbList{
	
	ListView categories ;
	private final int REQUEST_FOR_LOCATION = 1;
	static final public String CATEGORY_ID = "category_id"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_categories);
		setTitle("Category");
		//getActionBar().setIcon(R.drawable.my_icon);
		
		categories = (ListView) findViewById(R.id.list_of_categories);
		this.updateMyView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_locations, menu);
		return true;
	}

	public void setupCategory(List<Category> category){
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for(Category c: category){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Title", c.getTitle());
			map.put("Id", String.valueOf(c.getId()));
			map.put("Entries", c.getNumberOfEntries()+" Place(s)");
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_item_category,  
				new String[]{"Title","Id","Entries"}, new int[]{R.id.nest_list_item_category_title,R.id.nest_list_item_category_id,R.id.nest_list_item_category_entries});
		
		categories.setAdapter(adapter);
		
		categories.setOnItemClickListener(new CategoryClickListener());
	}
	
	class  CategoryClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long row) {
			TextView categoryIdToPass = (TextView)v.findViewById(R.id.nest_list_item_category_id);
			Intent intent = new Intent(ViewCategories.this, ViewLocations.class);
			intent.putExtra(CATEGORY_ID,categoryIdToPass.getText());
			startActivityForResult(intent, REQUEST_FOR_LOCATION);
			overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
		}
		
	}  
	

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == this.REQUEST_FOR_LOCATION){
			if(resultCode == RESULT_OK){
				ViewCategories.this.setResult(resultCode, data);
				ViewCategories.this.finish();
			}
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
}
