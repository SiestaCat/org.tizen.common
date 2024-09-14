package org.tizen.common.sdb.command;

import org.tizen.common.sdb.command.message.PkgcmdErrorType;
import org.tizen.common.sdb.command.receiver.PkgCmdReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;

public class UnInstallCommand extends ApplicationCommand {
  protected String APP_UNINSTALL_COMMAND = "/usr/bin/pkgcmd -q -u -t \"%s\" -n \"%s\"";
  
  protected String pkgId = null;
  
  protected String pkgType = null;
  
  public UnInstallCommand(IDevice device, String pkgId, String pkgType, ITizenConsoleManager console) {
    super(device, console);
    this.pkgId = pkgId;
    this.pkgType = pkgType;
  }
  
  protected void executeCommand() throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.uninstallPackage(this.pkgId, (IShellOutputReceiver)this.receiver))
        newCoreException("Unistall command failed: " + this.pkgId, null); 
    } else {
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), new PkgCmdReceiver(getConsole()));
      this.helper.runPkgCmd(getCommand(), new PkgcmdErrorType(), -1);
    } 
  }
  
  public String getCommand() {
    String command = String.format(this.APP_UNINSTALL_COMMAND, new Object[] { getPkgType(), getPkgId() });
    return command;
  }
  
  public String getPkgId() {
    return this.pkgId;
  }
  
  public String getPkgType() {
    return this.pkgType;
  }
}
