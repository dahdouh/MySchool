package com.example.ecoleenligne.model;

public class Historic {

	private int id;
	private User actor;
	private String description;
	private String date;

	public Historic(int id, User actor, String description, String date) {
		super();
		this.id = id;
		this.actor = actor;
		this.description = description;
		this.date = date;
	}

	public Historic(int id, String description, String date) {
		super();
		this.id = id;
		this.description = description;
		this.date = date;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getActor() {
		return actor;
	}
	public void setActor(User actor) {
		this.actor = actor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}

