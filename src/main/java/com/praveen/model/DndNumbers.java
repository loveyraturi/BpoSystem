package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dnd_numbers")
public class DndNumbers {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String phoneNumber;
	String serviceAreaCode;
	String preferences;
	String opstype;
	String phoneType;

	
	public String getServiceAreaCode() {
		return serviceAreaCode;
	}
	public void setServiceAreaCode(String serviceAreaCode) {
		this.serviceAreaCode = serviceAreaCode;
	}
	public String getPreferences() {
		return preferences;
	}
	public void setPreferences(String preferences) {
		this.preferences = preferences;
	}
	public String getOpstype() {
		return opstype;
	}
	public void setOpstype(String opstype) {
		this.opstype = opstype;
	}
	public String getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
