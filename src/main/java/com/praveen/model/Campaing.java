package com.praveen.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "campaing")
public class Campaing {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String name;
	String active;
	String localCallTime;
	String dialTimeout;
	String assignmentType;
	String dialPrefix;
	String manualDialPrefix;
	String additionalField;
	Date dateCreated;
	Date DateModified;
	

	public String getAdditionalField() {
		return additionalField;
	}
	public void setAdditionalField(String additionalField) {
		this.additionalField = additionalField;
	}
	public String getAssignmentType() {
		return assignmentType;
	}
	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}
	public String getLocalCallTime() {
		return localCallTime;
	}
	public void setLocalCallTime(String localCallTime) {
		this.localCallTime = localCallTime;
	}
	public String getDialTimeout() {
		return dialTimeout;
	}
	public void setDialTimeout(String dialTimeout) {
		this.dialTimeout = dialTimeout;
	}
	public String getDialPrefix() {
		return dialPrefix;
	}
	public void setDialPrefix(String dialPrefix) {
		this.dialPrefix = dialPrefix;
	}
	public String getManualDialPrefix() {
		return manualDialPrefix;
	}
	public void setManualDialPrefix(String manualDialPrefix) {
		this.manualDialPrefix = manualDialPrefix;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
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
