package com.example.movies_lefkowitz.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MoviesDBAdapter {

	/* 
	 * Constants & Data
	 */

	// For logging:
	private static final String TAG = "MoviesDBAdapter";
	
	// Column names
	public static final String KEY_ROWID = "_id";
	public static final String KEY_MOVIE_PIC = "pic";
	public static final String KEY_MOVIE_WATCHED = "watched";
	public static final String KEY_MOVIE_TITLE = "movie_title";
	public static final String KEY_MOVIE_YEAR = "year";
	public static final String KEY_MOVIE_GENRE = "genre";
	public static final String KEY_MOVIE_RATING = "rating";
	public static final String KEY_MOVIE_RUNTIME = "runtime";
	public static final String KEY_MOVIE_RT_RATING = "rt_rating";
	public static final String KEY_MOVIE_USER_RATING = "user_rating";
	public static final String KEY_MOVIE_DESCRIPTION = "description";
	public static final String KEY_MOVIE_CAST = "cast";
	public static final String KEY_MOVIE_DIRECTOR = "director";
	
	
	public static final String[] ALL_KEYS = new String[] {
		KEY_ROWID, KEY_MOVIE_PIC, KEY_MOVIE_WATCHED, KEY_MOVIE_TITLE, 
		KEY_MOVIE_YEAR, KEY_MOVIE_GENRE, KEY_MOVIE_RATING, 
		KEY_MOVIE_RUNTIME, KEY_MOVIE_RT_RATING, KEY_MOVIE_USER_RATING,
		KEY_MOVIE_DESCRIPTION, KEY_MOVIE_CAST, KEY_MOVIE_DIRECTOR};
	
	public static final String[] STRING_KEYS = new String[] {
		KEY_MOVIE_PIC, KEY_MOVIE_TITLE, KEY_MOVIE_GENRE, 
		KEY_MOVIE_RATING, KEY_MOVIE_DESCRIPTION, KEY_MOVIE_CAST, 
		KEY_MOVIE_DIRECTOR
	};
	
	public static final String[] INTEGER_KEYS = new String[] {
		KEY_MOVIE_WATCHED, KEY_MOVIE_YEAR, KEY_MOVIE_RUNTIME
	};
	
	public static final String[] REAL_KEYS = new String[] {
		KEY_MOVIE_RT_RATING, KEY_MOVIE_USER_RATING
	};
	
	// Column numbers
	public static final int COL_ROWID = 0;
	public static final int COL_MOVIE_PIC = 1;
	public static final int COL_MOVIE_WATCHED = 2;
	public static final int COL_MOVIE_TITLE = 3;
	public static final int COL_MOVIE_YEAR = 4;
	public static final int COL_MOVIE_GENRE = 5;
	public static final int COL_MOVIE_RATING = 6;
	public static final int COL_MOVIE_RUNTIME = 7;
	public static final int COL_MOVIE_RT_RATING = 8;
	public static final int COL_MOVIE_USER_RATING = 9;
	public static final int COL_MOVIE_DESCRIPTION = 10;
	public static final int COL_MOVIE_CAST = 11;
	public static final int COL_MOVIE_DIRECTOR = 12;
	
	// DB info
	public static final String DATABASE_NAME = "MyMovies";
	public static final String DATABASE_TABLE = "movies";
	public static final int DATABASE_VERSION = 1;	
	
	private static final String DATABASE_CREATE_SQL = 
			"create table " + DATABASE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_MOVIE_PIC + " string, "
			+ KEY_MOVIE_WATCHED + " integer, "
			+ KEY_MOVIE_TITLE + " text, "
			+ KEY_MOVIE_YEAR + " integer, "
			+ KEY_MOVIE_GENRE + " text, "
			+ KEY_MOVIE_RATING + " text, "
			+ KEY_MOVIE_RUNTIME + " integer, "
			+ KEY_MOVIE_RT_RATING  + " real, "
			+ KEY_MOVIE_USER_RATING + " real, "
			+ KEY_MOVIE_DESCRIPTION + " string, "
			+ KEY_MOVIE_CAST + " string, "
			+ KEY_MOVIE_DIRECTOR + " string"
			+ ");";
	

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/*
	 * Public methods:
	 */
	
	public MoviesDBAdapter(Context context) {
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection write.
	private MoviesDBAdapter openWriteable() {
 		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Open the database connection readonly.
	private MoviesDBAdapter openReadable() {
		db = myDBHelper.getReadableDatabase();
		return this;
	}
	
	// Close the database connection.
	private void close() {
		myDBHelper.close();
	}
	
	// Add a new set of values to the database.
	public void addMovie(String [] strings, int[] ints, double[] doubles) {
		/*
		 * strings[] {pic_Url, title, genre, movie rating, description, cast, director}
		 * ints [] {watched, year, runtime}
		 * double[] {tomato_rating, user_rating}
		 */
		openWriteable();
		ContentValues newMovieValues = new ContentValues();
		for (int i=0; i<strings.length; i++){
			newMovieValues.put(STRING_KEYS[i], strings[i]);
		}
		for (int i=0; i<ints.length; i++) {
			if (ints[i]>=0) {
				newMovieValues.put(INTEGER_KEYS[i], ints[i]);
			} else {
				newMovieValues.putNull(INTEGER_KEYS[i]);
			}
		}
		for (int i=0; i<doubles.length; i++) {
			if (doubles[i]>=0) {
				newMovieValues.put(REAL_KEYS[i], doubles[i]);
			} else {
				newMovieValues.putNull(REAL_KEYS[i]);
			}
		}
		try {
			db.insertOrThrow(DATABASE_TABLE, null, newMovieValues);
		} catch (SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
			throw ex;
		} finally {
			close();
		}
	}
	
	// Delete a row from the database, by rowId (primary key)
	public boolean deleteMovie(long rowId) {
		int rowsDeleted;
		openWriteable();
		String where = KEY_ROWID + "=" + rowId;
		try {
			rowsDeleted = db.delete(DATABASE_TABLE, where, null);
		} catch (SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
			throw ex;
		} finally {
			close();
		}
		return rowsDeleted != 0;
	}
	
	public void deleteAll() {
		openWriteable();
		try {
			db.execSQL("DELETE FROM " + DATABASE_TABLE);
		} catch (SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
			throw ex;
		} finally {
			close();
		}
	}
	
	// Return all data in the database.
	public Cursor getAllMovies() {
		openReadable();
		String where = null;
		Cursor c = db.query(DATABASE_TABLE, null, null, null, null, null, KEY_MOVIE_TITLE + " ASC");
		if (c != null) {
			c.moveToFirst();
		}
		close();
		return c;	
	}

	// Get a specific Movie (by rowId)
	public Cursor getMovie(long rowId) {
		openReadable();
		//Movie movie = null;
		String where = KEY_ROWID + "=" + rowId;
		Cursor c =	db.query(true, DATABASE_TABLE, ALL_KEYS, 
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		close();
		return c;		
	}
	
	// Change an existing row to be equal to new data.
	public boolean updateMovie(int rowId, String [] strings, int[] ints, double[] doubles) {
		/**
		 * strings[] {pic_Url, title, genre, description, cast, director}
		 * ints [] {watched, year, runtime}
		 * double[] {tomato_rating, user_rating}
		 */
		int numChanged;
		openWriteable();
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newMovieValues = new ContentValues();
		for (int i=0; i<strings.length; i++){
			newMovieValues.put(STRING_KEYS[i], strings[i]);
		}
		for (int i=0; i<ints.length; i++) {
			newMovieValues.put(INTEGER_KEYS[i], ints[i]);
		}
		for (int i=0; i<doubles.length; i++) {
			newMovieValues.put(REAL_KEYS[i], doubles[i]);
		}
		try {
			numChanged = db.update(DATABASE_TABLE, newMovieValues, where, null);
		} catch (SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
			throw ex;
		} finally {
			close();
		}
		 return numChanged != 0;
	}
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override 
		public void onCreate(SQLiteDatabase _db) {
			try {
				_db.execSQL(DATABASE_CREATE_SQL);
			} catch (SQLiteException ex) {
				Log.e(TAG, "Couldn't create database: " + ex.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
			
			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			
			// Recreate new database:
			onCreate(_db);
		}
	}
}