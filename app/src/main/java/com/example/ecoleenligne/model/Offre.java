package com.example.ecoleenligne.model;

public class Offre {

    protected String id;
    protected String nomHotel;
    protected String typeChambre;
    protected String dateDisponibilite;
    protected double prix;

    public Offre(String nomHotel) {
        this.nomHotel = nomHotel;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNomHotel() {
        return nomHotel;
    }
    public void setNomHotel(String nomHotel) {
        this.nomHotel = nomHotel;
    }
    public String getTypeChambre() {
        return typeChambre;
    }
    public void setTypeChambre(String typeChambre) {
        this.typeChambre = typeChambre;
    }
    public String getDateDisponibilite() {
        return dateDisponibilite;
    }
    public void setDateDisponibilite(String dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }
    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

}
