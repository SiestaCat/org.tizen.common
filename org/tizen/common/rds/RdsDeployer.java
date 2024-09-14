package org.tizen.common.rds;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.ITizenProject;
import org.tizen.common.TizenProjectType;
import org.tizen.common.fastdeploy.FastDeployer;
import org.tizen.common.launch.LaunchMessages;
import org.tizen.common.sdb.command.FsCommand;
import org.tizen.common.sdb.command.RdsCommand;
import org.tizen.common.sdb.command.ReInstallCommand;
import org.tizen.common.sdb.command.TerminateCommand;
import org.tizen.common.ui.page.properties.PackageConfigUtil;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.common.util.EFSUtil;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.ISdbCommandHelper;
import org.tizen.common.util.OSChecker;
import org.tizen.common.util.ProjectUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.exception.TimeoutException;
import org.tizen.sdblib.service.SyncResult;
import org.tizen.sdblib.service.SyncService;
import org.tizen.sdblib.util.ArrayUtil;
import org.tizen.sdblib.util.IOUtil;

public abstract class RdsDeployer implements Closeable {
  private final Logger logger = LoggerFactory.getLogger(RdsDeployer.class);
  
  private static final String RDS_MARKER = "org.tizen.common.launch.rdsMarker";
  
  private static final String ZIP_EXTENSION = ".zip";
  
  private static final int RDS_OPERATION_COUNT_THRESHOLD = 50;
  
  private static final long RDS_OPERATION_SIZE_THRESHOLD = 31457280L;
  
  private static final long RDS_LARGE_RESOURCE = 2097152L;
  
  private static final String EMPTY_STRING = "";
  
  protected static final String BINARY_EXTENSION = ".exe";
  
  public static final String RDS_SEPARATOR = "/";
  
  protected static final String RDS_EMPTY = ".";
  
  protected static final String FILE_NAME_MANIFEST = "manifest.xml";
  
  protected static final String FILE_NAME_MULTI_MANIFEST = "manifest_multi.xml";
  
  protected static final String FILE_SIGNATURE_FILTER = "signature";
  
  protected IProgressMonitor monitor;
  
  protected ITizenConsoleManager console;
  
  protected String pkgType;
  
  protected String strAppInstallPath;
  
  protected IProject project;
  
  protected IDevice device;
  
  protected RdsDeltaDetector deltaDetector;
  
  protected List<DeltaResourceInfo> ignoreList = new ArrayList<>();
  
  protected List<DeltaResourceInfo> interestList = new ArrayList<>();
  
  private SyncService syncService;
  
  private List<DeltaResourceInfo> deltaInfoList = null;
  
  private String strDeltaInfoFile = "";
  
  private TizenProjectType projectType = TizenProjectType.TIZEN_WEB_APPLICATION;
  
  private static final String WEB_APP_BUILD_RESULT_PATH = "/.build/Result Resource Layer";
  
  protected abstract void printInfo(String paramString);
  
  protected abstract String getAppInstallPath();
  
  protected abstract String getPkgId();
  
  protected abstract String getAppId();
  
  protected abstract boolean sign(List<DeltaResourceInfo> paramList) throws CoreException, IOException, IllegalStateException;
  
  protected abstract void mergeApplicationXML() throws Exception;
  
  @Deprecated
  public RdsDeployer(IProject project, IDevice device, ISdbCommandHelper tizenCommand, ITizenConsoleManager console, String pkgType, IProgressMonitor monitor) {
    this(project, device, console, pkgType, monitor);
  }
  
  public RdsDeployer(IProject project, IDevice device, ITizenConsoleManager console, String pkgType, IProgressMonitor monitor) {
    this.project = project;
    this.device = device;
    this.monitor = monitor;
    this.console = console;
    this.pkgType = pkgType;
    this.strAppInstallPath = getAppInstallPath();
    this.deltaDetector = new RdsDeltaDetector(device, project, this.strAppInstallPath);
  }
  
  protected boolean preDeploy() throws CoreException {
    this.project.deleteMarkers("org.tizen.common.launch.rdsMarker", true, 2);
    if (PackageConfigUtil.isBlackListDirty(this.project))
      return false; 
    if (!hasOldResourceInfo()) {
      this.logger.debug(makeRdsLog(RdsMessages.CANNOT_FIND_RDS_INFO));
      return false;
    } 
    return true;
  }
  
