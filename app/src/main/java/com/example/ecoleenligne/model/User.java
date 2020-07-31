package com.example.ecoleenligne.model;

import java.util.ArrayList;
import java.util.List;

public class User {

	public int id;
	public String firstname;
	public String lastname;
	public String image;
	public String email;
	public String tel;
	public String ville;
	public String level;
	public String subscriptionType;
	public String sex;
	public String parentRelation;
	public Compte compte;
	public int compte_id;
	public int parent_id;
	public List<Course> courses = new ArrayList<>();
	public List<Subscription> subscriptions = new ArrayList<Subscription>();

	//public List<Recommendation> recommendations;

	public User() { }

	public User(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public User(int id, String firstname, String lastname) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public User(int id, String firstname, String lastname, String email) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public User(int id, String firstname, String lastname, String email, String level, String image, String subscriptionType) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.level = level;
		this.image = image;
		this.subscriptionType = subscriptionType;
	}


	public User(String firstname, String lastname, String email, String tel, String ville) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.tel = tel;
		this.ville = ville;
	}
	public User(int id, String email, String firstname, String lastname, String parentRelation, String level, String tel, String ville, String sex, int compte_id, int parent_id) {
		this.id = id;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.parentRelation = parentRelation;
		this.level = level;
		this.tel = tel;
		this.ville = ville;
		this.sex = sex;
		this.compte_id = compte_id;
		this.parent_id = parent_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) { this.image = image; }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getLevel() {
		return this.level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getSubscriptionType() { return this.subscriptionType; }

	public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }

	public String getParentRelation() {
		return this.parentRelation;
	}

	public void setParentRelation(String parentRelation) {
		this.parentRelation = parentRelation;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	public int getCompte_id() {
		return this.compte_id;
	}

	public void setCompte_id(int compte_id) {
		this.compte_id = compte_id;
	}

	public int getParent_id() {
		return this.parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}


	public Compte getCompte() {
		return compte;
	}

	public void setCompte(Compte compte) {
		this.compte = compte;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) { this.courses = courses; }

	public List<Subscription> getsubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	/*
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}
	 */
	
	
	
}
