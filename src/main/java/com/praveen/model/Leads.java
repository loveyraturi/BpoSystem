package com.praveen.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "leads")
public class Leads implements Cloneable{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String name;
	String assignedTo;
	String phoneNumber;
	String firstName;
	String city;
	String state;
	String email;
	String crm;
	String status;
	int callCount;
	String filename;
	Date dateCreated;
	Date DateModified;
	String callBackDateTime;
	String 	last_local_call_time;
	String comments;
	Date callDate;
	Date CallEndDate;

//	@ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn (name="id",referencedColumnName = "id", insertable = false, updatable = false)
//	private Campaing campaing;
//	
//	public Campaing getCampaing() {
//		return campaing;
//	}
//
//	public void setCampaing(Campaing campaing) {
//		this.campaing = campaing;
//	}

	 public Leads clone() throws CloneNotSupportedException {
	        return (Leads) super.clone();
	    }

//		this.assignedTo;
//		this.phoneNumber;
//		this.firstName;
//		this.city;
//		this.state;
//		this.email;
//		this.crm;
//		this.status;
//		this.callCount;
//		this.filename;
//		this.dateCreated;
//		this.DateModified;
//		String callBackDateTime;
//		String 	last_local_call_time;
//		String comments;
//		Date callDate;
//		Date CallEndDate;
//	}
	public String getCallBackDateTime() {
		return callBackDateTime;
	}


	public void setCallBackDateTime(String callBackDateTime) {
		this.callBackDateTime = callBackDateTime;
	}


	public String getLast_local_call_time() {
		return last_local_call_time;
	}


	public void setLast_local_call_time(String last_local_call_time) {
		this.last_local_call_time = last_local_call_time;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public Date getCallDate() {
		return callDate;
	}


	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}


	public Date getCallEndDate() {
		return CallEndDate;
	}


	public void setCallEndDate(Date callEndDate) {
		CallEndDate = callEndDate;
	}


	public int getId() {
		return id;
	}

	
	public String getCrm() {
		return crm;
	}


	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getAssignedTo() {
		return assignedTo;
	}


	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
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
