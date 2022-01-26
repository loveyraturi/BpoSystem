package com.praveen.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "attendance")
public class Attendance {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
	String username;
	Date loggedInTime;
	Date loggedOutTime;
	String totalWorkHour;
	String deviceId;
	int hours;
	int minute;
	int seconds;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
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
	public Date getLoggedInTime() {
		return loggedInTime;
	}
	public void setLoggedInTime(Date loggedInTime) {
		this.loggedInTime = loggedInTime;
	}
	public Date getLoggedOutTime() {
		return loggedOutTime;
	}
	public void setLoggedOutTime(Date loggedOutTime) {
		this.loggedOutTime = loggedOutTime;
	}
	public String getTotalWorkHour() {
		return totalWorkHour;
	}
	public void setTotalWorkHour(String totalWorkHour) {
		this.totalWorkHour = totalWorkHour;
	}
	
}
