package org.tizen.common.sdb.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.TizenPlatformConstants;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;

public class OnDemandCommand extends ApplicationCommand {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected String ONDEMAND_INSTALL_COMMAND = "cd %s && %s %s && rm -f %s";
  
  protected String ONDEMAND_INSTALL_TAR = "tar -xf";
  
  protected String ONDEMAND_INSTALL_RPM = "rpm -U";
  
  protected String ONDEMAND_TYPE_RPM = "rpm";
  
  protected String command = "";
  
  protected String resultMsg = "";
  
  private String remote = null;
  
  private String local = null;
  
  private String type = null;
  
  public static final String CMD_RESULT_SUCCESS = "0";
  
  public OnDemandCommand(IDevice device, ITizenConsoleManager console, String remote, String local, String type) {
    super(device, console);
    this.remote = remote;
    this.local = local;
    this.type = type;
  }
  
  public void executeCommand() throws Exception {
    boolean isSuccess = false;
    String toolsTargetPath = TizenPlatformConstants.getToolsTargetDirectory(this.device);
    if (this.ONDEMAND_TYPE_RPM.equals(this.type)) {
      this.command = String.format(this.ONDEMAND_INSTALL_COMMAND, new Object[] { toolsTargetPath, this.ONDEMAND_INSTALL_RPM, this.remote, this.remote });
      this.resultMsg = "Ondemand install %s: " + this.local;
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
      this.helper.runCommand(getCommand());
      isSuccess = isSuccess();
    } else if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      isSuccess = appcmd.extractTarPackage(this.local, this.remote, (IShellOutputReceiver)this.receiver);
    } else {
      this.command = String.format(this.ONDEMAND_INSTALL_COMMAND, new Object[] { toolsTargetPath, this.ONDEMAND_INSTALL_TAR, this.remote, this.remote });
      this.resultMsg = "Ondemand install %s: " + this.local;
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
      this.helper.runCommand(getCommand());
      isSuccess = isSuccess();
    } 
    if (!isSuccess)
      newCoreException("Extract Tar Package command failed: " + this.local, null); 
  }
  
  protected String getCommand() {
    this.command = String.valueOf(this.command) + "; echo $?;";
    return this.command;
  }
  
  protected boolean isSuccess() {
    String result = this.helper.getEndLine();
    if (!"0".equals(result)) {
      String str = String.valueOf(String.format(this.resultMsg, new Object[] { "failed" })) + "\n";
      this.logger.error(str, this.helper.getCommandOutput());
      print(str);
      return false;
    } 
    String msg = String.valueOf(String.format(this.resultMsg, new Object[] { "complete" })) + "\n";
    print(msg);
    return true;
  }
}
