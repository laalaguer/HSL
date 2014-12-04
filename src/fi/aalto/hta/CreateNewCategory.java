package fi.aalto.hta;



//import fi.aalto.hta.activity.interfaces.FinishCallBackDbVoid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fi.aalto.hta.R;
import fi.aalto.hta.db.Category;
import fi.aalto.hta.db.asyn.CreateCategoryAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class CreateNewCategory extends Activity implements FinishCallBackDbVoid{
	// Edit text 
    EditText title;
    Category category = new Category();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_category);
		setTitle("Create New Category");
		title = (EditText) findViewById(R.id.create_category_title_field);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_category, menu);
		return true;
	}
	
	public void createCategory(View v){
		Toast t = new Toast(v.getContext());
		t.setDuration(Toast.LENGTH_LONG);
		if (isEmpty(title)){
			Toast.makeText(v.getContext(), "At least fill Name", Toast.LENGTH_LONG).show();
		}else{
			category.setTitle(title.getText().toString());
			new CreateCategoryAsyn(v.getContext(),CreateNewCategory.this).execute(category);
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

	@Override
	public void doneByDbVoid() {
		// TODO Auto-generated method stub
		this.setResult(RESULT_OK);
		this.finish();
	}

}
