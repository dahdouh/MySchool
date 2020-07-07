package com.example.ecoleenligne.model;

public class Post {
	
    private int id;

    private String content;

    private String date;

    private User author;

	public Post(int id, String content, String date) {
		super();
		this.id = id;
		this.content = content;
		this.date = date;
	}

	public Post(int id, String content, String date, User author) {
		super();
		this.id = id;
		this.content = content;
		this.date = date;
		this.author = author;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
    
}
