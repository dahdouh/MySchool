package com.example.ecoleenligne.model;

import java.util.List;

public class Topic {
	
	 private int id;
     private String title;
     private String content;
     private String date;
     private Subject subject;
     private User author;
     private Forum forum;
	 private List<Post> posts;
	 
	public Topic(int id, String title, String date, Subject subject, User author) {
		super();
		this.id = id;
		this.title = title;
		this.date = date;
		this.subject = subject;
		this.author = author;
	}

	public Topic(int id, String title, String content, String date, Subject subject, User author){
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.date = date;
		this.subject = subject;
		this.author = author;
	}

	public Topic(int id, String title, String content, String date, Subject subject, User author, Forum forum,
				 List<Post> posts) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.date = date;
		this.subject = subject;
		this.author = author;
		this.forum = forum;
		this.posts = posts;
	}

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

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Forum getForum() {
		return forum;
	}

	public void setForum(Forum forum) {
		this.forum = forum;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

}
