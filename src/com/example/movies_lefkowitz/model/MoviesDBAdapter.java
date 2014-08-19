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
	public static final String KEY_MOVIE_ROTTENID = "rotten_id";
	public static final String KEY_MOVIE_PIC = "pic";
	public static final String KEY_MOVIE_WATCHED = "watched";
	public static final String KEY_MOVIE_TITLE = "movie_title";
	public static final String KEY_MOVIE_YEAR = "year";
	public static final String KEY_MOVIE_GENRE = "genre";
	public static final String KEY_MOVIE_MPAA = "mpaa_rating";
	public static final String KEY_MOVIE_RUNTIME = "runtime";
	public static final String KEY_MOVIE_RT_RATING = "rt_rating";
	public static final String KEY_MOVIE_USER_RATING = "user_rating";
	public static final String KEY_MOVIE_DESCRIPTION = "description";
	public static final String KEY_MOVIE_CAST = "cast";
	public static final String KEY_MOVIE_DIRECTOR = "director";
	
	
	public static final String[] ALL_KEYS = new String[] {
		KEY_ROWID, KEY_MOVIE_ROTTENID, KEY_MOVIE_PIC, KEY_MOVIE_WATCHED, 
		KEY_MOVIE_TITLE, KEY_MOVIE_YEAR, KEY_MOVIE_GENRE, KEY_MOVIE_MPAA, 
		KEY_MOVIE_RUNTIME, KEY_MOVIE_RT_RATING, KEY_MOVIE_USER_RATING,
		KEY_MOVIE_DESCRIPTION, KEY_MOVIE_CAST, KEY_MOVIE_DIRECTOR};
		
	// Column numbers
	public static final int COL_ROWID = 0;
	public static final int COL_MOVIE_ROTTENID = 1;
	public static final int COL_MOVIE_PIC = 2;
	public static final int COL_MOVIE_WATCHED = 3;
	public static final int COL_MOVIE_TITLE = 4;
	public static final int COL_MOVIE_YEAR = 5;
	public static final int COL_MOVIE_GENRE = 6;
	public static final int COL_MOVIE_MPAA = 7;
	public static final int COL_MOVIE_RUNTIME = 8;
	public static final int COL_MOVIE_RT_RATING = 9;
	public static final int COL_MOVIE_USER_RATING = 10;
	public static final int COL_MOVIE_DESCRIPTION = 11;
	public static final int COL_MOVIE_CAST = 12;
	public static final int COL_MOVIE_DIRECTOR = 13;
	
	// DB info
	public static final String DATABASE_NAME = "MyMovies";
	public static final String DATABASE_TABLE = "movies";
	public static final int DATABASE_VERSION = 1;	
	
	private static final String DATABASE_CREATE_SQL = 
			"create table " + DATABASE_TABLE 
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_MOVIE_ROTTENID + " integer, "
			+ KEY_MOVIE_PIC + " string, "
			+ KEY_MOVIE_WATCHED + " integer, "
			+ KEY_MOVIE_TITLE + " text, "
			+ KEY_MOVIE_YEAR + " integer, "
			+ KEY_MOVIE_GENRE + " text, "
			+ KEY_MOVIE_MPAA + " text, "
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
	public void addMovie(Movie movie) {
		/*
		 * double[] {tomato_rating, user_rating}
		 */
		openWriteable();
		ContentValues newMovieValues = new ContentValues();
		newMovieValues.put(KEY_MOVIE_ROTTENID, movie.getRottenID());
		newMovieValues.put(KEY_MOVIE_PIC, movie.getPic());
		newMovieValues.put(KEY_MOVIE_TITLE, movie.getTitle());
		newMovieValues.put(KEY_MOVIE_GENRE, movie.getGenre());
		newMovieValues.put(KEY_MOVIE_MPAA, movie.getMpaa_rating());
		newMovieValues.put(KEY_MOVIE_DESCRIPTION, movie.getDescription());
		newMovieValues.put(KEY_MOVIE_CAST, movie.getCast());
		newMovieValues.put(KEY_MOVIE_DIRECTOR, movie.getDirector());
		newMovieValues.put(KEY_MOVIE_WATCHED, movie.getWatched());
		newMovieValues.put(KEY_MOVIE_YEAR, movie.getYear());
		newMovieValues.put(KEY_MOVIE_RUNTIME, movie.getRuntime());
		newMovieValues.put(KEY_MOVIE_RT_RATING, movie.getRt_rating());
		newMovieValues.put(KEY_MOVIE_USER_RATING, movie.getUser_rating());
		
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
	public boolean updateMovie(int rowId, Movie movie) {
		int numChanged;
		openWriteable();
		String where = KEY_ROWID + "=" + rowId;
		ContentValues newMovieValues = new ContentValues();
		newMovieValues.put(KEY_MOVIE_ROTTENID, movie.getRottenID());
		newMovieValues.put(KEY_MOVIE_PIC, movie.getPic());
		newMovieValues.put(KEY_MOVIE_TITLE, movie.getTitle());
		newMovieValues.put(KEY_MOVIE_GENRE, movie.getGenre());
		newMovieValues.put(KEY_MOVIE_MPAA, movie.getMpaa_rating());
		newMovieValues.put(KEY_MOVIE_DESCRIPTION, movie.getDescription());
		newMovieValues.put(KEY_MOVIE_CAST, movie.getCast());
		newMovieValues.put(KEY_MOVIE_DIRECTOR, movie.getDirector());
		newMovieValues.put(KEY_MOVIE_WATCHED, movie.getWatched());
		newMovieValues.put(KEY_MOVIE_YEAR, movie.getYear());
		newMovieValues.put(KEY_MOVIE_RUNTIME, movie.getRuntime());
		newMovieValues.put(KEY_MOVIE_RT_RATING, movie.getRt_rating());
		newMovieValues.put(KEY_MOVIE_USER_RATING, movie.getUser_rating());
	
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