package org.tourgune.mdp.amazon.bean;

import java.util.Date;

public class Rating {

	private int idAccommodation;
	private Date scrapingDate;
	private int idSegment;
	private String segment;
	private float clean;
	private float comfort;
	private float location;
	private float services;
	private float staff;
	private float value;
	private float wifi;
	private float average;
	private int ttlUsers;
	
	public int getTtlUsers() {
		return ttlUsers;
	}
	public void setTtlUsers(int ttlUsers) {
		this.ttlUsers = ttlUsers;
	}
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
	public int getIdSegment() {
		return idSegment;
	}
	public void setIdSegment(int idSegment) {
		this.idSegment = idSegment;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public float getClean() {
		return clean;
	}
	public void setClean(float clean) {
		this.clean = clean;
	}
	public float getComfort() {
		return comfort;
	}
	public void setComfort(float comfort) {
		this.comfort = comfort;
	}
	public float getLocation() {
		return location;
	}
	public void setLocation(float location) {
		this.location = location;
	}
	public float getServices() {
		return services;
	}
	public void setServices(float services) {
		this.services = services;
	}
	public float getStaff() {
		return staff;
	}
	public void setStaff(float staff) {
		this.staff = staff;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getWifi() {
		return wifi;
	}
	public void setWifi(float wifi) {
		this.wifi = wifi;
	}
	public float getAverage() {
		return average;
	}
	public void setAverage(float average) {
		this.average = average;
	}
	
	
}
