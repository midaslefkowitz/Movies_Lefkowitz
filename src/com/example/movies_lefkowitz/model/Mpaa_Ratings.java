package com.example.movies_lefkowitz.model;

public enum Mpaa_Ratings {
	UNRATED("Unrated"), 
	G("G"), 
	PG("PG"), 
	PG13("PG-13"), 
	R("R"), 
	NC17("NC-17");
	
	private String rating;
	
	private Mpaa_Ratings(String rating) {
        this.rating = rating;
	}
	
	public String getRating() {
		return rating;
	}
}