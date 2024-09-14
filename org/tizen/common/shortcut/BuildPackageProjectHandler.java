package org.tizen.common.shortcut;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.ProjectUtil;

public class BuildPackageProjectHandler extends AbstractHandler {
  private static final Logger logger = LoggerFactory.getLogger(BuildPackageProjectHandler.class);
  
  private static String extPointId = "org.eclipse.ui.handlers";
  
  private static String classIdNative = "org.tizen.nativecore.build.BuildPackageObjectAction";
  
  private static String classIdWeb = "org.tizen.web.ui.command.popupMenu.buildPackage";
  
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IProject project = ProjectUtil.getProject(event);
    try {
      IHandler obj = null;
      if (ProjectUtil.isTizenNativeProject(project)) {
        obj = (IHandler)loadClass(extPointId, classIdNative);
      } else if (ProjectUtil.isTizenWebProject(project)) {
        obj = (IHandler)loadClass(extPointId, classIdWeb);
      } else {
        return null;
      } 
      if (obj != null)
        obj.execute(event); 
    } catch (Exception e) {
      logger.debug("failed to execute handlers", e);
    } 
    return null;
  }
  
  private Object loadClass(String extPointId, String commandId) throws CoreException {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint(extPointId);
    IExtension[] exts = ep.getExtensions();
    byte b;
    int i;
    IExtension[] arrayOfIExtension1;
    for (i = (arrayOfIExtension1 = exts).length, b = 0; b < i; ) {
      IExtension ext = arrayOfIExtension1[b];
      IConfigurationElement[] configs = ext.getConfigurationElements();
      byte b1;
      int j;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (j = (arrayOfIConfigurationElement1 = configs).length, b1 = 0; b1 < j; ) {
        IConfigurationElement config = arrayOfIConfigurationElement1[b1];
        String id = config.getAttribute("commandId");
        if (id != null && id.equals(commandId))
          return config.createExecutableExtension("class"); 
        b1++;
      } 
      b++;
    } 
    return null;
  }
}
