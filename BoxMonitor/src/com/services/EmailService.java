package com.services;

import java.util.Properties;

public interface EmailService {
	void sendMail(final Properties props, final String receipents,
			final String subject, final String body) throws Exception;
	void sendGMail(final Properties props, final String receipents,
			final String subject, final String body) throws Exception;
}
