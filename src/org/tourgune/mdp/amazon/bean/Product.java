package org.tourgune.mdp.amazon.bean;

public class Product {

	private String name;
	private String adultAmount;
	private String childrenAmount;
	private String breakfastPrice;
	private boolean breakfastIncluded;
	private boolean halfBoard;
	private boolean fullBoard;
	private boolean allInclusive;
	private boolean freeCancellation;
	private boolean payStay;
	private boolean payLater;
	private boolean nonRefundable;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdultAmount() {
		return adultAmount;
	}
	public void setAdultAmount(String adultAmount) {
		this.adultAmount = adultAmount;
	}
	public String getChildrenAmount() {
		return childrenAmount;
	}
	public void setChildrenAmount(String childrenAmount) {
		this.childrenAmount = childrenAmount;
	}
	public String getBreakfastPrice() {
		return breakfastPrice;
	}
	public void setBreakfastPrice(String breakfastPrice) {
		this.breakfastPrice = breakfastPrice;
	}
	public boolean isBreakfastIncluded() {
		return breakfastIncluded;
	}
	public void setBreakfastIncluded(boolean breakfastIncluded) {
		this.breakfastIncluded = breakfastIncluded;
	}
	public boolean isHalfBoard() {
		return halfBoard;
	}
	public void setHalfBoard(boolean halfBoard) {
		this.halfBoard = halfBoard;
	}
	public boolean isFullBoard() {
		return fullBoard;
	}
	public void setFullBoard(boolean fullBoard) {
		this.fullBoard = fullBoard;
	}
	public boolean isAllInclusive() {
		return allInclusive;
	}
	public void setAllInclusive(boolean allInclusive) {
		this.allInclusive = allInclusive;
	}
	public boolean isFreeCancellation() {
		return freeCancellation;
	}
	public void setFreeCancellation(boolean freeCancellation) {
		this.freeCancellation = freeCancellation;
	}
	public boolean isPayStay() {
		return payStay;
	}
	public void setPayStay(boolean payStay) {
		this.payStay = payStay;
	}
	public boolean isPayLater() {
		return payLater;
	}
	public void setPayLater(boolean payLater) {
		this.payLater = payLater;
	}
	public boolean isNonRefundable() {
		return nonRefundable;
	}
	public void setNonRefundable(boolean nonRefundable) {
		this.nonRefundable = nonRefundable;
	}
	
}