  protected void configureRM(List<DeltaResourceInfo> deltaInfoList) {}
  
  public boolean deploy() throws CoreException {
    if (!preDeploy())
      return false; 
    List<DeltaResourceInfo> deltaInfoList = getInterestDelta();
    if (deltaInfoList == null)
      return false; 
    if (deltaInfoList.isEmpty())
      return true; 
    configureRM(deltaInfoList);
    try {
      if (!sign(deltaInfoList)) {
        printInfo("Failed to sign for RDS");
        return false;
      } 
    } catch (Exception e) {
      removeMergedManifest();
      printInfo("Failed to sign for RDS");
      this.logger.error("Failed to sign for RDS", e);
      return false;
    } 
    cleanDeltaInfo();
    deltaInfoList = getInterestDelta();
    if (deltaInfoList == null)
      return false; 
    if (deltaInfoList.isEmpty())
      return true; 
    try {
      RdsResourceThreshold rdsOperation = checkDeltaResources(deltaInfoList);
      if (rdsOperation.getTotalCount() > 50 || 
        rdsOperation.getTotalSize() > 31457280L)
        return false; 
      DeltaResourceInfo rdsDeltaInfo = makeRdsDeltaListFile(deltaInfoList);
    } catch (CoreException e) {
      this.logger.error(RdsMessages.CANNOT_PARTIALLY_INSTALL, (Throwable)e);
      printInfo(makeRdsLog(RdsMessages.CANNOT_PARTIALLY_INSTALL));
      reportWarning((IResource)this.project, e.getMessage(), false);
      return false;
    } catch (IOException iOException) {
      return false;
    } finally {
      removeZipFile();
      removeMergedManifest();
      this.project.refreshLocal(2, (IProgressMonitor)new NullProgressMonitor());
    } 
    removeZipFile();
    removeMergedManifest();
    this.project.refreshLocal(2, (IProgressMonitor)new NullProgressMonitor());
    return true;
  }
  
  protected void reportWarning(IResource resource, String msg, boolean persistent) {
    try {
      IMarker m = resource.createMarker("org.tizen.common.launch.rdsMarker");
      m.setAttribute("message", msg);
      m.setAttribute("priority", 2);
      m.setAttribute("severity", 1);
      m.setAttribute("transient", !persistent);
    } catch (CoreException e) {
      this.logger.error(e.getMessage(), (Throwable)e);
    } 
  }
  
  protected List<DeltaResourceInfo> getInterestDelta() {
    List<DeltaResourceInfo> deltaInfoList = null;
    try {
      this.project.refreshLocal(2, (IProgressMonitor)new NullProgressMonitor());
      deltaInfoList = getDelta();
      if (deltaInfoList == null)
        newCoreException(RdsMessages.CANNOT_FIND_DELTA, null); 
      deltaInfoList = getInterestDelta(deltaInfoList);
    } catch (CoreException coreException) {
      printInfo(makeRdsLog(RdsMessages.CANNOT_FIND_DELTA));
      return null;
    } 
    return deltaInfoList;
  }
  
  public void setInterestList(List<DeltaResourceInfo> list) {
    this.interestList = list;
  }
  
  public void appendInterestList(DeltaResourceInfo delta) {
    this.interestList.add(delta);
  }
  
  public void appendInterestList(List<DeltaResourceInfo> deltaList) {
    if (this.interestList != null) {
      int size = this.interestList.size();
      this.interestList.addAll(size, deltaList);
    } 
  }
  
  public void setIgnoreList(List<DeltaResourceInfo> list) {
    this.ignoreList = list;
  }
  
  public void appendIgnoreList(List<DeltaResourceInfo> deltaList) {
    if (this.ignoreList != null) {
      int size = this.ignoreList.size();
      this.ignoreList.addAll(size, deltaList);
    } 
  }
  
