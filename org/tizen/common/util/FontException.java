package org.tizen.common.util;

public class FontException extends Exception {
  private static final long serialVersionUID = -7698066985055386605L;
  
  public FontException(Throwable e) {
    super(e);
  }
  
  public FontException(String msg) {
    super(msg);
  }
  
  public FontException(String msg, Throwable e) {
    super(msg, e);
  }
}
