package org.tizen.common;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.core.application.ProfileInfo;
import org.tizen.common.core.application.TizenProjectDescription;
import org.tizen.common.core.application.tproject.TprojectHandler;
import org.tizen.common.rds.DeltaResourceInfo;

public abstract class AbstractTizenProject implements ITizenProject {
  public TizenProjectDescription getDescription() {
    return getDescription(true);
  }
  
  public TizenProjectDescription getDescription(boolean generate) {
    TprojectHandler handler = new TprojectHandler(getProject());
    TizenProjectDescription desc = handler.unmarshal();
    if (desc != null)
      return desc; 
    if (generate) {
      desc = new TizenProjectDescription(getProject());
      ProfileInfo latestProfileInfo = InstallPathConfig.getLatestProfileInfo();
      if (latestProfileInfo != null)
        desc.setPlatform(latestProfileInfo); 
      if (setDescription(desc))
        return desc; 
      desc = new TizenProjectDescription(getProject());
    } 
    return desc;
  }
  
  public boolean setDescription(TizenProjectDescription desc) {
    TprojectHandler handler = new TprojectHandler(getProject());
    return handler.marshal(desc);
  }
  
  protected abstract IProject getProject();
  
  public List<DeltaResourceInfo> setSubAppInterestList(IProject project, boolean isRootNative, boolean bFirst) {
    return null;
  }
  
  public List<DeltaResourceInfo> setSubAppIgnoreList() {
    return null;
  }
}
