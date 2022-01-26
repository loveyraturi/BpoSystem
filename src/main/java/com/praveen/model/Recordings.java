package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recordings")
public class Recordings {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String username;
	String recordingName;
	String campaing;
	int leadId;
	
	public String getCampaing() {
		return campaing;
	}
	public void setCampaing(String campaing) {
		this.campaing = campaing;
	}
	public int getLeadId() {
		return leadId;
	}
	public void setLeadId(int leadId) {
		this.leadId = leadId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRecordingName() {
		return recordingName;
	}
	public void setRecordingName(String recordingName) {
		this.recordingName = recordingName;
	}
	
}
