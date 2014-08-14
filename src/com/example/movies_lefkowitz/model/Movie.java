package com.example.movies_lefkowitz.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.example.movies_lefkowitz.R;

public class Movie {
	
	/* Constants */
	public static int UNWATCHED = 0;
	public static int WATCHED = 1;
	
	/* All Ratings */
	private List <String> all_ratings = new ArrayList<String>(); 
	private List <String> all_genres = new ArrayList<String>();
	
	/* Movie fields */
	private String pic = "";  
	private String title = "";
	private String genre = "";
	private String mpaa_rating = "";
	private String description = "";
	private String cast = ""; 
	private String director = "";
	
	private int rottenID = 0;
	private int watched = UNWATCHED; 
	private int year = 0;
	private int runtime = 0;
	
	private double rt_rating = 0;
	private double user_rating = 0;
	
	/* Constructor */
	public Movie(Context context, String title) {
		super();
		this.title = title;
		this.all_ratings = Arrays.asList(
				context.getResources().getStringArray(R.array.ratings));
		this.all_genres = Arrays.asList(
				context.getResources().getStringArray(R.array.genres));
	}
	
	/* Getters and Setters */
	public int getRottenID() {
		return rottenID;
	}

	public void setRottenID(int rottenID) {
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
		if (this.all_genres.size() > 0 && // we were able to get array of genres
			this.all_genres.contains(genre)) { // inputed genre is valid
				this.genre = genre;
		}
	}

	public String getMpaa_rating() {
		return mpaa_rating;
	}

	public void setMpaa_rating(String mpaa_rating) {
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
}