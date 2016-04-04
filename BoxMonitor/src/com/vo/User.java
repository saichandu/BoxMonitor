package com.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Comparable<User>, Serializable{

	private static final long serialVersionUID = 5594639977096859751L;

	final static int MILLI_TO_MINS = 1000 * 60;
	
	private String userName;
	private String email;
	private String teamName;
	private boolean highlight;
	private Date bookedDateNTime;
	private Date dateNTime;
	private String time;
	private String bookingId;
	private int estimatedUsage;
	private int remainingTime;
	
	public User() {}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}
	public String getTeamName() {
		return teamName;
	}

	public boolean getHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public Date getBookedDateNTime() {
		return bookedDateNTime;
	}
	
	public void setBookedDateNTime(Date bookedDateNTime) {
		this.bookedDateNTime = bookedDateNTime;
	}

	public Date getDateNTime() {
		return dateNTime;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public int getEstimatedUsage() {
		return estimatedUsage;
	}

	public void setEstimatedUsage(int estimatedUsage) {
		this.estimatedUsage = estimatedUsage;
	}

	public int getRemainingTime() {
		this.remainingTime = estimatedUsage - (int)((new Date().getTime() - this.getDateNTime().getTime())/MILLI_TO_MINS);
		this.remainingTime = this.remainingTime < 0 ? 0 : remainingTime;
		return remainingTime;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY hh:mm a");
		this.time = sdf.format(dateNTime);
		return time;
	}

	@Override
	public int compareTo(User obj) {
		if (obj != null && obj.getBookedDateNTime().before(this.getBookedDateNTime())) {
			return 1;
		}
		return -1;
	}
}
