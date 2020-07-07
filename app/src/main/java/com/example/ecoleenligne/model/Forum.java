package com.example.ecoleenligne.model;

public class Forum {
	
	 private int id;
     private Level level;
     private Topic topics;
     
    public Forum(){
    	 
    }
     
	public Forum(int id, Level level, Topic topics) {
		super();
		this.id = id;
		this.level = level;
		this.topics = topics;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Topic getTopics() {
		return topics;
	}

	public void setTopics(Topic topics) {
		this.topics = topics;
	}
	
	
     
     

}
