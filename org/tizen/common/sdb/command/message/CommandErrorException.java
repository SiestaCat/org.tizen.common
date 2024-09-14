package org.tizen.common.sdb.command.message;

public class CommandErrorException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private ErrorMessageType errorType;
  
  private String errorMsg = "";
  
  private int errorCode = 0;
  
  public CommandErrorException() {}
  
  public CommandErrorException(String msg) {
    super(msg);
  }
  
  public CommandErrorException(String msg, Throwable t) {
    super(msg, t);
  }
  
  public CommandErrorException(Throwable t) {
    super(t);
  }
  
  public CommandErrorException(ErrorMessageType errorType, String msg) {
    super(msg);
    this.errorType = errorType;
  }
  
  public CommandErrorException(ErrorMessageType errorType, String msg, String errorMsg, int errorCode) {
    this(errorType, msg);
    this.errorMsg = errorMsg;
    this.errorCode = errorCode;
  }
  
  public ErrorMessageType getErrorType() {
    return this.errorType;
  }
  
  public String getErrorMessage() {
    return this.errorMsg;
  }
  
  public int getErrorCode() {
    return this.errorCode;
  }
  
  public boolean hasHandler() {
    return (this.errorType != null && this.errorType.getErrorHandler() != null);
  }
  
  public void executeHandler() {
    if (this.errorType != null && this.errorType.getErrorHandler() != null)
      this.errorType.getErrorHandler().handle(this); 
  }
}
