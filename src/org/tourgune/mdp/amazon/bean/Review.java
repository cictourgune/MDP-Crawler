package org.tourgune.mdp.amazon.bean;

import java.util.Date;

public class Review {
	
	private int idAccommodation;
	private Date scrapingDate;
	private int idSegment;
	private String typeTrip;
	private String segment;
	private String typeRoom;
	private int stayNights;
	private String withPet;
//	private String subSegment;
	private String from;
	private Date date;
	private String lang;
	private String reviewGood;
	private String reviewBad;
	private float score;
	
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
	public String getTypeTrip() {
		return typeTrip;
	}
	public void setTypeTrip(String typeTrip) {
		this.typeTrip = typeTrip;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getTypeRoom() {
		return typeRoom;
	}
	public void setTypeRoom(String typeRoom) {
		this.typeRoom = typeRoom;
	}
	public int getStayNights() {
		return stayNights;
	}
	public void setStayNights(int stayNights) {
		this.stayNights = stayNights;
	}
	public String getWithPet() {
		return withPet;
	}
	public void setWithPet(String withPet) {
		this.withPet = withPet;
	}
//	public String getSubSegment() {
//		return subSegment;
//	}
//	public void setSubSegment(String subSegment) {
//		this.subSegment = subSegment;
//	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getReviewGood() {
		return reviewGood;
	}
	public void setReviewGood(String reviewGood) {
		this.reviewGood = reviewGood;
	}
	public String getReviewBad() {
		return reviewBad;
	}
	public void setReviewBad(String reviewBad) {
		this.reviewBad = reviewBad;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
}
