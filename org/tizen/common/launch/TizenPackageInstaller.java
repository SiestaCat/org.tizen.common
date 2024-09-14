package org.tizen.common.launch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.service.SyncResult;
import org.tizen.sdblib.service.SyncService;
import org.tizen.sdblib.util.IOUtil;

public class TizenPackageInstaller {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  List<ITizenPackageConfiguration> configList = null;
  
  private String profileName;
  
  private IDevice device;
  
  private IProject project;
  
  public TizenPackageInstaller(IProject project, IDevice device, String profileName) {
    this.project = project;
    this.device = device;
    this.profileName = profileName;
  }
  
  private void initConfigList() {
    if (this.configList == null) {
      this.configList = new ArrayList<>();
      IExtensionRegistry x = RegistryFactory.getRegistry();
      IConfigurationElement[] ces = x.getConfigurationElementsFor("org.tizen.common.package.install");
      byte b;
      int i;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (i = (arrayOfIConfigurationElement1 = ces).length, b = 0; b < i; ) {
        IConfigurationElement ce = arrayOfIConfigurationElement1[b];
        if ("configuration".equals(ce.getName())) {
          String className = ce.getAttribute("class");
          if (className != null)
            try {
              Object obj = ce.createExecutableExtension("class");
              if (obj instanceof ITizenPackageConfiguration)
                this.configList.add((ITizenPackageConfiguration)obj); 
            } catch (CoreException e) {
              this.logger.error("Failed to add " + ce + " to configList", (Throwable)e);
            }  
        } 
        b++;
      } 
    } 
  }
  
  public boolean installDeviceProfile() throws CoreException {
    boolean isRet = true;
    initConfigList();
    SyncService sync = null;
    try {
      sync = this.device.getSyncService();
    } catch (IOException e1) {
      this.logger.error("Failed to get a syncService: " + this.device, e1);
      return false;
    } 
    try {
      for (ITizenPackageConfiguration config : this.configList) {
        File profileFile = config.getDeviceProfileFile(this.profileName, this.device, this.project);
        File destFile = config.getDeviceProfileDest(this.device, this.project);
        if (profileFile == null || destFile == null)
          continue; 
        try {
          SyncResult result = sync.push(profileFile.getCanonicalPath(), this.device.getFileEntry(destFile.toString()));
          if (!result.isOk())
            this.logger.error("Failed to copy " + profileFile + " to " + destFile); 
        } catch (IOException e) {
          this.logger.error("Failed to copy " + profileFile, e);
        } 
      } 
    } finally {
      IOUtil.tryClose((Closeable)sync);
    } 
    return isRet;
  }
}
