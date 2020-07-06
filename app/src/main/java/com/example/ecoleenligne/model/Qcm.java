package com.example.ecoleenligne.model;


public class Qcm {

	public int id;
	public String question;
	public String listchoices;
	public String response;
	public Exercice exercice;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getListchoices() {
		return listchoices;
	}
	public void setListchoices(String listchoices) {
		this.listchoices = listchoices;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public Exercice getExercice() {
		return exercice;
	}
	public void setExercice(Exercice exercice) {
		this.exercice = exercice;
	}
	
	

}
