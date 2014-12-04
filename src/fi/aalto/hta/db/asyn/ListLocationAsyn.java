package fi.aalto.hta.db.asyn;

import java.util.List;

import android.content.Context;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public class ListLocationAsyn extends ListAsyn{
	final static String dialogTitle = "LIST LOCATIONS";
	final static String dialogMesage = "Progressing, please wait...";
	
	public ListLocationAsyn(Context mContext, FinishCallBackDbList activity) {
		super(mContext, activity, dialogTitle, dialogMesage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List doInBackground(Integer... id) {
		// TODO Auto-generated method stub
		return dbMgr.queryLocationByCategory(id[0]);
	}
	

}
