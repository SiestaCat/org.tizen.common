package org.tizen.common.sdb.command.message;

public interface CommandErrorType {
  boolean findErrorType(int paramInt, String paramString);
  
  String getMessage();
  
  int getExitCode();
  
  void setCommand(String paramString);
  
  void setCommandOutput(String paramString);
  
  void makeException() throws CommandErrorException;
}
