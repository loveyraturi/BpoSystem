package com.praveen.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
public class UserStatusDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String status;
	String lastLoggedInDateTime;
	String activityDone;
	Date dateCreated;
	Date DateModified;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastLoggedInDateTime() {
		return lastLoggedInDateTime;
	}

	public void setLastLoggedInDateTime(String lastLoggedInDateTime) {
		this.lastLoggedInDateTime = lastLoggedInDateTime;
	}

	public String getActivityDone() {
		return activityDone;
	}

	public void setActivityDone(String activityDone) {
		this.activityDone = activityDone;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return DateModified;
	}

	public void setDateModified(Date dateModified) {
		DateModified = dateModified;
	}

	@PrePersist
	void onCreate() {
		this.setDateCreated(new Timestamp((new Date()).getTime()));
	}

	@PreUpdate
	void onPersist() {
		this.setDateModified(new Timestamp((new Date()).getTime()));
	}
}
