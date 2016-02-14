package com.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.exception.ApplicationException;
import com.mongo.MongoDBConnManager;
import com.util.PropertiesUtil;

public class ApplicationContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	//Load properties
    	try {
			PropertiesUtil.getInstance().load();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    	//Close Mongo DB Client once the application is stopped
    	MongoDBConnManager.getInstance().closeMongoClient();
    }
}