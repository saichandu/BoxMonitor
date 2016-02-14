package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
			result = DataAccess.getInstance().closeBooking(bookingId);
			if (result) {
				out.println("Success.");
			} else {
				out.println("Failed.");
			}
			
			final User nextUserInQueue = DataAccess.getInstance().getAllBookings(boxName);
			
			if (nextUserInQueue != null && StringUtils.isNotBlank(nextUserInQueue.getEmail())) {
				final String subject = MessagesEnum.SLOT_AVAILABLE_EMAIL_SUBJECT_TEMPLATE.getMessage(boxName);
				final String body = MessagesEnum.SLOT_AVAILABLE_EMAIL_BODY_TEMPLATE
									.getMessage(
											boxName,
											request.getServerName(),
											"" + request.getServerPort() + "",
											request.getContextPath().substring(1,
													request.getContextPath().length()),
											boxName, nextUserInQueue.getBookingId(), "saavvaru@deloitte.com");
				
				final Properties props = PropertiesUtil.getInstance().getEmailProps();
				try {
					//EmailServiceImpl.getInstance().sendMail(props, email, subject, body);
					EmailServiceImpl.getInstance().sendGMail(props, nextUserInQueue.getEmail(), subject, body);
				} catch (Exception e) {
					throw new ApplicationException(MessagesEnum.EMAIL_SENDING_FAILED.getMessage(nextUserInQueue.getEmail()));
				}
			}
		} catch (ApplicationException e){
			out.println("Failed. " + e.getMessage());
		}
	}
}