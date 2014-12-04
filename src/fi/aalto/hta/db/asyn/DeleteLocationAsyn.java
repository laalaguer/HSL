package fi.aalto.hta.db.asyn;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class DeleteLocationAsyn extends DeleteAsyn{

	final static String dialogTitle = "DELETE LOCATION";
	final static String dialogMesage = "Progressing, please wait...";
	
	public DeleteLocationAsyn(Context mContext, FinishCallBackDbVoid activity) {
		super(mContext, activity, dialogTitle, dialogMesage);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground(ArrayList<Integer>... id) {
		// TODO Auto-generated method stub
		Iterator<Integer> it = id[0].iterator();
		while(it.hasNext()){
			dbMgr.deleteLocation(it.next());
		}
			
		return null;
	}
}
