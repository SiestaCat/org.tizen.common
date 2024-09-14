package org.tizen.common.core.command.sdb;

import java.io.Closeable;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.Assert;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.receiver.MultiLineReceiver;
import org.tizen.sdblib.util.IOUtil;

public class ShellSdbCommand extends SdbDevicesHandlingCommand {
  protected boolean printOption = true;
  
  private String command;
  
  public boolean isPrintOption() {
    return this.printOption;
  }
  
  public void setPrintOption(boolean printOption) {
    this.printOption = printOption;
  }
  
  private static int DEFAULT_TIME = 90;
  
  private int time;
  
  public int getTime() {
    return this.time;
  }
  
  public void setTime(int time) {
    this.time = (time <= 0) ? DEFAULT_TIME : time;
  }
  
  public ShellSdbCommand() {
    this.time = DEFAULT_TIME;
  }
  
  public ShellSdbCommand(String command) {
    this.command = command;
    this.time = DEFAULT_TIME;
  }
  
  public String getCommand() {
    return this.command;
  }
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public MultiLineReceiver createMultiLineReceiver() {
    return new MultiLineReceiver() {
        public void processNewLines(String[] lines) {
          byte b;
          int i;
          String[] arrayOfString;
          for (i = (arrayOfString = lines).length, b = 0; b < i; ) {
            String content = arrayOfString[b];
            ShellSdbCommand.this.setResult(String.valueOf(content) + System.getProperty("line.separator"));
            b++;
          } 
        }
      };
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    IDevice device = getDevice();
    Assert.notNull(device);
    Assert.notNull(this.command);
    clearResult();
    MultiLineReceiver receiver = null;
    try {
      receiver = createMultiLineReceiver();
      device.executeShellCommand(this.command, (IShellOutputReceiver)receiver, getTime() * 1000);
    } catch (Exception e) {
      if (isPrintOption()) {
        String msg = "Operation failed.";
        context.getPrompter().notify(msg);
      } 
      throw e;
    } finally {
      IOUtil.tryClose((Closeable)receiver);
    } 
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {}
}
