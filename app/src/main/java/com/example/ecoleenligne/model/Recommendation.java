package com.example.ecoleenligne.model;

import java.util.Date;

public class Recommendation {

	public int id;
	public String date;
	public String contenu;

	public Recommendation() {
	}
	public Recommendation(int id, String contenu, String date) {
		this.id = id;
		this.contenu = contenu;
		this.date = date;
	}

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContenu() {
		return contenu;
	}
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	
	
	
}
