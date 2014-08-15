package com.example.movies_lefkowitz.model;

public class MovieHolder {

	private Movie movie;

	public MovieHolder() {
	}
	
	public MovieHolder(Movie movie) {
		this.movie = movie;
	}
	
	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}	
}