package fi.aalto.hta.db;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import fi.aalto.hta.util.BitmapArrayConverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; 
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;


//Manager manages Locations into Database
public class DBManager {
	
	private DBHelper helper;  
	private SQLiteDatabase db; 
	
	
	// This means, manager can only be initial in Activity
	public DBManager(Context context){  
		helper = new DBHelper(context);  
		db = helper.getWritableDatabase();  
	}  
	
	// add favourite location into db
	public boolean addLocation(FavoriteLocation fl){
		ContentValues values = new ContentValues();
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_DES, fl.description);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT, fl.latitude);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON, fl.longitude);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_STREET, fl.street_info);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY, fl.type); 
		
		if(fl.image!=null){
			values.put(DBEntryContract.LocationEntry.COLUMN_NAME_PIC, BitmapArrayConverter.convertBitmapToByteArray(fl.image));
		}
		
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE, fl.title);
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
					DBEntryContract.LocationEntry.TABLE_NAME,
					null,
					values);
		
		if (newRowId != -1)
			return true;
		else
			return false;
	}
	
	
	// query all the locations in list,  return list of locations
	public List<FavoriteLocation> queryAllLocations() {
		ArrayList<FavoriteLocation> fls = new ArrayList<FavoriteLocation>(); 
		Cursor c = queryTheCursorLocation();  
		while(c.moveToNext()){
			FavoriteLocation fl = new FavoriteLocation();
			fl._id = c.getInt(c.getColumnIndex("_id"));
			fl.description = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_DES));
			fl.latitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT));
			fl.longitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON));
			fl.street_info = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_STREET));
			fl.type = c.getInt(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY));
			byte[] image_byte = c.getBlob(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_PIC)); 
			fl.image = BitmapArrayConverter.convertByteArrayToBitmap(image_byte);
			fl.title = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE));
			fls.add(fl);
		}
		// close the cursor
		c.close();
		return fls;
	}
	
	// query all the locations in list, return cursor
	public Cursor queryTheCursorLocation() {  
	    Cursor c = db.rawQuery("SELECT * FROM "+ DBEntryContract.LocationEntry.TABLE_NAME, null);  
	    return c;  
	}
	// query all the categories in list, return cursor
	public Cursor queryTheCursorCategory() {  
	    Cursor c = db.rawQuery("SELECT * FROM "+ DBEntryContract.CategoryEntry.TABLE_NAME, null);  
	    return c;  
	}
	// query all the locations in single category, return cursor
	public Cursor queryTheCursorLocationFromCategory(int categoryID) {  
	    Cursor c = db.rawQuery("SELECT * FROM "+ DBEntryContract.LocationEntry.TABLE_NAME + " WHERE " + DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY + " = " + String.valueOf(categoryID), null);  
	    return c;  
	}
	// query 1 location ,return cursor
	public Cursor queryTheCursorLocation(int locationID) {  
	    Cursor c = db.rawQuery("SELECT * FROM "+ DBEntryContract.LocationEntry.TABLE_NAME + " WHERE " + DBEntryContract.LocationEntry._ID + " = " + String.valueOf(locationID), null);  
	    return c;  
	} 
	
	
	// delete location with id
	public void deleteLocation(int id){
		db.delete(DBEntryContract.LocationEntry.TABLE_NAME, "_id = ?", new String[]{String.valueOf(id)});
	}
	
	// update location category
	public void updateLocationCategory(int categoryId, int id){
		ContentValues cv = new ContentValues(); 
		cv.put(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY, categoryId);  
		db.update(DBEntryContract.LocationEntry.TABLE_NAME, cv , "_id = ?", new String[]{String.valueOf(id)});
	}
	// update location picture
	public void updateLocationPicture(Bitmap image, int id){
		ContentValues cv = new ContentValues(); 
		cv.put(DBEntryContract.LocationEntry.COLUMN_NAME_PIC, BitmapArrayConverter.convertBitmapToByteArray(image));  
		db.update(DBEntryContract.LocationEntry.TABLE_NAME, cv , "_id = ?", new String[]{String.valueOf(id)});
	}
	// update location title
	public void updateLocationTitle(String title, int id){
		ContentValues cv = new ContentValues(); 
		cv.put(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE, title);  
		db.update(DBEntryContract.LocationEntry.TABLE_NAME, cv , "_id = ?", new String[]{String.valueOf(id)});
	}
	
	// update category name
	public void updateCategoryName(String title, int id){
		ContentValues cv = new ContentValues(); 
		cv.put(DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE, title);  
		db.update(DBEntryContract.CategoryEntry.TABLE_NAME, cv , "_id = ?", new String[]{String.valueOf(id)});
	}
	
	// update the whole location
	public void updateLocation(FavoriteLocation fl){
		ContentValues values = new ContentValues();
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_DES, fl.description);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT, fl.latitude);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON, fl.longitude);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_STREET, fl.street_info);
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY, fl.type); 
		
		if(fl.image!=null){
			values.put(DBEntryContract.LocationEntry.COLUMN_NAME_PIC, BitmapArrayConverter.convertBitmapToByteArray(fl.image));
		}
		
		values.put(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE, fl.title);
		
		db.update(DBEntryContract.CategoryEntry.TABLE_NAME, values , "_id = ?", new String[]{String.valueOf(fl._id)});
	}
	
	public int queryLocationEntryNumberByCategory(int categoryId){
		int entries = 0 ;
		Cursor c = queryTheCursorLocationFromCategory(categoryId); 
		while(c.moveToNext()){
			entries++;
		}
		c.close();
		return entries;
	}
	
	
	// list all places belongs to the same category
	public List<FavoriteLocation> queryLocationByCategory(int categoryId){
		ArrayList<FavoriteLocation> fls = new ArrayList<FavoriteLocation>(); 
		Cursor c = queryTheCursorLocationFromCategory(categoryId);  
		while(c.moveToNext()){
			FavoriteLocation fl = new FavoriteLocation();
			fl._id = c.getInt(c.getColumnIndex("_id"));
			fl.description = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_DES));
			fl.latitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT));
			fl.longitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON));
			fl.street_info = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_STREET));
			fl.type = c.getInt(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY));
			byte[] image_byte = c.getBlob(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_PIC)); 
			if(image_byte!=null){
				fl.image = BitmapArrayConverter.convertByteArrayToBitmap(image_byte);
			}
			fl.title = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE));
			fls.add(fl);
		}
		// close the cursor
		c.close();
		return fls;
	}
	
	// return locations under specific cateogry name
	public List<FavoriteLocation> queryLocationByCategory(String categoryName){
		// We will not detect Home, HOME, or home
		Cursor c = db.rawQuery("SELECT * FROM "+ DBEntryContract.CategoryEntry.TABLE_NAME + " WHERE " + DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE + " = ? COLLATE NOCASE", new String[]{new String(String.valueOf(categoryName))});
		ArrayList<FavoriteLocation> fls = new ArrayList<FavoriteLocation>(); 
		if (c.moveToFirst()){
			// if c is not empty
			fls = (ArrayList<FavoriteLocation>) queryLocationByCategory(c.getInt(c.getColumnIndex("_id")));
		}
		c.close();
		return fls;
	}
	
	public FavoriteLocation queryLocation(int locationId){
		Cursor c = queryTheCursorLocation(locationId);
		c.moveToFirst();
		FavoriteLocation fl = new FavoriteLocation();
		fl._id = c.getInt(c.getColumnIndex("_id"));
		fl.description = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_DES));
		fl.latitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LAT));
		fl.longitude = c.getDouble(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_GEO_LON));
		fl.street_info = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_STREET));
		fl.type = c.getInt(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY));
		byte[] image_byte = c.getBlob(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_PIC)); 
		if(image_byte!=null){
			fl.image = BitmapArrayConverter.convertByteArrayToBitmap(image_byte);
		}
		fl.title = c.getString(c.getColumnIndex(DBEntryContract.LocationEntry.COLUMN_NAME_TITLE));
		return fl;
	}
	
	// list all categories in DB
	public List<Category> queryExistingCategory(){
		ArrayList<Category> cs = new ArrayList<Category>(); 
		Cursor c = queryTheCursorCategory();  
		while(c.moveToNext()){
			Category category = new Category();
			category._id = c.getInt(c.getColumnIndex("_id"));
			category.title = c.getString(c.getColumnIndex(DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE));
			category.numberOfEntries = this.queryLocationEntryNumberByCategory(category._id);
			cs.add(category);
		}
		// close the cursor
		c.close();
		return cs;
		
	}
	
	// delete single category (and locations belongs to it)
	public void deleteCategory(int id){
		// delete all the values in location
		db.delete(DBEntryContract.LocationEntry.TABLE_NAME, DBEntryContract.LocationEntry.COLUMN_NAME_CATEGORY +" = ?", new String[]{String.valueOf(id)});
		// delete the category
		db.delete(DBEntryContract.CategoryEntry.TABLE_NAME, DBEntryContract.CategoryEntry._ID+"= ?", new String[]{String.valueOf(id)});
	}
	
	
	// add a category to DB
	public boolean addCategory(Category c){
		ContentValues values = new ContentValues();
		values.put(DBEntryContract.CategoryEntry.COLUMN_NAME_TITLE, c.title);
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
					DBEntryContract.CategoryEntry.TABLE_NAME,
					null,
					values);
		
		if (newRowId != -1)
			return true;
		else
			return false;
	}
	
	// close db
	public void closeDB(){
		db.close();
	}

}
