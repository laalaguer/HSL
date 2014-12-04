package fi.aalto.hta.db.asyn;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import fi.aalto.hta.interfaces.FinishCallBackDbVoid;

public class UpdateCategoryNameAsyn extends DeleteAsyn{
	final static String dialogTitle = "Update CATEGORY";
	final static String dialogMesage = "Progressing, please wait...";
	String name;

	public UpdateCategoryNameAsyn(Context mContext, FinishCallBackDbVoid activity, String categoryName) {
		super(mContext, activity, dialogTitle, dialogMesage);
		this.name = categoryName;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Void doInBackground(ArrayList<Integer>... id) {
		// TODO Auto-generated method stub
		dbMgr.updateCategoryName(name, id[0].get(0));
		return null;
	}

}
