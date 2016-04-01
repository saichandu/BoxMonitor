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

import com.dao.DataAccess;
import com.exception.ApplicationException;
import com.exception.MessagesEnum;
import com.services.EmailServiceImpl;
import com.util.PropertiesUtil;
import com.vo.User;

public class CloseBooking extends HttpServlet {

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
			final User booking  = DataAccess.getInstance().getBooking(bookingId);
			result = DataAccess.getInstance().closeBooking(bookingId);
			if (result) {
				out.println("Success.");
			} else {
				out.println("Failed.");
				return;
			}
			
			int index = 0;
			User nextUserInQueue = null; 
			for (User user : usersInQueue) {
				if (StringUtils.equalsIgnoreCase(booking.getEmail(), user.getEmail())) {
					break;
				}
				index++;
			}
			if (index == 0 && usersInQueue.size() > 1) {
				nextUserInQueue = usersInQueue.get(1);
			}
			if (nextUserInQueue != null) {
				final String subject = MessagesEnum.SLOT_AVAILABLE_EMAIL_SUBJECT_TEMPLATE.getMessage(boxName);
				final String body = MessagesEnum.SLOT_AVAILABLE_EMAIL_BODY_TEMPLATE
									.getMessage(
											boxName,
											booking.getEmail(),
											request.getServerName(),
											"" + request.getServerPort() + "",
											request.getContextPath().substring(1,
													request.getContextPath().length()),
											boxName, nextUserInQueue.getBookingId(), "saavvaru@deloitte.com");
				
				final Properties props = PropertiesUtil.getInstance().getDmailProps();
				try {
					EmailServiceImpl.getInstance().sendMail(props, nextUserInQueue.getEmail(), subject, body);
				} catch (Exception e) {
					throw new ApplicationException(MessagesEnum.EMAIL_SENDING_FAILED.getMessage(usersInQueue.get(0).getEmail()));
				}
			}
		} catch (ApplicationException e){
			out.println("Failed. " + e.getMessage());
		}
	}
}