package fi.aalto.hta.db;

import android.provider.BaseColumns;

public class DBEntryContract {
	
	// protected constructor
	private DBEntryContract(){}
	
	// Class for define the location
	// Once implemented BaseColumns, it has _id property
	public static abstract class LocationEntry implements BaseColumns {
	    public static final String TABLE_NAME = "location";
	    public static final String COLUMN_NAME_GEO_LAT = "latitude";
	    public static final String COLUMN_NAME_GEO_LON = "longitude";
	    public static final String COLUMN_NAME_STREET = "street";
	    public static final String COLUMN_NAME_DES = "description";
	    public static final String COLUMN_NAME_CATEGORY = "category";
	    public static final String COLUMN_NAME_PIC = "picture";
	    public static final String COLUMN_NAME_TITLE = "title";
	}
	
	// Class for define the category
	// Once implemented BaseColumns, it has _id proerty
	public static abstract class CategoryEntry implements BaseColumns {
	    public static final String TABLE_NAME = "category";
	    public static final String COLUMN_NAME_TITLE = "title";		
	}
	
}
