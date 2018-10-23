package com.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.dao.DataAccess;
import com.exception.ApplicationException;
import com.exception.MessagesEnum;
import com.vo.Booking;
import com.vo.User;

public class TimeKeeper extends TimerTask {

	final static String USER_AGENT = "Mozilla/5.0";
	final static int MILLI_TO_MINS = 1000 * 60;
	final String ip, port, contextPath;

	public TimeKeeper(String ip, String port, String contextPath) {
		this.ip = ip;
		this.port = port;
		this.contextPath = contextPath;
	}

	@Override
	public void run() {
		String bookingId = "";
		try {
			final List<Booking> bookings = new ArrayList<Booking>(DataAccess
					.getInstance().getAllBookings().values());
			System.out.println("TimeKeeper " + bookings.size());
			for (Booking booking : bookings) {
				User currUserInQueue = booking.getCurrentUser();
				bookingId = currUserInQueue.getBookingId();
				int remainingTime = currUserInQueue.getEstimatedUsage()
						- (int) ((new Date().getTime() - currUserInQueue
								.getDateNTime().getTime()) / MILLI_TO_MINS);
				if (remainingTime <= 0) {
					try {
						URL url = new URL("http://" + ip + ":" + port
								+ contextPath + "/closebooking?boxname="
								+ booking.getBoxName() + "&bookingid="
								+ bookingId);
						HttpURLConnection con = (HttpURLConnection) url
								.openConnection();
						con.setRequestMethod("GET");
						con.setRequestProperty("User-Agent", USER_AGENT);
						int responseCode = con.getResponseCode();

						if (responseCode == HttpURLConnection.HTTP_OK) {
							BufferedReader in = new BufferedReader(
									new InputStreamReader(con.getInputStream()));
							String line;
							StringBuffer response = new StringBuffer();

							while ((line = in.readLine()) != null) {
								response.append(line);
							}
							in.close();
						} else {
							throw new ApplicationException(
									MessagesEnum.FAILED_TO_INVOKE_SERVICE
											.getMessage("Close Booking"));
						}
					} catch (IOException ex) {
						ex.printStackTrace();
						throw new ApplicationException(
								MessagesEnum.FAILED_TO_INVOKE_SERVICE
										.getMessage("Close Booking"),
								ex);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(
					MessagesEnum.CLOSE_BOOKING_FAILED.getMessage(bookingId), e);
		}
	}

}
