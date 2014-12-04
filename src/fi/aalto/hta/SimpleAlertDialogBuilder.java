package fi.aalto.hta;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class SimpleAlertDialogBuilder extends Builder {

	public SimpleAlertDialogBuilder(Context context, String title, String message) {
		super(context);
		if(title != null){
			this.setTitle(title);
		}
		this.setMessage(message);
		this.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}

}
