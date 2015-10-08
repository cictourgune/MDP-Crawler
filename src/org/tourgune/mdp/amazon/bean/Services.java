package org.tourgune.mdp.amazon.bean;

import java.util.Date;

public class Services {
	
	private int idAccommodation;
	private Date scrapingDate;
	private boolean freeWifi;
	private boolean freeParking;
	private boolean petsAllowed;
	private String activities;
	
	public int getIdAccommodation() {
		return idAccommodation;
	}
	public void setIdAccommodation(int idAccommodation) {
		this.idAccommodation = idAccommodation;
	}
	public Date getScrapingDate() {
		return scrapingDate;
	}
	public void setScrapingDate(Date scrapingDate) {
		this.scrapingDate = scrapingDate;
	}
	public boolean isFreeWifi() {
		return freeWifi;
	}
	public void setFreeWifi(boolean freeWifi) {
		this.freeWifi = freeWifi;
	}
	public boolean isFreeParking() {
		return freeParking;
	}
	public void setFreeParking(boolean freeParking) {
		this.freeParking = freeParking;
	}
	public boolean isPetsAllowed() {
		return petsAllowed;
	}
	public void setPetsAllowed(boolean petsAllowed) {
		this.petsAllowed = petsAllowed;
	}
	public String getActivities() {
		return activities;
	}
	public void setActivities(String activities) {
		this.activities = activities;
	}
	
	
	
}
