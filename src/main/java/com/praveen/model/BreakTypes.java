package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "breakTypes")
public class BreakTypes {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String breakType;
	String campaingName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBreakType() {
		return breakType;
	}
	public void setBreakType(String breakType) {
		this.breakType = breakType;
	}
	public String getCampaingName() {
		return campaingName;
	}
	public void setCampaingName(String campaingName) {
		this.campaingName = campaingName;
	}
	
}
