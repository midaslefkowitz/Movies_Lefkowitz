package com.example.movies_lefkowitz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class MoviesDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "films.db";
	public static final String MOVIES_TABLE_NAME = "films";
	public static final String MOVIES_ID = "_id";
	public static final String MOVIES_TITLE = "title";
	public static final String MOVIES_BODY = "body";
	public static final String MOVIES_URL = "url";
	public static final String LOG_TAG = "Movies_DB";
	
	public int DATABASE_VERSION = 1;
	
	public MoviesDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "Creating movie table");
		
		String CREATE_MOVIES_TABLE = "CREATE TABLE " + MOVIES_TABLE_NAME + "("
                + MOVIES_ID + " INTEGER PRIMARY KEY," + MOVIES_TITLE + " TEXT,"
                + MOVIES_BODY + " STRING," + MOVIES_URL + " STRING" + ")";
		try {
			db.execSQL(CREATE_MOVIES_TABLE);
		} catch (SQLiteException ex) {
			Log.e(LOG_TAG, "Couldn't create database: " + ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + MOVIES_TABLE_NAME);
		onCreate(db);
	}

}