  protected String getRemotePathOfDelta(DeltaResourceInfo interest, String strProjectPath, DeltaResourceInfo delta) throws CoreException {
    String strRemotePath = "";
    String prefix = "";
    if (interest == null || interest.getRemotePath() == null) {
      prefix = strProjectPath;
      strRemotePath = DeltaResourceInfo.convertToRemotePath(delta.getFullPath(), prefix, "");
    } else {
      File file = new File(delta.getFullPath());
      String interestRemotePath = "";
      String interestFullPath = "";
      if (isDirectory(interest.getFullPath())) {
        if (file.exists()) {
          interestFullPath = interest.getFullPath();
          interestRemotePath = interest.getRemotePath();
        } else {
          interestFullPath = interest.getFullPath().substring(0, interest.getFullPath().length() - 1);
          interestRemotePath = interest.getRemotePath().substring(0, interest.getRemotePath().length() - 1);
        } 
      } else {
        interestFullPath = interest.getFullPath();
        interestRemotePath = interest.getRemotePath();
      } 
      String rootProjectName = this.project.getName();
      String currentProjectName = delta.getProjectName();
      String deltaFullPath = delta.getFullPath();
      if (!rootProjectName.equals(currentProjectName) && !deltaFullPath.contains(".exe")) {
        strProjectPath = strProjectPath.replace(currentProjectName, rootProjectName);
        deltaFullPath = deltaFullPath.replace(currentProjectName, rootProjectName);
      } 
      prefix = String.valueOf(strProjectPath) + interestFullPath;
      if (deltaFullPath.contains("manifest_multi.xml"))
        deltaFullPath = deltaFullPath.replace("manifest_multi.xml", "manifest.xml"); 
      strRemotePath = DeltaResourceInfo.convertToRemotePath(deltaFullPath, prefix, interestRemotePath);
    } 
    if (strRemotePath == null) {
      this.logger.error(String.format("Cannot make remotePath (host: %s, prefix: %s)", new Object[] { delta.getFullPath(), prefix }));
      newCoreException(makeRdsLog(RdsMessages.CANNOT_FIND_DELTA), null);
    } 
    if (2 == delta.getResourceType() && !"delete".equals(delta.getType()) && 
      !strRemotePath.endsWith("/"))
      strRemotePath = String.valueOf(strRemotePath) + "/"; 
    return strRemotePath.replaceFirst("/", "");
  }
  
  protected DeltaResourceInfo containsNode(DeltaResourceInfo node, List<DeltaResourceInfo> nodeList, String prefix) {
    File file = new File(node.getFullPath());
    for (DeltaResourceInfo tempNode : nodeList) {
      if (isDirectory(tempNode.getFullPath())) {
        if (file.exists()) {
          if (node.getFullPath().startsWith(String.valueOf(prefix) + tempNode.getFullPath()))
            return tempNode; 
          continue;
        } 
        if ((String.valueOf(node.getFullPath()) + "/").startsWith(String.valueOf(prefix) + tempNode.getFullPath()))
          return tempNode; 
        continue;
      } 
      String fullPath = node.getFullPath();
      if (tempNode.getFullPath().contains("*")) {
        CharSequence tempNodePathStr = String.valueOf(prefix) + tempNode.getFullPath();
        CharSequence nodePathStr = fullPath;
        int length = nodePathStr.length();
        int index = 0;
        int i = 0;
        for (; i < length; i++) {
          char nodeChar = nodePathStr.charAt(i);
          if (tempNodePathStr.length() < i + index + 1)
            break; 
          char tempNodeChar = tempNodePathStr.charAt(i + index);
          if (tempNodeChar != nodeChar)
            if (tempNodeChar == '*') {
              if (tempNodePathStr.length() == i + index + 1) {
                index--;
              } else if (nodeChar != tempNodePathStr.charAt(i + index + 1)) {
                index--;
              } else if (nodeChar == tempNodePathStr.charAt(i + index + 1)) {
                index++;
              } 
            } else {
              break;
            }  
        } 
        if (i == length && tempNodePathStr.length() == i + index)
          return tempNode; 
        if (i == length && tempNodePathStr.length() == i + index + 1 && 
          tempNodePathStr.charAt(i + index) == '*')
          return tempNode; 
        continue;
      } 
      if (fullPath.equals(String.valueOf(prefix) + tempNode.getFullPath()))
        return tempNode; 
    } 
    return null;
  }
  
