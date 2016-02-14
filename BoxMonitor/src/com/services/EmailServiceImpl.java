package com.services;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailServiceImpl implements EmailService {

	private EmailServiceImpl() {

	}

	private static volatile EmailService instance;

	public static EmailService getInstance() {
		if (instance == null) {
			synchronized (EmailServiceImpl.class) {
				if (instance == null) {
					instance = new EmailServiceImpl();
				}
			}
		}
		return instance;
	}

	@Override
	public void sendMail(final Properties props, final String receipents,
			final String subject, final String body) throws Exception {
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(props.getProperty("mail.smtp.user")));

		InternetAddress[] address = InternetAddress.parse(receipents);

		message.setRecipients(Message.RecipientType.TO, address);

		message.setSubject(subject);
		message.setSentDate(new Date());
		message.setText(body);
		Transport transport = session.getTransport();
		transport.connect();
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}
	
	@Override
	public void sendGMail(final Properties props, final String receipents,
			final String subject, final String body) throws Exception {
		Session session = Session.getDefaultInstance(props);
		//session.setDebug(true);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(props.getProperty("mail.smtp.user")));
		InternetAddress[] address = InternetAddress.parse(receipents);
		message.setRecipients(Message.RecipientType.TO, address);
		message.setContent(body, "text/html");
		message.setSubject(subject);
		message.setSentDate(new Date());
		//message.setText(body);
		//Transport.send(message);
		Transport tp = session.getTransport("smtp");
	    tp.connect(props.getProperty("mail.smtps.host"), props.getProperty("mail.smtp.user"), props.getProperty("mail.smtp.password"));
	    tp.sendMessage(message, message.getAllRecipients());
	    tp.close();
	}
}
