package org.tizen.common;

import java.util.List;
import org.tizen.common.core.application.PackageResourceInfo;
import org.tizen.common.launch.ITizenLaunchInfo;

public interface ITizenNativeProject extends ITizenProject {
  List<String> getBuildConfigurations();
  
  String getDefaultBuildConfiguration();
  
  ITizenNativeXMLStore getXmlStore();
  
  ITizenLaunchInfo getLaunchInfo();
  
  List<PackageResourceInfo> optimize(List<PackageResourceInfo> paramList);
}
