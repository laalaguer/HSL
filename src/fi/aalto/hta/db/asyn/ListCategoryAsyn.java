package fi.aalto.hta.db.asyn;

import java.util.List;

import android.content.Context;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public class ListCategoryAsyn extends ListAsyn{
	
	final static String dialogTitle = "LIST CATEGORY";
	final static String dialogMesage = "Progressing, please wait...";

	public ListCategoryAsyn(Context mContext, FinishCallBackDbList activity) {
		super(mContext, activity, dialogTitle, dialogMesage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		return dbMgr.queryExistingCategory();
	}
	


}
