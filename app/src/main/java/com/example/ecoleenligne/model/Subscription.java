package com.example.ecoleenligne.model;

public class Subscription {

	private int id;
    private String price;
	private Subject subject;
    private Level level;

	public Subscription(int id, Subject subject) {
		this.id = id;
		this.subject = subject;
	}

	public Subscription(int id, Subject subject, String price, Level level) {
		this.id = id;
		this.subject = subject;
		this.price = price;
		this.level = level;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSUbject(Subject subject) {
		this.subject = subject;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
    
    
}
