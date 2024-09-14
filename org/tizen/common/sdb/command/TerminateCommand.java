package org.tizen.common.sdb.command;

import org.tizen.common.sdb.command.message.PkgcmdErrorType;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;

public class TerminateCommand extends ApplicationCommand {
  public static final String APP_TERMINATE_COMMAND = "/usr/bin/pkgcmd -k -t %s -n %s";
  
  protected String pkgId = null;
  
  private String pkgType = "TPK".toLowerCase();
  
  public TerminateCommand(IDevice device, String pkgId, String pkgType, ITizenConsoleManager console) {
    super(device, console);
    this.pkgId = pkgId;
    this.pkgType = pkgType;
  }
  
  protected void executeCommand() throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.killApplication(this.pkgId, (IShellOutputReceiver)this.receiver))
        newCoreException("Terminate command failed: " + this.pkgId, null); 
    } else {
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
      this.helper.runPkgCmd(getCommand(), new PkgcmdErrorType(), -1);
    } 
  }
  
  protected String getCommand() {
    String command = String.format("/usr/bin/pkgcmd -k -t %s -n %s", new Object[] { getPkgType(), getPkgId() });
    return command;
  }
  
  protected String getPkgId() {
    return this.pkgId;
  }
  
  protected String getPkgType() {
    return this.pkgType;
  }
}
