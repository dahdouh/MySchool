package com.example.ecoleenligne.model;


public class Compte {

	public int id;
	public String login;
	public String password;
	public Profile profile;
	public int profile_id;
	public String activate;

	public Compte() {
	}

	public Compte(String login, String password){
		this.login = login;
		this.password = password;
	}

	public Compte(int id, String login, String password, String activate, int profile_id){
		this.id = id;
		this.login = login;
		this.password = password;
		this.activate = activate;
		this.profile_id = profile_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getActivate() {
		return activate;
	}

	public void setActivate(String activate) {
		this.activate = activate;
	}

	public int getProfile_id() {
		return profile_id;
	}

	public void setProfile_id(int profile_id) {
		this.profile_id = profile_id;
	}

}
