package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.constants.ApplicationConstants;
import com.constants.EnumConstants;
import com.dao.DataAccess;
import com.exception.ApplicationException;
import com.exception.MessagesEnum;
import com.services.EmailServiceImpl;
import com.util.PropertiesUtil;
import com.vo.User;

public class PassBooking extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final String bookingId = request.getParameter("bookingid");
		final String boxName = request.getParameter("boxname");
		boolean result = false;
		PrintWriter out = response.getWriter();
		try {
			final List<User> usersInQueue = DataAccess.getInstance().getAllBookings(boxName);
			//Collections.sort(usersInQueue);
			final User currentUserBooking  = DataAccess.getInstance().getBooking(bookingId);
			
			int index = 0;
			User nextUserInQueue = null; 
			for (User user : usersInQueue) {
				if (StringUtils.equalsIgnoreCase(currentUserBooking.getEmail(), user.getEmail())) {
					break;
				}
				index++;
			}
			
			if (index == usersInQueue.size()) {
				out.println("This booking is expired.");
				return;
			} else if ((index + 1) == usersInQueue.size()) {
				out.println("No other users in Queue to pass the booking. Please close the booking.");
				return;
			} else {
				nextUserInQueue = usersInQueue.get(index + 1);
				result = DataAccess.getInstance().passBooking(currentUserBooking, nextUserInQueue);
				if (result) {
					out.println(EnumConstants.SUCCESS.toString());
				} else {
					out.println(EnumConstants.FAILED.toString());
					return;
				}
			}
			
			if (result) {
				//Delete other bookings of the same user if any
				DataAccess.getInstance().removeOtherBookings(
						nextUserInQueue.getEmail(),
						nextUserInQueue.getBookingId());
				
				final String subject = MessagesEnum.SLOT_AVAILABLE_EMAIL_SUBJECT_TEMPLATE.getMessage(boxName);
				final String body = MessagesEnum.SLOT_AVAILABLE_EMAIL_BODY_TEMPLATE
									.getMessage(
											boxName,
											currentUserBooking.getEmail(),
											request.getServerName(),
											"" + request.getServerPort() + "",
											request.getContextPath().substring(1,
													request.getContextPath().length()),
											boxName, nextUserInQueue.getBookingId(), ApplicationConstants.ADMIN_EMAIL);
				
				final Properties props = PropertiesUtil.getInstance().getDmailProps();
				try {
					EmailServiceImpl.getInstance().sendMail(props, nextUserInQueue.getEmail(), subject, body);
				} catch (Exception e) {
					throw new ApplicationException(MessagesEnum.EMAIL_SENDING_FAILED.getMessage(usersInQueue.get(0).getEmail()));
				}
			}
		} catch (ApplicationException e){
			out.println(e.getMessage());
		}
	}
}