package org.tizen.common.launch;

import java.io.File;
import java.util.List;

public interface ITizenLaunchInfo {
  List<File> getPackageFiles();
  
  String getExeFileOnTarget();
}
