package com.vo;

import java.util.Date;

public class User implements Comparable<User>{
	private String userName;
	private String email;
	private String teamName;
	private boolean highlight;
	private Date dateNTime;
	private String bookingId;
	private int estimatedUsage;
	
	public User() {}
	
	public User(final String userName, final String email, final String teamName, final Date dateNTime) {
		super();
		this.userName = userName;
		this.email = email;
		this.teamName = teamName;
		this.dateNTime = dateNTime;
	}
	
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

	@Override
	public int compareTo(User obj) {
		if (obj != null && obj.getDateNTime().after(this.getDateNTime())) {
			return 1;
		}
		return -1;
	}
}
