package org.tizen.common.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.fastdeploy.FastDeployer;
import org.tizen.common.rds.RdsUtil;

public class ProjectDeletionListener implements IResourceChangeListener {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected IProject project;
  
  public void resourceChanged(IResourceChangeEvent event) {
    if (event.getType() == 4) {
      IResource rsrc = event.getResource();
      if (rsrc instanceof IProject)
        preProjectDelete((IProject)rsrc); 
    } 
  }
  
  private void preProjectDelete(IProject project) {
    RdsUtil.getInstance().removeRDSMeta(project);
    FastDeployer.reinstallComplete(project);
  }
}
