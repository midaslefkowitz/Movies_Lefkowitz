package com.example.movies_lefkowitz.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.example.movies_lefkowitz.R;

public class Movie implements Serializable {
	
	
	/* Constants */
	public static final int UNWATCHED = 0;
	public static final int WATCHED = 1;
	private static final long serialVersionUID = 1L;
	
	/* All Ratings */
	private List <String> all_ratings = new ArrayList<String>(); 
	//private List <String> all_genres = new ArrayList<String>();
	
	/* Movie fields */
	private String pic = "";  
	private String title = "";
	private String genre = "";
	private String mpaa_rating = "";
	private String description = "";
	private String cast = ""; 
	private String director = "";
	
	private long rottenID = 0;
	private long dbID = 0;
	
	private int watched = UNWATCHED; 
	private int year = 0;
	private int runtime = 0;
	
	private double rt_rating = 0;
	private double user_rating = 0;
	
	/* Constructors */
	public Movie(Context context, String title) {
		super();
		this.title = title;
		this.all_ratings = Arrays.asList(
				context.getResources().getStringArray(R.array.ratings));
	}
	
	public Movie (Context context, Cursor cursor) {
		this.dbID = cursor.getLong(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_ROWID));
		this.pic = cursor.getString(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_PIC));  
		this.title = cursor.getString(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_TITLE));
		this.genre = cursor.getString(cursor.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_GENRE));
		this.mpaa_rating = cursor.getString(cursor.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_MPAA));;
		this.description = cursor.getString(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_DESCRIPTION));
		this.cast = cursor.getString(cursor.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_CAST)); 
		this.director = cursor.getString(cursor.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_DIRECTOR));
		
		this.rottenID = cursor.getLong(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_ROTTENID));
		this.dbID = cursor.getLong(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_ROWID));
		
		this.watched = cursor.getInt(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_WATCHED));
		this.year = cursor.getInt(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_YEAR));
		this.runtime = cursor.getInt(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_RUNTIME));
		
		this.rt_rating = cursor.getDouble(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_RT_RATING));
		this.user_rating = cursor.getDouble(cursor
				.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_USER_RATING));
		
		this.all_ratings = Arrays.asList(
				context.getResources().getStringArray(R.array.ratings));

	}
	
	/* Getters and Setters */
	public long getDbID() {
		return dbID;
	}
	
	public long getRottenID() {
		return rottenID;
	}

	public void setRottenID(long rottenID) {
		this.rottenID = rottenID;
	}	
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getMpaa_rating() {
		return mpaa_rating;
	}

	public void setMpaa_rating(String mpaa_rating) {
		// sanity check, mpaa_rating is a valid rating
		if (this.all_ratings.size() > 0 && // we were able to get array of ratings
			this.all_ratings.contains(mpaa_rating)) { // inputed rating is valid
				this.mpaa_rating = mpaa_rating;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCast() {
		return cast;
	}

	public void setCast(String cast) {
		this.cast = cast;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public int getWatched() {
		return watched;
	}

	public void setWatched(int watched) {
		// sanity check. movie is watched or unwatched
		if (watched == UNWATCHED || watched == WATCHED){
			this.watched = watched;			
		}
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public double getRt_rating() {
		return rt_rating;
	}

	public void setRt_rating(double rt_rating) {
		this.rt_rating = rt_rating;
	}

	public double getUser_rating() {
		return user_rating;
	}

	public void setUser_rating(double user_rating) {
		this.user_rating = user_rating;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}