package com.example.ecoleenligne.model;

public class Subject {

    private int id;

    private String name;

    private String icon;

    public Subject(int id, String name){
        this.id = id;
        this.name = name;
    }

    public Subject(int id, String name, String icon){
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
