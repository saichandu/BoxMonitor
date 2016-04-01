package com.listener;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.exception.ApplicationException;
import com.mongo.MongoDBConnManager;
import com.scheduler.LeftOverBookingsCloseScheduler;
import com.util.PropertiesUtil;

public class ApplicationContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		try {
			// create the timer and timer task objects
			Timer timer = new Timer();
			LeftOverBookingsCloseScheduler task = new LeftOverBookingsCloseScheduler();

			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 22);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);

			// schedule the task
			timer.schedule(task, today.getTime(),
					TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // 60*60*24*100 = 8640000ms

			// save the timer in context
			servletContext.setAttribute("timer", timer);

		} catch (Exception e) {
			throw new ApplicationException("Problem initializing the scheduled task: ", e);
		}
         
    	//Load properties
    	try {
			PropertiesUtil.getInstance().load();
		} catch (Exception e) {
			throw new ApplicationException("Failed to load the application properties", e);
		}
    }

	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		ServletContext servletContext = servletContextEvent.getServletContext();

		// get our timer from the context
		Timer timer = (Timer) servletContext.getAttribute("timer");

		// cancel all active tasks in the timers queue
		if (timer != null) {
			timer.cancel();
		}

		// remove the timer from the context
		servletContext.removeAttribute("timer");

		// Close Mongo DB Client once the application is stopped
		MongoDBConnManager.getInstance().closeMongoClient();
	}
}