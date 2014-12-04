package fi.aalto.hta.db.asyn;

import android.content.Context;
import android.os.AsyncTask;
import fi.aalto.hta.db.Category;
import fi.aalto.hta.db.DBManager;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class CreateCategoryAsyn extends AsyncTask<Category, Integer, Void>{
	Context mContext;
	ShowDialogProgress dialogMgr;
	String dialogTitle = "CREATE CATEGORY";
	String dialogMesage = "Progressing, please wait...";
	DBManager dbMgr;
	FinishCallBackDbVoid activity;
	
	public CreateCategoryAsyn(Context mContext, FinishCallBackDbVoid activity){
		 super();
		 this.mContext = mContext;
		 this.activity = activity;
		 dialogMgr = new ShowDialogProgress(dialogTitle, dialogMesage, mContext);
		 dbMgr = new DBManager(this.mContext);
		 
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
	protected Void doInBackground(Category... cs) {
		// TODO Auto-generated method stub
		dbMgr.addCategory(cs[0]);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		dialogMgr.dismissDialog();
		dbMgr.closeDB();
		activity.doneByDbVoid();
		super.onPostExecute(result);
	}
}
