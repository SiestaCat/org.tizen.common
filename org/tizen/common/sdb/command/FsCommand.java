package org.tizen.common.sdb.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;

public class FsCommand extends ApplicationCommand {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected static String REMOVE_FILE_COMMAND = "rm -rf %s";
  
  protected static String MAKE_DIRECTORY_COMMAND = "mkdir -p -m 755 \"%s\"";
  
  protected static String CHANGE_OWNER_COMMAND = "chown -R app:app \"%s\"";
  
  protected static String EXTRACT_COMMAND = "unzip -o \"%s\" -d \"%s\"";
  
  protected static String LS_COMMAND = "ls \"%s\"";
  
  protected static String SYMBOLIC_LINK_COMMAND = "ln -sf \"%s\" \"%s\"";
  
  protected static String EXTRACT_COMMAND_WITH_C = "unzip -c \"%s\" \"%s\" > \"%s\"";
  
  protected static String ONDEMAND_INSTALL_COMMAND = "cd %s && %s %s && rm -f %s";
  
  protected static String ONDEMAND_INSTALL_TAR = "tar -xf";
  
  protected static String ONDEMAND_INSTALL_RPM = "rpm -U";
  
  protected static String ONDEMAND_TYPE_RPM = "rpm";
  
  public static final String CMD_RESULT_SUCCESS = "0";
  
  protected String command = "";
  
  protected String resultMsg = "";
  
  public FsCommand(IDevice device, ITizenConsoleManager console) {
    super(device, console);
  }
  
  public void removeFile(String filePath) throws Exception {
    this.command = String.format(REMOVE_FILE_COMMAND, new Object[] { filePath });
    this.resultMsg = "Remove file %s: " + filePath;
    executeCommand();
    isSuccess();
  }
  
  public boolean mkdir(String dirPath) throws Exception {
    this.command = String.format(MAKE_DIRECTORY_COMMAND, new Object[] { dirPath });
    this.resultMsg = "mkdir command %s: " + dirPath;
    executeCommand();
    return isSuccess();
  }
  
  public void chown(String path) throws Exception {
    this.command = String.format(CHANGE_OWNER_COMMAND, new Object[] { path });
    this.resultMsg = "chown command %s: " + path;
    executeCommand();
    isSuccess();
  }
  
  public boolean unzipCOptionAndCreateFile(String zipFilePath, String infoFilePathinZip, String extractInfoFilePath) throws Exception {
    this.command = String.format(EXTRACT_COMMAND_WITH_C, new Object[] { zipFilePath, infoFilePathinZip, extractInfoFilePath });
    this.resultMsg = "unzip with c option %s: " + zipFilePath;
    executeCommand();
    return isSuccess();
  }
  
  public void unzip(String remoteZipPath, String appInstallPath) throws Exception {
    this.command = String.format(EXTRACT_COMMAND, new Object[] { remoteZipPath, appInstallPath });
    this.resultMsg = "unzip command %s: " + remoteZipPath;
    executeCommand();
    isSuccess();
  }
  
  public boolean isExists(String path) throws Exception {
    this.command = String.format(LS_COMMAND, new Object[] { path });
    this.resultMsg = "isExists command(ls command) %s: " + path;
    executeCommand();
    return isSuccess();
  }
  
  public boolean createSymbolicLink(String source, String target) throws Exception {
    this.command = String.format(SYMBOLIC_LINK_COMMAND, new Object[] { source, target });
    this.resultMsg = "ln command %s: " + source + " to " + target;
    executeCommand();
    return isSuccess();
  }
  
  protected String getCommand() {
    this.command = String.valueOf(this.command) + "; echo $?;";
    return this.command;
  }
  
  protected void executeCommand() throws Exception {
    this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
    this.helper.runCommand(getCommand());
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
