package com.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import com.dao.DataAccess;

public class LeftOverBookingsCloseScheduler extends TimerTask {
	public void run() {
		Calendar calendar = GregorianCalendar.getInstance();
		int currentHourOfTheDay = calendar.get(Calendar.HOUR_OF_DAY);
		if (currentHourOfTheDay >= 21 || currentHourOfTheDay <= 10) {
			DataAccess.getInstance().clearAllBookings();
		}
	}
}