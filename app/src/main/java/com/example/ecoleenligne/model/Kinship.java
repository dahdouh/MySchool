package com.example.ecoleenligne.model;

public class Kinship {
	
	private int id;
    private User tutor;
    private User student;
    
	public Kinship(int id, User tutor, User student) {
		super();
		this.id = id;
		this.tutor = tutor;
		this.student = student;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getTutor() {
		return tutor;
	}

	public void setTutor(User tutor) {
		this.tutor = tutor;
	}

	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}
    
    

}
