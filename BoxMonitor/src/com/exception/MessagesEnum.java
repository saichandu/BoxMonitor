package com.exception;

import java.text.MessageFormat;

public enum MessagesEnum {
	ENTER_VALID_EMAIL("Invalid email address. Please enter valid email."),
	ENTER_DELOITTE_EMAIL("Please enter your Deloitte email address."),
	DUPLICATE_USER("User with same Email ID \"{0}\" already exists."),
	ADD_USER_SUCCESS("User \"{0}\" has been added successfully."),
	ADD_USER_FAILED("Failed to add the user."),
	USER_DOES_NOT_EXISTS("User with \"{0}\" email does not exists. Please use registered email for booking"),
	RETRIVAL_FAILED("Failed to retrive the information."),
	BOOKING_SUCCESS("The Machine/Box \"{0}\" has been booked successfully."),
	BOOKING_FAILED("Failed to book the machine/box. Please try again later."),
	BOOKINGS_RETRIVAL_FAILED("Failed to retrive all the bookings. Please try again later."),
	BOOKINGS_ALREADY_EXISTS("There is a booking already existing for \"{0}\" box on email \"{1}\"."),
	CLOSE_BOOKING_FAILED("Failed to close the booking \"{0}\". Please contact administrator."),
	NAV_BOX_BOOKING("Click <a href=\"/boxmonitor/boxbooking.faces\">here</a> to go to Box Booking Screen."),
	EMAIL_SENDING_FAILED("Failed to send booking email to \"{0}\". Please contact administrator."),
	GENERIC_ERROR("We are temporarily unable to process your request. Please try again later."),
	APP_PROPERTIES_LOADING_FAILED("Failed to load application properties."),
	
	BOOKING_EMAIL_SUBJECT_TEMPLATE("AUTO GENERATED: Booking for Box/Machine {0}"),
	BOOKING_EMAIL_BODY_TEMPLATE("This is auto generated email for box/machine booking. DO NOT REPLY.<br/><br/>Once the usage with box/machine <b>{0}</b> "
			+ "is finished, please click <a href=\"http:\\\\{1}:{2}\\{3}\\closebooking?boxname={4}&bookingid={5}\">here</a> to close the booking.<br/><br/>"
			+ "Please note that closing the booking is very important.".toUpperCase() 
			+ "<br/><br/>For any queries, please contact <a href=\"{6}\">administrator</a>.<br/>"),
			
	SLOT_AVAILABLE_EMAIL_SUBJECT_TEMPLATE("AUTO GENERATED: Box/Machine {0} is available"),
	SLOT_AVAILABLE_EMAIL_BODY_TEMPLATE("This is auto generated email for box/machine availability notification. DO NOT REPLY.<br/><br/>"
					+ "The box/machine <b>{0}</b> is available for usage. It is your turn NOW.<br/>"
					+ "If are intended not to use at this time, please close the booking by clicking"
					+ " <a href=\"http:\\\\{1}:{2}\\{3}\\closebooking?boxname={4}&bookingid={5}\">here</a>."
					+ "<br/><br/>For any queries, please contact <a href=\"{6}\">administrator</a>.<br/>");
	
	private String messageDetail;
	
	MessagesEnum(String messageDetail) {
		this.messageDetail = messageDetail;
	}
	
	public String getMessage(Object...args) {
		if (args == null || (args != null && args.length == 0)) return messageDetail; 
		MessageFormat formatter = new MessageFormat(messageDetail);
		String output = formatter.format(args);
		return output;
	}
}