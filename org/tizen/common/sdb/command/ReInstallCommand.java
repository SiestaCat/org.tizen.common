package org.tizen.common.sdb.command;

import org.tizen.common.sdb.command.message.PkgcmdErrorType;
import org.tizen.common.sdb.command.receiver.PkgCmdReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.PlatformCapability;
import org.tizen.sdblib.service.ApplicationCmdService;

public class ReInstallCommand extends ApplicationCommand {
  protected String APP_REINSTALL_COMMAND = "pkgcmd -q -r -t \"%s\" -n \"%s\" %s";
  
  protected String pkgType = null;
  
  protected String pkgId = null;
  
  protected int timeout = -1;
  
  protected String args = "";
  
  public ReInstallCommand(IDevice device, String pkgType, String pkgId, int timeout, ITizenConsoleManager console) {
    super(device, console);
    this.pkgType = pkgType;
    this.pkgId = pkgId;
    this.timeout = timeout;
  }
  
  protected void executeCommand() throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.reinstallPackage(this.pkgId, (IShellOutputReceiver)this.receiver, null))
        newCoreException("Reinstall failed: " + this.pkgId, null); 
    } else {
      this.helper = new SdbCommandHelper(this.device, getConsole(), new PkgCmdReceiver(getConsole()));
      getConsole().print("Reinstall: ");
      this.helper.runPkgCmd(getCommand(), new PkgcmdErrorType(), this.timeout);
    } 
  }
  
  protected String getCommand() {
    String command = String.format(this.APP_REINSTALL_COMMAND, new Object[] { getPkgType(), getPkgId(), getArgs() });
    return command;
  }
  
  protected String getPkgType() {
    return this.pkgType;
  }
  
  protected String getPkgId() {
    return this.pkgId;
  }
  
  protected int getTimeout() {
    return this.timeout;
  }
  
  protected String getArgs() {
    if (isDebugMode())
      this.args = String.valueOf(this.args) + "-G"; 
    return this.args;
  }
  
  private boolean isDebugMode() {
    PlatformCapability capa = null;
    boolean isDebugMode = false;
    try {
      capa = this.device.getPlatformCapability();
      isDebugMode = "enabled".equals(capa.getPkgcmdDebugModeSupport());
    } catch (Exception e) {
      isDebugMode = false;
      this.logger.debug("Failed to get platform capability.", e);
    } 
    return isDebugMode;
  }
}
