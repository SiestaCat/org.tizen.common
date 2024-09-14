package org.tizen.common.sdb.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.TizenPlatformConstants;
import org.tizen.common.rds.RdsDeployer;
import org.tizen.common.sdb.command.receiver.PkgCmdReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;
import org.tizen.sdblib.service.SyncResult;

public class RdsCommand extends ApplicationCommand {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public RdsCommand(IDevice device, ITizenConsoleManager console) {
    super(device, console);
  }
  
  public boolean rdsPush(String pkgId, String localPackagePath, String remotePackagePath) throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.unzipPackage(localPackagePath, pkgId, (IShellOutputReceiver)this.receiver))
        return false; 
    } else {
      String strAppInstallPath = String.valueOf(TizenPlatformConstants.getAppTmpDirectory(this.device)) + "/" + pkgId;
      FsCommand command = new FsCommand(this.device, getConsole());
      command.removeFile(strAppInstallPath);
      SyncResult result = this.device.getSyncService().push(localPackagePath, this.device.getFileEntry(remotePackagePath).getParent());
      if (!result.isOk())
        return false; 
      command.unzip(remotePackagePath, strAppInstallPath);
      command.removeFile(remotePackagePath);
    } 
    return true;
  }
  
  public boolean rdsInstall(String pkgId, String pkgType, String localPackagePath, String remotePackagePath) throws Exception {
    boolean isSuccess = false;
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = this.device.getApplicationCmdService();
      try {
        isSuccess = appcmd.installPackageWithRDS(localPackagePath, pkgId, (IShellOutputReceiver)new PkgCmdReceiver(getConsole()));
      } catch (Exception e) {
        newCoreException(RdsDeployer.makeRdsLog("rds appcmd error"), e);
      } 
    } else {
      String strAppInstallPath = String.valueOf(TizenPlatformConstants.getAppTmpDirectory(this.device)) + "/" + pkgId;
      rdsPush(pkgId, localPackagePath, remotePackagePath);
      ReInstallCommand command = new ReInstallCommand(getDevice(), pkgType, pkgId, 20000, getConsole());
      command.execute();
      FsCommand fsCommand = new FsCommand(this.device, getConsole());
      fsCommand.removeFile(strAppInstallPath);
      isSuccess = true;
    } 
    return isSuccess;
  }
}
