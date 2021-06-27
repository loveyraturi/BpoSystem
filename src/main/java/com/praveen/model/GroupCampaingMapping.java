package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GroupCampaingMapping")
public class GroupCampaingMapping {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String campaingname;
	String groupname;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCampaingname() {
		return campaingname;
	}
	public void setCampaingname(String campaingname) {
		this.campaingname = campaingname;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	
	
}
