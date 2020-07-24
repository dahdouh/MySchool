package com.example.ecoleenligne.model;


public class Course {

	public int id;
 	public String libelle;
 	public String progress;
	public String level;
	public String description;
	public String image;


	private String subject;
	private String name;
	private String content;




	public Course(){

	}
	public Course(int id){
		this.id = id;
	}


	public Course(int id, String name, String description){
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Course(int id, String name, String description, String image){
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
	}
	/*
	public Course(int id, String name, String description, String subject){
		this.id = id;
		this.name = name;
		this.description = description;
		this.subject = subject;
	}
	*/
	/*
	public Course(int id, String libelle, String description, String image){
		this.id = id;
		this.libelle = libelle;
		this.description = description;
		this.image = image;
	}
	*/

	public Course(String libelle, String description, String image, String level, String progress){
		this.libelle = libelle;
		this.description = description;
		this.image = image;
		this.level = level;
		this.progress = progress;

	}

	public Course(int id, String libelle, String description, String image, String level, String progress){
		this.id = id;
		this.libelle = libelle;
		this.description = description;
		this.image = image;
		this.level = level;
		this.progress = progress;

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {

		this.libelle = libelle;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.subject = name;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = name;
	}


	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
