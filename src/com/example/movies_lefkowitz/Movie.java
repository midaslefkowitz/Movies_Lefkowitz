package com.example.movies_lefkowitz;

public class Movie {
	private int id;
	private String title;
	private String body;
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
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	// Constructors
	public Movie(int id, String title, String body, String url) {
		setId(id);
		setTitle(title);
		setBody(body);
		setUrl(url);		
	}
}