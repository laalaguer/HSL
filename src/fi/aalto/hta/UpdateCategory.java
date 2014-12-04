package fi.aalto.hta;

import java.util.ArrayList;

import fi.aalto.hta.db.Category;
import fi.aalto.hta.db.asyn.CreateCategoryAsyn;
import fi.aalto.hta.db.asyn.UpdateCategoryNameAsyn;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateCategory extends Activity implements FinishCallBackDbVoid{
	// Edit text 
    EditText title;
    Category category = new Category();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_category);
		setTitle("Modify Category");
		Intent intent = getIntent();
		if(intent.getExtras()!=null){
			category._id = intent.getExtras().getInt("id");
		}
		title = (EditText) findViewById(R.id.update_category_title_field);
		title.setText(intent.getExtras().getString("title"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_category, menu);
		return true;
	}
	
	
	public void updateCategory(View v){
		Toast t = new Toast(v.getContext());
		t.setDuration(Toast.LENGTH_LONG);
		if (isEmpty(title)){
			Toast.makeText(v.getContext(), "At least fill Name", Toast.LENGTH_LONG).show();
		}else{
			category.setTitle(title.getText().toString());
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(category._id);
			new UpdateCategoryNameAsyn(v.getContext(),UpdateCategory.this, category.title).execute(a);
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
