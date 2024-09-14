package org.tizen.common.rds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.internal.events.ResourceDeltaFactory;
import org.eclipse.core.internal.localstore.SafeFileInputStream;
import org.eclipse.core.internal.localstore.SafeFileOutputStream;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.internal.watson.ElementTree;
import org.eclipse.core.internal.watson.ElementTreeReader;
import org.eclipse.core.internal.watson.ElementTreeWriter;
import org.eclipse.core.internal.watson.IElementInfoFlattener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.ProjectUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.service.SyncResult;
import org.tizen.sdblib.service.SyncService;

public class RdsDeltaDetector {
  private static final Logger logger = LoggerFactory.getLogger(RdsDeltaDetector.class);
  
  ElementTree[] oldTrees;
  
  ElementTree latestTree;
  
  IDevice device;
  
  IProject project;
  
  String remotePath;
  
  String strResInfoFile = "";
  
  public static final String STR_TREE_FILE = ".sdk_delta.info";
  
  public static final String STR_TREE_DIRECTORY = "/info/";
  
  private static final String EMPTY_STRING = "";
  
  private static final String RDS_SEPARATOR = "/";
  
  private static final String TEMP_RESOURCE = ".temp";
  
  private static final int TREE_BUFFER_SIZE = 65536;
  
  List<String> deltaFilterDataList;
  
  private List<IProject> projects = new ArrayList<>();
  
  private Map<String, ElementTree> projectTreeMap = new HashMap<>();
  
  public RdsDeltaDetector(IDevice device, IProject project, String remotePath) {
    this.device = device;
    this.project = project;
    this.remotePath = remotePath;
    List<IProject> referencedProjects = ProjectUtil.getReferencedProjects(project);
    int size = referencedProjects.size();
    this.projects.add(project);
    if (size > 0) {
      this.projects.addAll(1, referencedProjects);
      this.oldTrees = new ElementTree[size + 1];
    } else {
      this.oldTrees = new ElementTree[1];
      this.latestTree = new ElementTree();
    } 
  }
  
  public boolean readOldTree() {
    boolean isSucceed = false;
    String strLocalFile = downloadOldTree();
    if (strLocalFile == null)
      return isSucceed; 
    Workspace workspace = (Workspace)this.project.getWorkspace();
    ElementTreeReader treeReader = new ElementTreeReader((IElementInfoFlattener)workspace.getSaveManager());
    DataInputStream input = null;
    try {
      input = new DataInputStream((InputStream)new SafeFileInputStream(strLocalFile, String.valueOf(strLocalFile) + ".temp", 65536));
      this.projectTreeMap.clear();
      for (int i = 0; i < this.projects.size(); i++) {
        ElementTree readTree = treeReader.readTree(input);
        if (readTree != null) {
          this.oldTrees[i] = readTree;
          this.projectTreeMap.put(((IProject)this.projects.get(i)).getName(), readTree);
        } else {
          return isSucceed;
        } 
      } 
      isSucceed = true;
    } catch (IOException e) {
      logger.error("Failed to read", e);
    } finally {
      IOUtil.tryClose(new Object[] { input });
    } 
    return isSucceed;
  }
  
  public boolean readLatestTree() {
    String latestTreePath = RdsUtil.getInstance().getLatestSnapshotFilePath(this.project);
    File latestFile = new File(latestTreePath);
    if (!latestFile.exists())
      return false; 
    Workspace workspace = (Workspace)this.project.getWorkspace();
    ElementTreeReader treeReader = new ElementTreeReader((IElementInfoFlattener)workspace.getSaveManager());
    DataInputStream input = null;
    try {
      input = new DataInputStream((InputStream)new SafeFileInputStream(latestTreePath, String.valueOf(latestTreePath) + ".temp", 65536));
      ElementTree readTree = treeReader.readTree(input);
      if (readTree != null) {
        this.latestTree = readTree;
        return true;
      } 
    } catch (IOException e) {
      logger.error("Failed to read latest snapshot", e);
    } finally {
      IOUtil.tryClose(new Object[] { input });
    } 
    return false;
  }
  
  public boolean hasOldTree() {
    if (this.oldTrees == null || this.oldTrees[0] == null)
      return false; 
    return true;
  }
  
  private String downloadOldTree() {
    if (this.remotePath == null)
      return null; 
    String deltaFileOnRemote = String.valueOf(this.remotePath) + "/info/" + ".sdk_delta.info";
    String strLocalFile = RdsUtil.getInstance().getOldSnapshotDirPath(this.project);
    if (!RdsUtil.getInstance().mkdir(strLocalFile))
      return null; 
    SyncService service = null;
    try {
      service = this.device.getSyncService();
      SyncResult result = service.pull(this.device.getFileEntry(deltaFileOnRemote), strLocalFile);
      if (!result.isOk()) {
        logger.error("old snapshot pull failed: " + result.getCode() + " " + result.getMessage());
        return null;
      } 
    } catch (IOException e) {
      logger.error("old snapshot pull - get SyncService failed: " + this.device.getSerialNumber(), e);
      return null;
    } finally {
      IOUtil.tryClose(new Object[] { service });
    } 
    return 
      String.valueOf(strLocalFile) + "/" + ".sdk_delta.info";
  }
  
