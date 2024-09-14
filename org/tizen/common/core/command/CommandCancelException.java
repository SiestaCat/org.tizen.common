package org.tizen.common.core.command;

public class CommandCancelException extends RuntimeException {
  private static final long serialVersionUID = -3303351863981251390L;
  
  public CommandCancelException() {}
  
  public CommandCancelException(String msg) {
    super(msg);
  }
  
  public CommandCancelException(Throwable t) {
    super(t);
  }
  
  public CommandCancelException(String msg, Throwable t) {
    super(msg, t);
  }
}
