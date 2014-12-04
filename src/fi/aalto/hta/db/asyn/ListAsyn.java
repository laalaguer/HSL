package fi.aalto.hta.db.asyn;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import fi.aalto.hta.db.DBManager;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public abstract class ListAsyn extends AsyncTask<Integer, Integer, List>{

	Context mContext;
	ShowDialogProgress dialogMgr;
	String dialogTitle;
	String dialogMessage;
	DBManager dbMgr;
	FinishCallBackDbList activity;
	
	public ListAsyn(Context mContext, FinishCallBackDbList activity, String title, String msg){
		 super();
		 this.dialogMessage = msg;
		 this.dialogTitle = title;
		 this.mContext = mContext;
		 dialogMgr = new ShowDialogProgress(dialogTitle, dialogMessage, mContext);
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
	protected abstract List doInBackground(Integer... arg0);
	
	@Override
	protected void onPostExecute(List result) {
		dialogMgr.dismissDialog();
		dbMgr.closeDB();
		activity.doneByDbList(result);
		super.onPostExecute(result);
	}
}
