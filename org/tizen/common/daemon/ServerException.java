package org.tizen.common.daemon;

public class ServerException extends Exception {
  private static final long serialVersionUID = 3662986092815719651L;
  
  public ServerException() {}
  
  public ServerException(String msg) {
    super(msg);
  }
  
  public ServerException(Throwable cause) {
    super(cause);
  }
  
  public ServerException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
