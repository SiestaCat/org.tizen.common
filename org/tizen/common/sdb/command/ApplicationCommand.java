package org.tizen.common.sdb.command;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.sdb.command.receiver.CommandOutputReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.SmartDevelopmentBridge;
import org.tizen.sdblib.service.ApplicationCmdService;

public class ApplicationCommand {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected IDevice device = null;
  
  protected ITizenConsoleManager launchConsole = null;
  
  protected SdbCommandHelper helper = null;
  
  protected CommandOutputReceiver receiver = null;
  
  public ApplicationCommand(IDevice device, ITizenConsoleManager console) {
    this.device = device;
    this.launchConsole = console;
    if (console != null)
      this.receiver = new CommandOutputReceiver(console); 
  }
  
  public void setConsole(ITizenConsoleManager console) {
    this.launchConsole = console;
  }
  
  public ITizenConsoleManager getConsole() {
    return this.launchConsole;
  }
  
  public IDevice getDevice() {
    return this.device;
  }
  
  protected void executeCommand() throws Exception {}
  
  public void execute() throws Exception {
    executeCommand();
  }
  
  public String getSdbPath() {
    SmartDevelopmentBridge sdb = SmartDevelopmentBridge.getBridge();
    String path = null;
    if (sdb != null)
      path = sdb.getSdbOsLocation(); 
    return path;
  }
  
  public void setReceiver(CommandOutputReceiver receiver) {
    this.receiver = receiver;
  }
  
  public static boolean isAppCmdSupported(IDevice device) {
    return false;
  }
  
  public ApplicationCmdService getApplicationCmdService() {
    return getDevice().getApplicationCmdService();
  }
  
  public void newCoreException(String message, Throwable exception) throws CoreException {
    Status status = new Status(4, "org.tizen.common", message, exception);
    throw new CoreException(status);
  }
  
  protected void print(String msg) {
    if (getConsole() != null)
      getConsole().print(msg); 
  }
}
