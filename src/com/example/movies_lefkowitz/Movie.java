package com.example.movies_lefkowitz;

public class Movie {
	private int id;
	private String title;
	private String description;
	private String url;
	
	// getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	// Constructors
	public Movie(int id, String title, String body, String url) {
		setId(id);
		setTitle(title);
		setDescription(description);
		setUrl(url);		
	}
}