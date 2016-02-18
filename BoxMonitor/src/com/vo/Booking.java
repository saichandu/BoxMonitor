package com.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Serializable{
	
	private static final long serialVersionUID = 5949997812451286612L;
	
	private String boxName;
	private String boxOwner;
	private User currentUser;
	private List<User> usersInQueue;
	
	public String getBoxName() {
		return boxName;
	}
	public void setBoxName(String boxName) {
		this.boxName = boxName;
	}
	public String getBoxOwner() {
		return boxOwner;
	}
	public void setBoxOwner(String boxOwner) {
		this.boxOwner = boxOwner;
	}
	public User getCurrentUser() {
		if (this.usersInQueue != null) {
			currentUser = usersInQueue.get(0);
		}
		return currentUser;
	}
	public List<User> getUsersInQueue() {
		return usersInQueue;
	}
	public void addUserToQueue(User user) {
		if (usersInQueue == null) {
			usersInQueue = new ArrayList<User>();
		}
		this.usersInQueue.add(user);
	}
}
