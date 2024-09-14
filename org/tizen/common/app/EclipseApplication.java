package org.tizen.common.app;

import org.eclipse.core.resources.IProject;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.app.Application;
import org.tizen.sdblib.app.IApplicationType;

public class EclipseApplication extends Application {
  private IProject project;
  
  public EclipseApplication(IProject project, IDevice device, String name, String packageId, String appId, IApplicationType type) {
    super(device, name, packageId, appId, type);
    this.project = project;
  }
  
  public IProject getProject() {
    return this.project;
  }
}
