package org.tizen.common.sdb.command.receiver;

import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.receiver.MultiLineReceiver;

public class CommandOutputReceiver extends MultiLineReceiver {
  protected StringBuilder commandOutput = new StringBuilder(256);
  
  protected String endLine = null;
  
  protected ITizenConsoleManager console = null;
  
  public CommandOutputReceiver(ITizenConsoleManager console) {
    setTrimLines(false);
    this.console = console;
  }
  
  public void processNewLines(String[] lines) {
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = lines).length, b = 0; b < i; ) {
      String line = arrayOfString[b];
      if (this.console != null)
        this.console.println(line); 
      this.commandOutput.append(line);
      this.commandOutput.append(System.getProperty("line.separator"));
      b++;
    } 
    this.endLine = lines[lines.length - 1];
  }
  
  public String getCommandOutput() {
    return this.commandOutput.toString();
  }
  
  public String getEndLine() {
    return this.endLine;
  }
}
