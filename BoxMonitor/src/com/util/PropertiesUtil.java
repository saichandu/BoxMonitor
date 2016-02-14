package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.exception.ApplicationException;
import com.exception.MessagesEnum;

public class PropertiesUtil {

	private static volatile PropertiesUtil instance;
	
	private Properties props;
	
	private Properties emailprops;
	
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
			
			emailprops = new Properties();
			emailprops.put("mail.smtps.host","smtp.gmail.com");
			emailprops.put("mail.transport.protocol", "smtp");
			emailprops.put("mail.smtps.auth", "true");
			emailprops.put("mail.smtp.port", 465);
			emailprops.put("mail.smtp.socketFactory.port", 465);
			emailprops.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			emailprops.put("mail.smtp.socketFactory.fallback", "false");
			emailprops.put("mail.smtp.ssl.enable", "true");
			emailprops.put("mail.smtp.user", "boxmonitorapp@gmail.com");
			emailprops.put("mail.smtp.password", "Welcome5$");
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
	
	public Properties getEmailProps() {
		return emailprops;
	}
}
