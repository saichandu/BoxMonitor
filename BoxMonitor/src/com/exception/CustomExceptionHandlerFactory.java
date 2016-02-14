package com.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * @author saavvaru Created May 31st 2015
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {


 private ExceptionHandlerFactory parent;

  public CustomExceptionHandlerFactory(final ExceptionHandlerFactory parent) {
    this.parent = parent;
  }

  @Override
  public final ExceptionHandler getExceptionHandler() {
      return new CustomExceptionHandler(parent.getExceptionHandler());

  }

}