package org.tizen.common.rds;

import java.io.File;
import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.util.FileUtil;
import org.tizen.common.util.OSChecker;

public class RdsUtil {
  public static final String SEPARATOR = OSChecker.isWindows() ? "\\" : "/";
  
  public static final String FAST_DEPLOY_DATA_PATH = "fast-deploy";
  
  public static final String OLD_SNAPSHOT_NAME = "old-snapshot";
  
  public static final String SNAPSHOT_NAME = "snapshot";
  
  public static final String LATEST_SNAPSHOT_NAME = "latest-snapshot";
  
  private static final RdsUtil instance = new RdsUtil();
  
  public static RdsUtil getInstance() {
    return instance;
  }
  
  public String getProjectDirPath(IProject project) {
    String workspaceHash = getHashformWorkspace(project);
    return String.valueOf(InstallPathConfig.getIdeUserDataPath()) + SEPARATOR + "fast-deploy" + SEPARATOR + workspaceHash + 
      SEPARATOR + project.getName();
  }
  
  private String getHashformWorkspace(IProject project) {
    File projectLocation = new File(project.getLocation().toString());
    String workspaceName = projectLocation.getParentFile().getName();
    String workspaceLocation = projectLocation.getParent();
    int hashForWorkspace = workspaceLocation.hashCode();
    return String.valueOf(workspaceName) + "_" + Integer.toString(hashForWorkspace);
  }
  
  public String getLatestSnapshotDirPath(IProject project) {
    return String.valueOf(getProjectDirPath(project)) + SEPARATOR + "latest-snapshot";
  }
  
  public String getLatestSnapshotFilePath(IProject project) {
    return String.valueOf(getLatestSnapshotDirPath(project)) + SEPARATOR + ".sdk_delta.info";
  }
  
  public String getOldSnapshotDirPath(IProject project) {
    return String.valueOf(getProjectDirPath(project)) + SEPARATOR + "old-snapshot";
  }
  
  public String getOldSnapshotFilePath(IProject project) {
    return String.valueOf(getOldSnapshotDirPath(project)) + SEPARATOR + ".sdk_delta.info";
  }
  
  public String getSnapshotDirPath(IProject project) {
    return String.valueOf(getProjectDirPath(project)) + SEPARATOR + "snapshot";
  }
  
  public String getSnapshotFilePath(IProject project) {
    return String.valueOf(getSnapshotDirPath(project)) + SEPARATOR + ".sdk_delta.info";
  }
  
  public String getRdsDeltaDirPath(IProject project) {
    return String.valueOf(getProjectDirPath(project)) + SEPARATOR;
  }
  
  public boolean mkdir(String path) {
    File dir = new File(path);
    if (!dir.exists())
      return dir.mkdirs(); 
    return true;
  }
  
  public boolean copyToLatest(IProject project) {
    String snapshotPath = getSnapshotFilePath(project);
    String lastestPath = getLatestSnapshotFilePath(project);
    try {
      FileUtil.copyTo(snapshotPath, lastestPath);
    } catch (IOException iOException) {
      return false;
    } 
    return true;
  }
  
  public boolean removeRDSMeta(IProject project) {
    String path = getProjectDirPath(project);
    return FileUtil.recursiveDelete(path);
  }
}
