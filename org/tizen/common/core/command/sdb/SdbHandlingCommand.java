package org.tizen.common.core.command.sdb;

import org.tizen.common.core.command.Command;

public abstract class SdbHandlingCommand implements Command<String> {
  protected StringBuffer result;
  
  public void clearResult() {
    if (this.result == null) {
      this.result = new StringBuffer();
    } else {
      this.result.delete(0, this.result.capacity());
      this.result.setLength(0);
    } 
  }
  
  public String getResult() {
    if (this.result == null)
      return null; 
    return this.result.toString();
  }
  
  public void setResult(String result) {
    if (this.result == null)
      this.result = new StringBuffer(); 
    this.result.append(result);
  }
}
