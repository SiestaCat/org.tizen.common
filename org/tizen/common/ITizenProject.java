package org.tizen.common;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.tizen.common.core.application.TizenPackageInfoStore;
import org.tizen.common.core.application.TizenProjectDescription;
import org.tizen.common.rds.DeltaResourceInfo;

public interface ITizenProject {
  String getAppId();
  
  String getPackageId();
  
  TizenProjectType getTizenProjectType();
  
  TizenProjectDescription getDescription();
  
  TizenProjectDescription getDescription(boolean paramBoolean);
  
  boolean setDescription(TizenProjectDescription paramTizenProjectDescription);
  
  List<DeltaResourceInfo> setSubAppInterestList(IProject paramIProject, boolean paramBoolean1, boolean paramBoolean2);
  
  List<DeltaResourceInfo> setSubAppIgnoreList();
  
  TizenPackageInfoStore getTizenPkgInfoStore();
}
