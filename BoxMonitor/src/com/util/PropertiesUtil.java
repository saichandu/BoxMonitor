package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.exception.ApplicationException;
import com.exception.MessagesEnum;

public class PropertiesUtil {

	private static volatile PropertiesUtil instance;
	
	private Properties props;
	
	private Properties gmailprops, dmailprops;
	
	private PropertiesUtil() {
	}
	
	public static PropertiesUtil getInstance() {
		if (instance == null) {
			synchronized (PropertiesUtil.class) {
				if (instance == null) {
					instance = new PropertiesUtil();
				}
			}
		}
		return instance;
	}
	
	public void load() throws IOException {
		InputStream ipStream = null;
		try {
			props = new Properties();
			ipStream = PropertiesUtil.class
					.getResourceAsStream("../resources/Application.properties");
			props.load(ipStream);
			
			gmailprops = new Properties();
			gmailprops.put("mail.smtps.host", props.getProperty("GMAIL_HOST"));
			gmailprops.put("mail.transport.protocol", "smtp");
			gmailprops.put("mail.smtps.auth", "true");
			gmailprops.put("mail.smtp.port", props.getProperty("GMAIL_PORT"));
			gmailprops.put("mail.smtp.socketFactory.port", props.getProperty("GMAIL_PORT"));
			gmailprops.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			gmailprops.put("mail.smtp.socketFactory.fallback", "false");
			gmailprops.put("mail.smtp.ssl.enable", "true");
			gmailprops.put("mail.smtp.user", props.getProperty("GMAIL_USER"));
			gmailprops.put("mail.smtp.password", props.getProperty("GMAIL_PWD"));
			
			/*dmailprops = new Properties();
			dmailprops.put("mail.transport.protocol", "smtp");
			dmailprops.put("mail.host", props.getProperty("OFFICEMAIL_HOST"));
			dmailprops.put("mail.smtp.port", props.getProperty("OFFICEMAIL_PORT"));
			dmailprops.put("mail.smtp.user", props.getProperty("OFFICEMAIL_USER"));*/
		} catch (IOException e) {
			throw new ApplicationException(MessagesEnum.APP_PROPERTIES_LOADING_FAILED.getMessage(), e);
		} finally {
			try {
				if (ipStream != null) {
					ipStream.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public Properties getProps() {
		return props;
	}
	
	public Properties getGmailProps() {
		return gmailprops;
	}
	
	public Properties getDmailProps() {
		return dmailprops;
	}
}
