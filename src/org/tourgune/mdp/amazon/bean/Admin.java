package org.tourgune.mdp.amazon.bean;

import java.sql.Timestamp;

public class Admin {

	private Integer id = null;
	private String instance = null;
	private Timestamp startTime = null;
	private Timestamp endTime = null;
	private String totalTime = null;
	private Double dataSize = null;
	private String country = null;
	private String accType = null;
	private Integer accCount = null;
	private Integer rowsTotal = null;
	private Integer rowsOk = null;
	private Integer rowsKo = null;
	private Integer rowsHDE_NF = null;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public Double getDataSize() {
		return dataSize;
	}
	public void setDataSize(Double dataSize) {
		this.dataSize = dataSize;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public Integer getAccCount() {
		return accCount;
	}
	public void setAccCount(Integer accCount) {
		this.accCount = accCount;
	}
	public Integer getRowsTotal() {
		return rowsTotal;
	}
	public void setRowsTotal(Integer rowsTotal) {
		this.rowsTotal = rowsTotal;
	}
	public Integer getRowsOk() {
		return rowsOk;
	}
	public void setRowsOk(Integer rowsOk) {
		this.rowsOk = rowsOk;
	}
	public Integer getRowsKo() {
		return rowsKo;
	}
	public void setRowsKo(Integer rowsKo) {
		this.rowsKo = rowsKo;
	}
	public Integer getRowsHDE_NF() {
		return rowsHDE_NF;
	}
	public void setRowsHDE_NF(Integer rowsHDE_NF) {
		this.rowsHDE_NF = rowsHDE_NF;
	}
}
