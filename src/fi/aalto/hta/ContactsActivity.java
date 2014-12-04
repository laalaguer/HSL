package fi.aalto.hta;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactsActivity extends ListActivity {
	private Button buttonCancel = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		buttonCancel = (Button)findViewById(R.id.buttonCancel);
		ContentResolver cr = getContentResolver();
		String addrWhere = ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] addrWhereParams = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null); 
		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.listitem_contact, cur,
				new String[] {ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.StructuredPostal.STREET,
				ContactsContract.CommonDataKinds.StructuredPostal.CITY
		},
		new int[] {R.id.textContactName, R.id.textContactStreet, R.id.textContactCity});

		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				String street = ((TextView)view.findViewById(R.id.textContactStreet)).getText().toString();
				String city = ((TextView)view.findViewById(R.id.textContactCity)).getText().toString();
				intent.putExtra("address", street + ", " + city);
				ContactsActivity.this.setResult(RESULT_OK, intent);
				ContactsActivity.this.finish();
			}    
		});

		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ContactsActivity.this.setResult(RESULT_CANCELED, null);
				ContactsActivity.this.finish();

			}
		});
	}
}
