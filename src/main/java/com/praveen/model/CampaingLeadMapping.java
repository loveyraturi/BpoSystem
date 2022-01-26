package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CampaingLeadMapping")
public class CampaingLeadMapping {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String campaingName;
	String leadVersionName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCampaingName() {
		return campaingName;
	}
	public void setCampaingName(String campaingName) {
		this.campaingName = campaingName;
	}
	public String getLeadVersionName() {
		return leadVersionName;
	}
	public void setLeadVersionName(String leadVersionName) {
		this.leadVersionName = leadVersionName;
	}
	
	
}
