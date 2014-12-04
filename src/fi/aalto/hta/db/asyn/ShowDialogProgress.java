package fi.aalto.hta.db.asyn;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * 
 * @author Xiqing Chu
 * this class should be inherited by other classes wants to pop out dialog
 */

public class ShowDialogProgress {
	Context mContext;
	ProgressDialog progressDialog;
	String dialogTitle;
	String dialogMessage;
	
	public ShowDialogProgress(String title,String msg, Context context){
		this.dialogTitle = title;
		this.dialogMessage = msg;
		this.mContext = context;
	}
	
	public void showDialog(){
		progressDialog = ProgressDialog.show(mContext, dialogTitle, dialogMessage, true, false);
	}
	
	public void dismissDialog(){
		progressDialog.dismiss();
	}
	
	public void setProgress(int value){
		progressDialog.setProgress(value);
	}
}
