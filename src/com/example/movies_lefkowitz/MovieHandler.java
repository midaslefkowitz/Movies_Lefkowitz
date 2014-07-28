package com.example.movies_lefkowitz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class MovieHandler {
	private MoviesDB dbHelper;

	public MovieHandler(Context context) {
		dbHelper = new MoviesDB(context, MoviesDB.DATABASE_NAME,
				null, MoviesDB.DATABASE_VERSION);
	}

	public Cursor getAllMovies() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(MoviesDB.MOVIES_TABLE_NAME, null, null, null, null,
				null, MoviesDB.MOVIE_DESCRIPTION + " ASC;");
		return cursor;
	}

	public Movie getMovie(long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Movie movie = null;

		Cursor cursor = db.query(MoviesDB.MOVIES_TABLE_NAME, null, MoviesDB.MOVIE_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		// Check if the movie has been found.
		if (cursor.moveToFirst()) {
			movie = new Movie(cursor.getInt(0), // Get _id
					cursor.getString(1), // Get title
					cursor.getString(2), // Get description
					cursor.getString(3)); // Get url
		}
		return movie;
	}

	public void addMovie(Movie movie) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues newMovieValues = new ContentValues();
		newMovieValues.put(MoviesDB.MOVIE_TITLE, movie.getTitle());
		newMovieValues.put(MoviesDB.MOVIE_DESCRIPTION, movie.getDescription());
		newMovieValues.put(MoviesDB.MOVIE_URL, movie.getUrl());

		try {
			db.insertOrThrow(MoviesDB.MOVIES_TABLE_NAME, null, newMovieValues);
		} catch (SQLiteException ex) {
			Log.e(MoviesDB.LOG_TAG, ex.getMessage());
			throw ex;
		} finally {
			db.close();
		}
	}

	public void updateMovie(Movie movie) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			ContentValues newMovieValues = new ContentValues();
			newMovieValues.put(MoviesDB.MOVIE_TITLE, movie.getTitle());
			newMovieValues.put(MoviesDB.MOVIE_DESCRIPTION, movie.getDescription());
			newMovieValues.put(MoviesDB.MOVIE_URL, movie.getUrl());

			// Save the changes
			db.update(MoviesDB.MOVIES_TABLE_NAME, newMovieValues, MoviesDB.MOVIE_ID + "=?",
					new String[] { String.valueOf(movie.getId()) });
		} catch (SQLiteException ex) {
			Log.e(MoviesDB.LOG_TAG, ex.getMessage());
			throw ex;
		} finally {
			db.close();
		}
	}

	public void deleteMovie(Movie movie) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		try {
			db.delete(MoviesDB.MOVIES_TABLE_NAME, MoviesDB.MOVIE_ID + "=?",
					new String[] { String.valueOf(movie.getId()) });
		} catch (SQLiteException ex) {
			Log.e(MoviesDB.LOG_TAG, ex.getMessage());
			throw ex;
		} finally {
			db.close();
		}
	}

	public void deleteAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		try {
			db.execSQL("DELETE FROM " + MoviesDB.MOVIES_TABLE_NAME);
		} catch (SQLiteException ex) {
			Log.e(MoviesDB.LOG_TAG, ex.getMessage());
			throw ex;
		} finally {
			db.close();
		}
	}
}