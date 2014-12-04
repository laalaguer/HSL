package fi.aalto.hta.db.asyn;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import fi.aalto.hta.interfaces.FinishCallBackDbList;
import fi.aalto.hta.db.*;

public class GetOneLocationAsyn extends ListAsyn{
	final static String dialogTitle = "LIST LOCATIONS";
	final static String dialogMesage = "Progressing, please wait...";

	public GetOneLocationAsyn(Context mContext, FinishCallBackDbList activity) {
		super(mContext, activity, dialogTitle, dialogMesage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List doInBackground(Integer... arg0) {
		// TODO Auto-generated method stub
		
		ArrayList<FavoriteLocation> fls = new ArrayList<FavoriteLocation>();
		
		fls.add(dbMgr.queryLocation(arg0[0]));
		return fls;
	}

}
