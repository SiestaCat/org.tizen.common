package org.tizen.common.sdb.command.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tizen.sdblib.util.StringUtil;

public class PkgcmdErrorType implements CommandErrorType {
  private static final String ERROR_MESSAGE_TEMPLATE = "error message: ";
  
  private static final int DEFAULT_ERROR_CODE = 9999;
  
  private ErrorMessageType errorType;
  
  private String command;
  
  private String commandOutput;
  
  private String backendErrorMessage = "";
  
  private String errorMessage = "";
  
  private int errorCode = 9999;
  
  private final Pattern resultPattern = Pattern.compile("processing result : (.*) \\[(.*)\\] failed");
  
  public boolean findErrorType(int exitCode, String command) {
    if (this.errorCode == 9999)
      this.errorCode = exitCode; 
    this.errorType = ErrorMessageType.getErrorType(this.errorCode);
    if (this.errorType == null)
      return false; 
    this.command = command;
    return true;
  }
  
  public String getMessage() {
    if (this.errorType == null) {
      if (!StringUtil.isEmpty(this.errorMessage))
        return String.format("%s [%d]", new Object[] { this.errorMessage, Integer.valueOf(this.errorCode) }); 
      return null;
    } 
    String errorMsg = this.errorMessage;
    if (StringUtil.isEmpty(this.errorMessage) && !StringUtil.isEmpty(this.backendErrorMessage))
      errorMsg = this.backendErrorMessage; 
    return String.format(PkgcmdErrorMessages.MESSAGE_FORMAT, new Object[] { this.errorType.name(), errorMsg, this.command, this.errorType.getManagement() });
  }
  
  private void parseErrorResultMessage() {
    Matcher matcher = this.resultPattern.matcher(this.commandOutput);
    if (matcher.find()) {
      String message = matcher.group(1);
      String code = matcher.group(2);
      this.errorMessage = (message != null) ? message : "";
      this.errorCode = (code != null) ? Integer.parseInt(code) : 9999;
    } 
  }
  
  public int getExitCode() {
    if (this.errorType == null)
      return -1; 
    return this.errorType.getExitCode();
  }
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public void setCommandOutput(String commandOutput) {
    this.commandOutput = commandOutput;
    this.backendErrorMessage = (this.commandOutput == null) ? "" : parseErrorMessage();
    parseErrorResultMessage();
  }
  
  public void makeException() throws CommandErrorException {
    String exceptionMessage = getMessage();
    if (this.errorType == null && 
      StringUtil.isEmpty(exceptionMessage))
      throw new CommandErrorException(PkgcmdErrorMessages.ERROR_UNKNOWN); 
    if (!ErrorMessageType.SUCCESS.equals(this.errorType))
      throw new CommandErrorException(this.errorType, exceptionMessage, this.errorMessage, this.errorCode); 
  }
  
  public String toString() {
    return getMessage();
  }
  
  private String parseErrorMessage() {
    int startIdx = this.commandOutput.indexOf("error message: ");
    int endIdx = this.commandOutput.indexOf(System.getProperty("line.separator"), startIdx);
    if (startIdx == -1 || endIdx == -1)
      return ""; 
    return this.commandOutput.substring(startIdx + "error message: ".length(), endIdx).replaceAll("\\|", "\n");
  }
}