  protected boolean isDirectory(String path) {
    return path.endsWith("/");
  }
  
  private void partialZipInstall(List<DeltaResourceInfo> rearrangedDeltaInfoList) throws CoreException {
    DeltaResourceInfo deltaResInfo = rearrangedDeltaInfoList.get(0);
    RdsCommand command = new RdsCommand(this.device, this.console);
    boolean isSuccess = false;
    try {
      String strRemoteAbsolutePath = String.valueOf(this.strAppInstallPath) + "/" + deltaResInfo.getRemotePath();
      isSuccess = command.rdsInstall(getPkgId(), this.pkgType, deltaResInfo.getFullPath(), strRemoteAbsolutePath);
    } catch (Exception e) {
      newCoreException(makeRdsLog("rds command error"), e);
    } 
    if (!isSuccess)
      newCoreException("RDS install failed: " + deltaResInfo.getFullPath(), null); 
  }
  
  public List<DeltaResourceInfo> getInterestDelta(List<DeltaResourceInfo> deltaInfoList) throws CoreException {
    List<DeltaResourceInfo> delta = new ArrayList<>();
    DeltaResourceInfo interestNode = null;
    String strRemotePath = "";
    for (DeltaResourceInfo node : deltaInfoList) {
      String strProjectPath = this.project.getLocation().toString();
      String nodeProjectName = node.getProjectName();
      if (nodeProjectName != null && 
        !this.project.getName().equals(nodeProjectName))
        strProjectPath = strProjectPath.replace(this.project.getName(), nodeProjectName); 
      interestNode = null;
      if (containsNode(node, this.ignoreList, strProjectPath) != null)
        continue; 
      interestNode = containsNode(node, this.interestList, strProjectPath);
      if (node.getType() != "delete" && interestNode == null)
        continue; 
      strRemotePath = getRemotePathOfDelta(interestNode, strProjectPath, node);
      node.setRemotePath(strRemotePath);
      delta.add(node);
      getInterestDelta(node.getChildren());
    } 
    return delta;
  }
  
  public void cleanDeltaInfo() {
    this.deltaInfoList = null;
    this.strDeltaInfoFile = null;
  }
  
  public List<DeltaResourceInfo> getDelta() {
    if (this.deltaInfoList == null) {
      this.deltaInfoList = this.deltaDetector.getDelta();
      this.strDeltaInfoFile = this.deltaDetector.makeDeltaFile();
    } 
    return this.deltaInfoList;
  }
  
  public String getDeltaInfoFile() {
    getDelta();
    return this.strDeltaInfoFile;
  }
  
  public void reInstall() throws CoreException {
    try {
      TerminateCommand termCommand = new TerminateCommand(this.device, getPkgId(), this.pkgType, this.console);
      termCommand.execute();
    } catch (Exception e) {
      newCoreException("Terminate command failed: " + getAppId(), e);
    } 
    int timeout = 20000;
    try {
      ReInstallCommand command = new ReInstallCommand(this.device, this.pkgType.toLowerCase(), getPkgId(), timeout, this.console);
      command.execute();
      FsCommand fsCommand = new FsCommand(this.device, this.console);
      fsCommand.removeFile(this.strAppInstallPath);
    } catch (TimeoutException e) {
      newCoreException(makeRdsLog(NLS.bind(LaunchMessages.SDB_TIMEOUT_EXCEPTION, Integer.valueOf(timeout))), (Throwable)e);
    } catch (Exception e) {
      newCoreException(makeRdsLog(RdsMessages.CANNOT_INSTALL), e);
    } 
  }
  
  public boolean hasOldResourceInfo() {
    boolean readOldTree = this.deltaDetector.readOldTree();
    if (!readOldTree)
      return false; 
    return true;
  }
  
  public void pushResInfoFile() {
    String appInstallPath = getAppInstallPath();
    this.strDeltaInfoFile = this.deltaDetector.makeDeltaFile();
    String strRemotePath = String.valueOf(appInstallPath) + "/info/";
    try {
      pushFile(this.strDeltaInfoFile, strRemotePath);
    } catch (Exception e) {
      this.logger.error(makeRdsLog(RdsMessages.RDS_RES_INFO_PUSH_ERROR), e);
    } 
    RdsUtil.getInstance().copyToLatest(this.project);
  }
  
