package fi.aalto.hta.db;

import android.content.ContentValues;
import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  


public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "test.db";  
	private static final int DATABASE_VERSION = 4;  
	private static final String[] INITIAL_CATEGORY_VALUES = {"Home","Bus Stop","Restaurant","Bar","Office","General"};

	public DBHelper(Context context){
		// Setup DB with name and version
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	}

	// Initiate once and only on create
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create a location table
		db.execSQL(SQL_CREATE_LOCATION_ENTRIES);
		// Create a Category table
		db.execSQL(SQL_CREATE_CATEGORY_ENTRIES);
		// Add some default categories
		addCategory(db, INITIAL_CATEGORY_VALUES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop existing table, then create a new one
        db.execSQL(SQL_DELETE_LOCATION_ENTRIES);
        db.execSQL(SQL_DELETE_CATEGORY_ENTRIES);
        onCreate(db);
	}
	
	// add a category to DB
	public void addCategory(SQLiteDatabase db,String[] titles){
		for (String t: titles){
			ContentValues values = new ContentValues();
			values.put(DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE, t);
			db.insert(DBEntryContract.CategoryEntry.TABLE_NAME,	null,values);
		}
	}
	
	
	
	// SQLite types that commonly used
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String BLOB_TYPE = " BLOB";
	private static final String COMMA_SEP = ",";
	
	
	// Statements that commonly used
	private static final String SQL_CREATE_LOCATION_ENTRIES =
	    "CREATE TABLE " + DBEntryContract.LocationEntry.TABLE_NAME + " (" +
	    DBEntryContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
	    DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT + REAL_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON + REAL_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_STREET + TEXT_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_DES + TEXT_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_PIC + BLOB_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	    DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY + INTEGER_TYPE +
	    " )";

	private static final String SQL_DELETE_LOCATION_ENTRIES =
	    "DROP TABLE IF EXISTS " + DBEntryContract.LocationEntry.TABLE_NAME;
	
	// Statements that commonly used
	private static final String SQL_CREATE_CATEGORY_ENTRIES =
	    "CREATE TABLE " + DBEntryContract.CategoryEntry.TABLE_NAME + " (" +
	    DBEntryContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
	    DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE + TEXT_TYPE +
	    " )";

	private static final String SQL_DELETE_CATEGORY_ENTRIES =
	    "DROP TABLE IF EXISTS " + DBEntryContract.CategoryEntry.TABLE_NAME;
}