  public List<DeltaResourceInfo> getDelta() {
    String projectPath = "";
    if (this.oldTrees == null || this.oldTrees[0] == null)
      return null; 
    List<DeltaResourceInfo> deltaInfoList = new ArrayList<>();
    for (int i = 0; i < this.oldTrees.length; i++) {
      IProject currentProject = this.projects.get(i);
      String projectName = currentProject.getName();
      IPath subTreePath = currentProject.getFullPath();
      projectPath = currentProject.getLocation().toString();
      ElementTree tree = ((Workspace)currentProject.getWorkspace()).getElementTree().getSubtree(subTreePath);
      ResourceDelta resourceDelta = ResourceDeltaFactory.computeDelta((Workspace)currentProject.getWorkspace(), this.oldTrees[i], tree, currentProject.getFullPath(), -1L);
      getDelta(deltaInfoList, (IResourceDelta)resourceDelta, projectPath, projectName);
    } 
    return deltaInfoList;
  }
  
  public List<DeltaResourceInfo> getLatestDelta() {
    String projectPath = "";
    if (this.latestTree == null)
      return null; 
    List<DeltaResourceInfo> deltaInfoList = new ArrayList<>();
    String projectName = this.project.getName();
    IPath subTreePath = this.project.getFullPath();
    projectPath = this.project.getLocation().toString();
    ElementTree tree = ((Workspace)this.project.getWorkspace()).getElementTree().getSubtree(subTreePath);
    ResourceDelta resourceDelta = ResourceDeltaFactory.computeDelta((Workspace)this.project.getWorkspace(), this.latestTree, tree, this.project.getFullPath(), -1L);
    getDelta(deltaInfoList, (IResourceDelta)resourceDelta, projectPath, projectName);
    return deltaInfoList;
  }
  
  public List<DeltaResourceInfo> getDelta(IProject project) {
    String projectPath = project.getLocation().toString();
    IPath subTreePath = project.getFullPath();
    ElementTree tree = ((Workspace)project.getWorkspace()).getElementTree().getSubtree(subTreePath);
    ElementTree oldTree = this.projectTreeMap.get(project.getName());
    ResourceDelta resourceDelta = ResourceDeltaFactory.computeDelta((Workspace)project.getWorkspace(), oldTree, tree, project.getFullPath(), -1L);
    return getDelta(new ArrayList<>(), (IResourceDelta)resourceDelta, projectPath, project.getName());
  }
  
  private DeltaResourceInfo addDeltaInfo(List<DeltaResourceInfo> deltaInfoList, String nodeName, String deltaFullPath, String deltaType, int resourceType, String projectName, IResource res) {
    DeltaResourceInfo resourceInfo = new DeltaResourceInfo(nodeName, deltaFullPath, null, deltaType, resourceType, projectName, res);
    deltaInfoList.add(resourceInfo);
    return resourceInfo;
  }
  
  public String makeDeltaFile() {
    Workspace workspace = (Workspace)this.project.getWorkspace();
    ElementTreeWriter treeWriter = new ElementTreeWriter((IElementInfoFlattener)workspace.getSaveManager());
    String strLocalDir = RdsUtil.getInstance().getSnapshotDirPath(this.project);
    RdsUtil.getInstance().mkdir(strLocalDir);
    String strLocalFile = RdsUtil.getInstance().getSnapshotFilePath(this.project);
    DataOutputStream output = null;
    try {
      output = new DataOutputStream((OutputStream)new SafeFileOutputStream(strLocalFile, String.valueOf(strLocalFile) + ".temp"));
      for (IProject currentProject : this.projects) {
        ElementTree currentTree = workspace.getElementTree().getSubtree(currentProject.getFullPath());
        IPath currentSubTreePath = currentProject.getFullPath();
        treeWriter.writeTree(currentTree, currentSubTreePath, -1, output);
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtil.tryClose(new Object[] { output });
      try {
        this.project.refreshLocal(1, (IProgressMonitor)new NullProgressMonitor());
      } catch (CoreException coreException) {}
    } 
    return strLocalFile;
  }
  
  public List<DeltaResourceInfo> getDelta(List<DeltaResourceInfo> deltaInfoList, IResourceDelta deltaNode, String strPath, String projectName) {
    String resourceName = "";
    int resourceType = 0;
    DeltaResourceInfo resourceInfo = null;
    IResource resource = null;
    String resourcePath = "";
    byte b;
    int i;
    IResourceDelta[] arrayOfIResourceDelta;
    for (i = (arrayOfIResourceDelta = deltaNode.getAffectedChildren()).length, b = 0; b < i; ) {
      IResourceDelta resourceDelta = arrayOfIResourceDelta[b];
      resource = resourceDelta.getResource();
      resourceName = resource.getName();
      resourceType = resource.getType();
      resourcePath = String.valueOf(strPath) + "/" + resourceName;
      switch (resourceDelta.getKind()) {
        case 1:
        case 8:
          addDeltaInfo(deltaInfoList, resourceName, resourcePath, "add", resourceType, projectName, resource);
          getDelta(deltaInfoList, resourceDelta, resourcePath, projectName);
          break;
        case 4:
          if (2 == resourceType) {
            getDelta(deltaInfoList, resourceDelta, resourcePath, projectName);
            break;
          } 
          if (1 == resourceType)
            addDeltaInfo(deltaInfoList, resourceName, resourcePath, "modify", resourceType, projectName, resource); 
          break;
        case 2:
        case 16:
          resourceInfo = addDeltaInfo(deltaInfoList, resourceName, resourcePath, "delete", resourceType, projectName, resource);
          if (2 == resourceType)
            getDelta(resourceInfo.getChildren(), resourceDelta, resourcePath, projectName); 
          break;
      } 
      b++;
    } 
    return deltaInfoList;
  }
  
  public List<IProject> getProjects() {
    return this.projects;
  }
  
  public Map<String, ElementTree> getProjectTreeMap() {
    return this.projectTreeMap;
  }
}
