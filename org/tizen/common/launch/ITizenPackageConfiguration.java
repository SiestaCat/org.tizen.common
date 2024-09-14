package org.tizen.common.launch;

import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.tizen.sdblib.IDevice;

public interface ITizenPackageConfiguration {
  public static final String EXTENSION_POINT_ID = "org.tizen.common.package.install";
  
  public static final String ATTR_CONFIG = "configuration";
  
  File getDeviceProfileFile(String paramString, IDevice paramIDevice, IProject paramIProject) throws CoreException;
  
  File getDeviceProfileDest(IDevice paramIDevice, IProject paramIProject) throws CoreException;
}
