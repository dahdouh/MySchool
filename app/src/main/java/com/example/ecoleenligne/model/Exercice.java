package com.example.ecoleenligne.model;

import java.util.List;



public class Exercice {

	public int id;
	public String content;
	public String correction;
	public Course course;

	public Exercice(int id, String content, String correction) {
		this.id = id;
		this.content = content;
		this.correction = correction;
	}

	public Exercice(int id, String content, String correction, Course course) {
		this.id = id;
		this.content = content;
		this.correction = correction;
		this.course = course;
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

	public String getCorrection() {
		return correction;
	}
	public void setCorrection(String correction) {
		this.correction = correction;
	}

	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}


}