  public void removeResInfoFile() {
    String appInstallPath = getAppInstallPath();
    String strRemotePath = String.valueOf(appInstallPath) + "/info/" + ".sdk_delta.info";
    try {
      FsCommand command = new FsCommand(this.device, null);
      command.removeFile(strRemotePath);
    } catch (Exception e) {
      this.logger.error(makeRdsLog(String.valueOf(RdsMessages.RDS_DELETE_ERROR) + " - " + strRemotePath), e);
    } 
    RdsUtil.getInstance().removeRDSMeta(this.project);
    FastDeployer.reinstallComplete(this.project);
  }
  
  public static String makeRdsLog(String log) {
    return String.valueOf(RdsMessages.RDS_MODE_PREFIX) + " " + log;
  }
  
  private void pushFile(String source, String dest) throws Exception {
    SyncResult result = getSyncService().push(source, this.device.getFileEntry(dest));
    if (!result.isOk())
      throw new Exception(result.getMessage()); 
  }
  
  protected void newCoreException(String message, Throwable exception) throws CoreException {
    Status status = new Status(4, "org.tizen.common", message, exception);
    throw new CoreException(status);
  }
  
  protected SyncService getSyncService() throws Exception {
    if (this.syncService == null)
      this.syncService = this.device.getSyncService(); 
    return this.syncService;
  }
  
  public void close() throws IOException {
    if (this.syncService != null)
      this.syncService.close(); 
  }
  
  private RdsResourceThreshold checkDeltaResources(List<DeltaResourceInfo> deltaInfoList) {
    RdsResourceThreshold rdsOperation = new RdsResourceThreshold();
    int removeResourceCount = 0;
    long totalSize = 0L;
    long sumOfLargeResourceSize = 0L;
    for (DeltaResourceInfo deltaInfo : deltaInfoList) {
      String resourcePath = deltaInfo.getFullPath();
      String type = deltaInfo.getType();
      File currentResource = new File(resourcePath);
      long currentResourceSize = currentResource.length();
      if ("modify".equals(type) || "add".equals(type)) {
        if (currentResourceSize > 2097152L) {
          rdsOperation.getLargeResources().add(deltaInfo);
          sumOfLargeResourceSize += currentResourceSize;
        } 
        if (currentResource.isFile())
          totalSize += currentResourceSize; 
        continue;
      } 
      removeResourceCount++;
    } 
    int totalCount = deltaInfoList.size() - removeResourceCount;
    rdsOperation.setTotalCount(totalCount);
    rdsOperation.setTotalSize(totalSize);
    rdsOperation.setSumOfLargeResourceSize(sumOfLargeResourceSize);
    int largeResourceCount = rdsOperation.getLargeResources().size();
    rdsOperation.setLargeResourceCount(largeResourceCount);
    return rdsOperation;
  }
  
