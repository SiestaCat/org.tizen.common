package org.tizen.common.core.command;

public class UntrackedException extends RuntimeException {
  private static final long serialVersionUID = 2958587918624984128L;
  
  public UntrackedException(String message, Throwable cause) {
    super(message, cause);
  }
}
