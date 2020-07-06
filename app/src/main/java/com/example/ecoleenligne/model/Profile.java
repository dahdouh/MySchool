package com.example.ecoleenligne.model;


public class Profile {

	public int id;
	public String libelle;
	public String role;

	public Profile(){}
	public Profile(int id, String libelle, String role){
		this.id = id;
		this.libelle = libelle;
		this.role = role;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
	

}