  private void removeZipFile() {
    ITizenProject tizenProject = ProjectUtil.getTizenProject(this.project);
    final String appId = tizenProject.getAppId();
    String zipFilePath = this.project.getLocation().toString();
    File zipFile = new File(zipFilePath);
    FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          boolean isMatched = (name.toLowerCase().contains(appId.toLowerCase()) && 
            name.toLowerCase().contains(".z"));
          return isMatched;
        }
      };
    File[] listFiles = zipFile.listFiles(filter);
    if (!ArrayUtil.isEmpty((Object[])listFiles)) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = listFiles).length, b = 0; b < i; ) {
        File deleteFile = arrayOfFile[b];
        deleteFile.delete();
        b++;
      } 
    } 
  }
  
  private void removeMergedManifest() {
    String mergedManifestPath = null;
    if (this.projectType != TizenProjectType.TIZEN_WEB_APPLICATION && 
      this.projectType != TizenProjectType.TIZEN_WEB_UIFW_APPLICATION && 
      this.projectType != TizenProjectType.TIZEN_WEB_UIBUILDER_APPLICATION) {
      mergedManifestPath = String.valueOf(getRootPrjPath()) + "/" + "manifest_multi.xml";
    } else {
      mergedManifestPath = String.valueOf(getRootPrjPath()) + "/" + "manifest.xml";
    } 
    File mergedManifestFile = new File(mergedManifestPath);
    if (mergedManifestFile.exists())
      mergedManifestFile.delete(); 
  }
  
  private List<DeltaResourceInfo> makeZipAndRearrangeDeltaResources(List<DeltaResourceInfo> deltaInfoList) throws IOException {
    List<DeltaResourceInfo> rearrangedDeltaInfoList = new ArrayList<>();
    ITizenProject tizenProject = ProjectUtil.getTizenProject(this.project);
    String appId = tizenProject.getAppId();
    String zipFilePath = String.valueOf(this.project.getLocation().toString()) + File.separator + appId + ".zip";
    File zipFile = new File(zipFilePath);
    byte[] buf = new byte[4096];
    FileOutputStream fos = null;
    ZipArchiveOutputStream zaos = null;
    FileInputStream in = null;
    try {
      fos = new FileOutputStream(zipFile);
      zaos = new ZipArchiveOutputStream(fos);
      boolean isExistEmptyEntry = false;
      for (DeltaResourceInfo deltaInfo : deltaInfoList) {
        String currentResourcePath = deltaInfo.getFullPath();
        File currentResource = new File(currentResourcePath);
        if (currentResource.isDirectory()) {
          zaos.putArchiveEntry((ArchiveEntry)new ZipArchiveEntry(deltaInfo.getRemotePath()));
          continue;
        } 
        if ("delete".equals(deltaInfo.getType())) {
          if (!isExistEmptyEntry) {
            zaos.putArchiveEntry((ArchiveEntry)new ZipArchiveEntry("."));
            isExistEmptyEntry = true;
          } 
          continue;
        } 
        String remotePath = deltaInfo.getRemotePath();
        if (currentResourcePath.contains("manifest_multi.xml"))
          remotePath = remotePath.replace("manifest_multi.xml", "manifest.xml"); 
        try {
          IResource deltaRes = deltaInfo.getResource();
          if (deltaRes != null && deltaRes.isLinked())
            currentResource = deltaRes.getLocation().toFile(); 
          in = new FileInputStream(currentResource);
          ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(deltaInfo.getRemotePath());
          if (OSChecker.isLinux())
            zipArchiveEntry.setUnixMode(EFSUtil.getPermissions(currentResource.getAbsolutePath())); 
          zaos.putArchiveEntry((ArchiveEntry)zipArchiveEntry);
          int len;
          while ((len = in.read(buf)) > 0)
            zaos.write(buf, 0, len); 
        } finally {
          IOUtil.tryClose(in);
        } 
      } 
    } finally {
      if (zaos != null) {
        zaos.closeArchiveEntry();
        IOUtil.tryClose(new Object[] { zaos });
      } 
    } 
    DeltaResourceInfo zipDeltaInfo = new DeltaResourceInfo(appId, zipFilePath, zipFile.getName(), 
        "add", 1);
    rearrangedDeltaInfoList.add(zipDeltaInfo);
    return rearrangedDeltaInfoList;
  }
  
  private DeltaResourceInfo makeRdsDeltaListFile(List<DeltaResourceInfo> deltaInfoList) throws CoreException {
    RdsDeltaListFile deltaListFile = new RdsDeltaListFile();
    String rootProjectPath = this.project.getLocation().toString();
    String comareProjectPath = null;
    for (DeltaResourceInfo node : deltaInfoList) {
      String projectName = node.getProjectName();
      if (projectName != null) {
        String[] split = rootProjectPath.split("/");
        int length = split.length;
        String rootProjectName = split[length - 1];
        if (!rootProjectName.equals(projectName)) {
          comareProjectPath = rootProjectPath.replace(rootProjectName, projectName);
        } else {
          comareProjectPath = rootProjectPath;
        } 
      } 
      if (containsNode(node, this.ignoreList, comareProjectPath) != null)
        continue; 
      DeltaResourceInfo interestNode = containsNode(node, this.interestList, comareProjectPath);
      if (node.getType() != "delete" && interestNode == null)
        continue; 
      String strRemotePath = getRemotePathOfDelta(interestNode, comareProjectPath, node);
      String type = node.getType();
      if ("modify".equals(type)) {
        deltaListFile.addModifyDelta(strRemotePath);
        continue;
      } 
      if ("add".equals(type)) {
        deltaListFile.addAddDelta(strRemotePath);
        continue;
      } 
      if ("delete".equals(type))
        deltaListFile.addDeleteDelta(strRemotePath); 
    } 
    String rdsDeltaDir = RdsUtil.getInstance().getRdsDeltaDirPath(this.project);
    File file = deltaListFile.makeFile(rdsDeltaDir);
    if (file == null) {
      newCoreException(makeRdsLog(RdsMessages.CANNOT_FIND_RDS_INFO), null);
    } else if (!file.exists()) {
      try {
        boolean created = file.createNewFile();
        if (!created)
          newCoreException(makeRdsLog(RdsMessages.CANNOT_FIND_RDS_INFO), null); 
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    DeltaResourceInfo rdsDeltaInfo = new DeltaResourceInfo(file.getName(), file.getAbsolutePath(), file.getName(), 
        "add", 1);
    return rdsDeltaInfo;
  }
  
  protected boolean isChangedManifest(List<DeltaResourceInfo> deltaList) {
    boolean isChanged = false;
    for (DeltaResourceInfo delta : deltaList) {
      String fullPath = delta.getFullPath();
      if (fullPath != null && fullPath.contains("manifest.xml")) {
        isChanged = true;
        break;
      } 
    } 
    return isChanged;
  }
  
  protected String getRootPrjPath() {
    return this.project.getLocation().toString();
  }
  
  protected String[] getRefPrjPaths() {
    List<IProject> referencedProjects = ProjectUtil.getReferencedProjects(this.project);
    int referencedProjectCount = referencedProjects.size();
    if (referencedProjects != null && referencedProjectCount > 0) {
      String[] refPrjPaths = new String[referencedProjectCount];
      for (int i = 0; i < referencedProjectCount; i++) {
        IProject project = referencedProjects.get(i);
        refPrjPaths[i] = project.getLocation().toFile().getAbsolutePath();
      } 
      return refPrjPaths;
    } 
    return new String[0];
  }
  
  public boolean checkValidDelta(DeltaResourceInfo delta) {
    boolean isValidDelta = false;
    String comparePathOfDelta = getComparePath(delta.getFullPath(), delta.getProjectName());
    List<IProject> projects = this.deltaDetector.getProjects();
    for (int i = 0; i < projects.size(); i++) {
      IProject findDeltaProject = projects.get(i);
      if (!findDeltaProject.equals(this.project)) {
        List<DeltaResourceInfo> deltaListOfProject = this.deltaDetector.getDelta(findDeltaProject);
        for (int j = 0; j < deltaListOfProject.size(); j++) {
          DeltaResourceInfo compareDelta = deltaListOfProject.get(j);
          String comparePath = getComparePath(compareDelta.getFullPath(), compareDelta.getProjectName());
          if (comparePathOfDelta.equals(comparePath)) {
            isValidDelta = true;
            return isValidDelta;
          } 
        } 
      } 
    } 
    return isValidDelta;
  }
  
  public String getComparePath(String fullPath, String projectName) {
    String comparePath = null;
    if (fullPath.contains("/.build/Result Resource Layer"))
      fullPath = fullPath.replace("/.build/Result Resource Layer", ""); 
    int indexOf = fullPath.indexOf(projectName);
    comparePath = fullPath.substring(indexOf + projectName.length());
    if (comparePath.contains(".exe")) {
      String[] split = comparePath.split("/");
      if (split != null && split.length > 0)
        comparePath = split[split.length - 1]; 
    } 
    return comparePath;
  }
  
  public boolean isSignatureFile(DeltaResourceInfo delta) {
    boolean isSignature = false;
    if (delta.getFullPath().contains("signature")) {
      File signatureFile = new File(delta.getFullPath());
      if (signatureFile.exists() && signatureFile.isFile())
        isSignature = true; 
    } 
    return isSignature;
  }
}
