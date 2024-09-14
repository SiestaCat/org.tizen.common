package org.tizen.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.util.DialogUtil;
import org.tizen.sdblib.ICrashReportServiceListener;
import org.tizen.sdblib.SmartDevelopmentBridge;
import org.tizen.sdblib.exception.ServerException;
import org.tizen.sdblib.service.CrashReportService;

public class CommonStartup implements IStartup {
  public void earlyStartup() {
    (new Thread("Start SmartDevelopmentBridge") {
        public void run() {
          CommonStartup.this.startSmartDevelopmentBridge();
        }
      }).start();
    try {
      initializeCrashReportService();
    } catch (ServerException serverException) {}
  }
  
  private void startSmartDevelopmentBridge() {
    SmartDevelopmentBridge bridge = SmartDevelopmentBridge.getBridge();
    if (bridge != null) {
      bridge.startBridge();
      try {
        bridge.waitforStart(5000L);
        if (!bridge.getStarted()) {
          DialogUtil.openErrorDialog("Failed to start sdb");
          bridge.stopBridge();
        } 
        String sdbLogPath = InstallPathConfig.getIdeUserDataLogPath();
        bridge.setSdbLogLocation(sdbLogPath);
      } catch (Throwable t) {
        CommonPlugin.getDefault().getLog().log((IStatus)new Status(4, "org.tizen.common", "Problem occurred while initializing sdb", t));
        DialogUtil.openErrorDialog("Failed to start sdb");
      } 
    } 
  }
  
  protected void initializeCrashReportService() throws ServerException {
    IExtensionRegistry x = RegistryFactory.getRegistry();
    IConfigurationElement[] ces = x.getConfigurationElementsFor(
        "org.tizen.common.crashreporter");
    byte b;
    int i;
    IConfigurationElement[] arrayOfIConfigurationElement1;
    for (i = (arrayOfIConfigurationElement1 = ces).length, b = 0; b < i; ) {
      IConfigurationElement ce = arrayOfIConfigurationElement1[b];
      if ("client".equals(ce.getName())) {
        String className = ce.getAttribute("class");
        if (className != null)
          try {
            Object obj = ce.createExecutableExtension("class");
            if (obj instanceof ICrashReportServiceListener)
              CrashReportService.getDefault().addCrashReportServiceListener((ICrashReportServiceListener)obj); 
          } catch (CoreException e) {
            CommonPlugin.getDefault().getLog().log((IStatus)new Status(2, "org.tizen.common", "Error occurred while adding cs create listener", (Throwable)e));
          }  
      } 
      b++;
    } 
    CrashReportService.getDefault().boot();
  }
}
