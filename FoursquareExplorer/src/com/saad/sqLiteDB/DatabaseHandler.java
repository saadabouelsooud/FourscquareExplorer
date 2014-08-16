package com.saad.sqLiteDB;

import java.util.ArrayList;
import java.util.List;

import com.saad.objects.VenueData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "venuesManager";

	// Venues table name
	private static final String TABLE_VENUES = "venues";

	// venues Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LONGITUDE = "lng";
	private static final String KEY_LATITUDE = "lat";
	private static final String KEY_PHOTOURL = "photourl";
	private static final String KEY_PHOTOSDURL = "photosdcardUrl";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_VenueS_TABLE = "CREATE TABLE " + TABLE_VENUES + "("
				+ KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_LONGITUDE + " TEXT," + KEY_LATITUDE + " TEXT,"
				+ KEY_PHOTOURL + " TEXT," + KEY_PHOTOSDURL + " TEXT" + ")";
		db.execSQL(CREATE_VenueS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENUES);

		// Create tables again
		onCreate(db);
	}
    
	public void deleteAllRecords()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+ TABLE_VENUES);
		System.out.println("all records was deleted and db rows = "+getVenuesCount());
	}
	// Adding new venue
	public void addVenue(VenueData venue) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, venue.getId()); // Venue ID
		values.put(KEY_NAME, venue.getName()); // Venue Name
		values.put(KEY_LONGITUDE, venue.getLng()); // Venue longitude
		values.put(KEY_LATITUDE, venue.getLat()); // Venue latitude
		values.put(KEY_PHOTOURL, venue.getPhotURL()); // Venue photo url
		values.put(KEY_PHOTOSDURL, venue.getPhotoSDCardUrl()); // Venue photo
																// url on sd
																// card
		// Inserting Row
		db.insert(TABLE_VENUES, null, values);
		db.close(); // Closing database connection
	}

	// Getting single venue
	public VenueData getVenue(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_VENUES, new String[] { KEY_ID,
				KEY_NAME, KEY_LONGITUDE,KEY_LATITUDE,KEY_PHOTOURL,KEY_PHOTOSDURL }, KEY_ID + "=?",
				new String[] { id }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		VenueData venue = new VenueData(cursor.getString(0),
				cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
		// return Venue
		return venue;
	}
	
	// Getting All Venues
	 public List<VenueData> getAllVenues() {
	    List<VenueData> venueList = new ArrayList<VenueData>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_VENUES;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	VenueData Venue = new VenueData();
	            Venue.setId(cursor.getString(0));
	            Venue.setName(cursor.getString(1));
	            Venue.setLng(Double.parseDouble(cursor.getString(2)));
	            Venue.setLat(Double.parseDouble(cursor.getString(3)));
	            Venue.setPhotURL(cursor.getString(4));
	            Venue.setPhotoSDCardUrl(cursor.getString(5));
	            // Adding venue to list
	            venueList.add(Venue);
	        } while (cursor.moveToNext());
	    }
	 
	    // return Venue list
	    return venueList;
	}
	 
	// Getting venues Count
	    public int getVenuesCount() {
	        String countQuery = "SELECT * FROM " + TABLE_VENUES;
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        int count=cursor.getCount();
	        cursor.close();
	 
	        // return count
	        return count;
	    }
	    
	 // Updating single venue
	    public int updateVenue(VenueData venue) {
	        SQLiteDatabase db = this.getWritableDatabase();
	     
	        ContentValues values = new ContentValues();
	        values.put(KEY_NAME, venue.getName());
	        values.put(KEY_LONGITUDE, venue.getLng());
	        values.put(KEY_LATITUDE, venue.getLat());
	        values.put(KEY_PHOTOURL, venue.getPhotURL());
	        values.put(KEY_PHOTOSDURL, venue.getPhotoSDCardUrl());
	     
	        // updating row
	        return db.update(TABLE_VENUES, values, KEY_ID + " = ?",
	                new String[] { String.valueOf(venue.getId()) });
	    }
	    
	 // Deleting single Venue
	    public void deleteVenue(VenueData venue) {
	        SQLiteDatabase db = this.getWritableDatabase();
	        db.delete(TABLE_VENUES, KEY_ID + " = ?",
	                new String[] { String.valueOf(venue.getId()) });
	        db.close();
	    }
}
