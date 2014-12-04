package fi.aalto.hta.db.asyn;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import fi.aalto.hta.db.DBManager;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public abstract class DeleteAsyn extends AsyncTask<ArrayList<Integer>, Integer, Void>{
	Context mContext;
	ShowDialogProgress dialogMgr;
	String dialogTitle;
	String dialogMesage;
	DBManager dbMgr;
	FinishCallBackDbVoid activity;
	
	public DeleteAsyn(Context mContext, FinishCallBackDbVoid activity, String title, String msg){
		 super();
		 this.dialogMesage = msg;
		 this.dialogTitle = title;
		 this.mContext = mContext;
		 dialogMgr = new ShowDialogProgress(dialogTitle, dialogMesage, mContext);
		 dbMgr = new DBManager(this.mContext);
		 this.activity = activity;
	}
	
	@Override
    protected void onPreExecute() {
		super.onPreExecute();
		dialogMgr.showDialog();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
        dialogMgr.setProgress(progress[0]);
    }
	
	
	@Override
	protected abstract Void doInBackground(ArrayList<Integer>... id);
	
	@Override
	protected void onPostExecute(Void result) {
		dialogMgr.dismissDialog();
		dbMgr.closeDB();
		activity.doneByDbVoid();
		super.onPostExecute(result);
	}
}
