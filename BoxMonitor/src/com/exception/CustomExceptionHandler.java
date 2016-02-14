package com.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * @author saavvaru Created May 31st 2015
 * This class is to handle ViewExpiredException and redirect to generic error page.
 * This handles ajax and non-ajax calls
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private final ExceptionHandler wrapped;

	public CustomExceptionHandler(final ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;

	}

	public void handle() throws FacesException {
		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents()
				.iterator();

		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event
					.getSource();
			
			Throwable t;
			if (context.getException().getCause() == null) {
				t = context.getException();
			} else if (context.getException().getCause().getCause() == null){
				t = context.getException().getCause();
			} else if (context.getException().getCause().getCause().getCause() == null) {
				t = context.getException().getCause().getCause();
			} else {
				t = context.getException().getCause().getCause().getCause();
			}
			
			if (t instanceof ApplicationException) {
				try {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, ((ApplicationException) t).getMessage() , null));
				} finally {
					i.remove();
				}
			}
		}
		getWrapped().handle();
	}

}
