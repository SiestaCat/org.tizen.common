package org.tizen.common.sdb.command;

import org.tizen.common.sdb.command.message.PkgcmdErrorType;
import org.tizen.common.sdb.command.receiver.PkgCmdReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;

public class InstallCommand extends ApplicationCommand {
  protected String APP_INSTALL_COMMAND = "/usr/bin/pkgcmd -q -i -t \"%s\" -p \"%s\" %s";
  
  protected ApplicationCmdService.PackageType pkgType = null;
  
  protected String path = null;
  
  protected String args = "";
  
  public InstallCommand(IDevice device, ApplicationCmdService.PackageType pkgType, String pkgPath, String args, ITizenConsoleManager console) {
    super(device, console);
    this.pkgType = pkgType;
    this.path = pkgPath;
    this.args = args;
  }
  
  protected InstallCommand(IDevice device, ITizenConsoleManager console) {
    super(device, console);
  }
  
  protected void executeCommand() throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.installPackage(this.pkgType, getPath(), null, null, (IShellOutputReceiver)this.receiver))
        newCoreException("Install command failed: " + getPath(), null); 
    } else {
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), new PkgCmdReceiver(getConsole()));
      this.helper.runPkgCmd(getCommand(), new PkgcmdErrorType(), -1);
    } 
  }
  
  protected String getCommand() {
    String command = String.format(this.APP_INSTALL_COMMAND, new Object[] { getPkgType(), getPath(), getArgs() });
    return command;
  }
  
  protected ApplicationCmdService.PackageType getPkgType() {
    return this.pkgType;
  }
  
  protected String getPath() {
    return this.path;
  }
  
  protected String getArgs() {
    return this.args;
  }
}
