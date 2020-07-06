package com.example.ecoleenligne.model;

public class Tchat {

	public int id;
	public String message;
	public String dateSend;
	public User transmtter;
	public User receiver;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDateSend() {
		return dateSend;
	}
	public void setDateSend(String dateSend) {
		this.dateSend = dateSend;
	}
	public User getTransmtter() {
		return transmtter;
	}
	public void setTransmtter(User transmtter) {
		this.transmtter = transmtter;
	}
	public User getReceiver() {
		return receiver;
	}
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	
}
