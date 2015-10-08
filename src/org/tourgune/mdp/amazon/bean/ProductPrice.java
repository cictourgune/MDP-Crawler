package org.tourgune.mdp.amazon.bean;

import java.util.Date;

public class ProductPrice {

	private int idProduct;
	private int idAccommodation;
	private int idChannel;
	private Date idBookingDate;
	private Date idCheckinDate;
	private int lagDays;
	private int lengthOfStay;
	private float price;
	private int priceFlag;
	
	public int getPriceFlag() {
		return priceFlag;
	}
	public void setPriceFlag(int priceFlag) {
		this.priceFlag = priceFlag;
	}
	public int getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(int idProduct) {
		this.idProduct = idProduct;
	}
	public int getIdAccommodation() {
		return idAccommodation;
	}
	public void setIdAccommodation(int idAccommodation) {
		this.idAccommodation = idAccommodation;
	}
	public int getIdChannel() {
		return idChannel;
	}
	public void setIdChannel(int idChannel) {
		this.idChannel = idChannel;
	}
	public Date getIdBookingDate() {
		return idBookingDate;
	}
	public void setIdBookingDate(Date idBookingDate) {
		this.idBookingDate = idBookingDate;
	}
	public Date getIdCheckinDate() {
		return idCheckinDate;
	}
	public void setIdCheckinDate(Date idCheckinDate) {
		this.idCheckinDate = idCheckinDate;
	}
	public int getLagDays() {
		return lagDays;
	}
	public void setLagDays(int lagDays) {
		this.lagDays = lagDays;
	}
	public int getLengthOfStay() {
		return lengthOfStay;
	}
	public void setLengthOfStay(int lengthOfStay) {
		this.lengthOfStay = lengthOfStay;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
}
