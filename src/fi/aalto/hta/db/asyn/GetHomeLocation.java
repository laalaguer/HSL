package fi.aalto.hta.db.asyn;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import fi.aalto.hta.db.FavoriteLocation;
import fi.aalto.hta.interfaces.FinishCallBackDbList;

public class GetHomeLocation extends ListAsyn{
	final static String dialogTitle = "GET HOME LOCATIONS";
	final static String dialogMesage = "Progressing, please wait...";
	final static String HOME_CATEGORY = "HOME";
	
	public GetHomeLocation(Context mContext, FinishCallBackDbList activity) {
		super(mContext, activity, dialogTitle, dialogMesage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		return dbMgr.queryLocationByCategory(HOME_CATEGORY);
		
	}
}
