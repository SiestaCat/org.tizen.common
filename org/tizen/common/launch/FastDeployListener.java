package org.tizen.common.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.rds.ui.preference.RdsPreferencePage;
import org.tizen.common.util.ProjectUtil;
import org.tizen.sdblib.IDevice;

public abstract class FastDeployListener implements IResourceChangeListener {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final String EMPTY_STRING = "";
  
  protected IProject project;
  
  protected List<IResourceDelta> deltaList = new ArrayList<>();
  
  protected static HashMap<String, Boolean> disabledMap = new HashMap<>();
  
  public boolean deploy() throws CoreException {
    return true;
  }
  
  public boolean isInstalled() {
    return true;
  }
  
  public boolean isSupportType() {
    return true;
  }
  
  public IDevice getDevice() {
    return null;
  }
  
  public boolean isExecutable() {
    if (this.project == null)
      return false; 
    if (!isEnabled(this.project))
      return false; 
    if (!isSupportType())
      return false; 
    if (!RdsPreferencePage.isRdsMode(this.project))
      return false; 
    if (verifyProjectError())
      return false; 
    if (getDevice() == null)
      return false; 
    if (!isInstalled())
      return false; 
    return true;
  }
  
  public boolean isAcceptableEvent(IResourceChangeEvent event) {
    return true;
  }
  
  public void resourceChanged(IResourceChangeEvent event) {
    IResourceDelta[] children = event.getDelta().getAffectedChildren();
    if (children.length == 0)
      return; 
    this.project = ProjectUtil.getProject(children[0].getFullPath());
    if (!isExecutable())
      return; 
    if (!isAcceptableEvent(event))
      return; 
    try {
      deploy();
    } catch (Exception e) {
      this.logger.error("[FastDeploy] An exception occurs during launch in FastDeploy mode.", e);
      return;
    } 
  }
  
  public boolean verifyProjectError() {
    return false;
  }
  
  public static void enableFastDeployListener(IProject project) {
    disabledMap.remove(project.getLocation().toString());
  }
  
  public static void disableFastDeployListener(IProject project) {
    disabledMap.put(project.getLocation().toString(), Boolean.valueOf(true));
  }
  
  public static boolean isEnabled(IProject project) {
    if (disabledMap.get(project.getLocation().toString()) == null)
      return true; 
    return false;
  }
}
