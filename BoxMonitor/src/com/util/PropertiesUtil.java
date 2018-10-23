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
			ipStream = PropertiesUtil.class.getClassLoader()
					.getResourceAsStream("Application.properties");
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
			
			dmailprops = new Properties();
			dmailprops.setProperty("mail.transport.protocol", "smtp");
			dmailprops.setProperty("mail.host", props.getProperty("mail.host"));
			dmailprops.setProperty("mail.smtp.port", props.getProperty("mail.smtp.port"));
			dmailprops.setProperty("mail.smtp.user", props.getProperty("mail.fromMail"));
		} catch (IOException e) {
			e.printStackTrace();
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
