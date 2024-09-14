package org.tizen.common.core.application.tproject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.RemoteLogger;
import org.tizen.common.core.application.PackageResourceInfo;
import org.tizen.common.core.application.TizenProjectDescription;
import org.tizen.common.util.DialogUtil;
import org.tizen.common.util.IOUtil;
import org.tizen.sdblib.util.StringUtil;

public class TprojectHandler {
  private static final Logger logger = LoggerFactory.getLogger(TprojectHandler.class);
  
  private String FILE_NAME = ".tproject";
  
  private IProject project;
  
  private static final String FILE = "file";
  
  private static final String DIR = "dir";
  
  private static final String ALL = "all";
  
  private static HashMap<String, Boolean> tpkLoaded = new HashMap<>();
  
  static ObjectFactory objFactory = new ObjectFactory();
  
  static JAXBContext jaxbContext = null;
  
  static {
    try {
      jaxbContext = JAXBContext.newInstance(ObjectFactory.class
          .getPackage().getName());
    } catch (JAXBException e) {
      String message = "cannot create JAXBContext instance (" + 
        TprojectHandler.class.getName() + ")";
      logger.error(message, (Throwable)e);
    } 
  }
  
  public TprojectHandler(IProject project) {
    this.project = project;
  }
  
  public boolean marshal(TizenProjectDescription desc) {
    if (this.project == null)
      return false; 
    Tproject tProject = makeModel(desc);
    File file = this.project.getFile(this.FILE_NAME).getLocation().toFile();
    try {
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", 
          Boolean.TRUE);
      marshaller.marshal(tProject, file);
    } catch (JAXBException e) {
      logger.error("cannot marshal in " + this.project.getName(), (Throwable)e);
      DialogUtil.openErrorDialog(String.valueOf(Messages.TPROJECTHANDLER_ERR) + (
          (e.getMessage() != null) ? e.getMessage() : (
          (e.getLinkedException().getMessage() != null) ? e.getLinkedException().getMessage() : "")));
      return false;
    } 
    try {
      this.project.refreshLocal(2, null);
    } catch (CoreException e) {
      logger.warn("cannot excute resfreshLocal method of " + this.project, (Throwable)e);
    } 
    return true;
  }
  
  public static Tproject unmarshalTproject(File file) {
    try {
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      Object obj = unmarshaller.unmarshal(file);
      if (obj instanceof Tproject)
        return (Tproject)obj; 
    } catch (JAXBException jAXBException) {
      logger.error("Couldn't unmarshal file: {}", file.getAbsolutePath());
    } 
    return null;
  }
  
  public static String getMd5(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(input.getBytes());
      BigInteger num = new BigInteger(1, messageDigest);
      String hashtext = num.toString(16);
      while (hashtext.length() < 32)
        hashtext = "0" + hashtext; 
      return hashtext;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return "";
    } 
  }
  
  public static String getFirstPlatformNameFromFile(File file) {
    Tproject tproject = unmarshalTproject(file);
    if (tproject != null) {
      Platforms platforms = tproject.getPlatforms();
      if (platforms != null) {
        List<Platform> platformList = platforms.getPlatform();
        if (platformList != null) {
          Iterator<Platform> iterator = platformList.iterator();
          if (iterator.hasNext()) {
            Platform platform = iterator.next();
            String name = platform.getName();
            if (!tpkLoaded.containsKey(file.getAbsolutePath())) {
              int lastIndexOf = name.lastIndexOf('-');
              if (lastIndexOf != -1) {
                String hash = getMd5(file.getAbsolutePath());
                String type = "Web";
                Path prjFile = Paths.get(file.getParent(), new String[] { ".cproject" });
                if (Files.exists(prjFile, new java.nio.file.LinkOption[0]))
                  type = "Native"; 
                String os = System.getProperty("os.name");
                String profile = name.substring(0, lastIndexOf);
                String version = name.substring(lastIndexOf + 1);
                tpkLoaded.put(file.getAbsolutePath(), new Boolean(true));
                RemoteLogger.logProfile(hash, "TizenStudio", profile, version, os, type);
              } 
            } 
            return name;
          } 
        } 
      } 
    } 
    return null;
  }
  
  public TizenProjectDescription unmarshal() {
    if (this.project == null)
      return null; 
    File file = this.project.getFile(this.FILE_NAME).getLocation().toFile();
    return unmarshal(file);
  }
  
  public TizenProjectDescription unmarshal(File file) {
    if (!file.exists()) {
      logger.error(
          String.format("File does not exist. Check the file path. (project: %s, file: %s)", new Object[] { this.project, file }));
      return null;
    } 
    Tproject tProject = null;
    FileInputStream is = null;
    try {
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      is = new FileInputStream(file);
      tProject = (Tproject)unmarshaller.unmarshal(is);
    } catch (Exception e) {
      logger.error("cannot unmarshal in " + this.project.getName(), e);
      return null;
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
    List<Platform> platformList = getPlatformList(tProject);
    if (platformList == null || platformList.isEmpty())
      return null; 
    List<PackageResourceInfo> blackList = getBlacklist(tProject);
    TizenProjectDescription desc = new TizenProjectDescription(this.project);
    desc.setPlatformName(((Platform)platformList.get(0)).getName());
    desc.setBlacklist(blackList);
    List<Library> libList = getReferencedProjectList(tProject);
    desc.setReferencedLibraryProjectList(libList);
    desc.setSubProjectList(getSubProject(tProject));
    desc.setAutoGenResMetaFile(Boolean.valueOf(isAutoGenResMetaFile(tProject)));
    desc.setUsePrebuiltIndexer(Boolean.valueOf(canUsePreBuiltIndex(tProject)));
    return desc;
  }
  
  private boolean canUsePreBuiltIndex(Tproject tProject) {
    CanUsePrebuiltIndex canUsePrebuiltIndex = tProject.getCanUsePrebuiltIndex();
    if (canUsePrebuiltIndex != null && canUsePrebuiltIndex.isValue())
      return true; 
    return false;
  }
  
  private boolean isAutoGenResMetaFile(Tproject tProject) {
    boolean isAutoGen = true;
    Package p = tProject.getPackage();
    if (p == null)
      return isAutoGen; 
    ResFallback r = p.getResFallback();
    if (r == null)
      return isAutoGen; 
    return r.isAutoGen();
  }
  
  private List<TizenProjectDescription.RefTizenProject> getSubProject(Tproject tProject) {
    List<TizenProjectDescription.RefTizenProject> subProjectList = new ArrayList<>();
    Package pkg = tProject.getPackage();
    if (pkg == null)
      return subProjectList; 
    SubProjects subProjects = pkg.getSubProjects();
    if (subProjects == null)
      return subProjectList; 
    List<TizenProject> tizenProject = subProjects.getTizenProject();
    if (tizenProject == null || tizenProject.isEmpty())
      return subProjectList; 
    for (TizenProject project : tizenProject)
      subProjectList.add(new TizenProjectDescription.RefTizenProject(project.getProject(), project.getStyle())); 
    return subProjectList;
  }
  
  private List<PackageResourceInfo> getBlacklist(Tproject tProject) {
    List<PackageResourceInfo> resInfoList = new ArrayList<>();
    Package pkg = tProject.getPackage();
    if (pkg == null)
      return resInfoList; 
    Blacklist blacklist = pkg.getBlacklist();
    PackageResourceInfo pkgResInfo = null;
    for (Name name : blacklist.getName()) {
      pkgResInfo = new PackageResourceInfo(name.getValue(), 
          convertToInt(name.getType()), 0, "", 
          false);
      addExcludeElementsToParent(name.getExclude(), pkgResInfo);
      resInfoList.add(pkgResInfo);
    } 
    for (Path path : blacklist.getPath())
      resInfoList.add(new PackageResourceInfo(path.getValue(), 
            convertToInt(path.getType()), 1, "", 
            false)); 
    for (Regex regex : blacklist.getRegex()) {
      pkgResInfo = new PackageResourceInfo(regex.getValue(), 
          convertToInt(regex.getType()), 3, 
          "", false);
      addExcludeElementsToParent(regex.getExclude(), pkgResInfo);
      resInfoList.add(pkgResInfo);
    } 
    return resInfoList;
  }
  
  private List<Library> getReferencedProjectList(Tproject tProject) {
    Package pkg = tProject.getPackage();
    if (pkg == null)
      return new ArrayList<>(); 
    return pkg.getReferencedProject();
  }
  
  private void addExcludeElementsToParent(List<Exclude> excludeElementList, PackageResourceInfo parent) {
    List<PackageResourceInfo> excludeList = parent.getExcludeList();
    for (Exclude exclude : excludeElementList)
      excludeList.add(new PackageResourceInfo(exclude.getValue(), 
            convertToInt(exclude.getType()), 
            3, "", false)); 
  }
  
  private int convertToInt(String type) {
    if (StringUtil.isEmpty(type) || type.equals("all"))
      return 0; 
    if (type.equals("file"))
      return 1; 
    return 2;
  }
  
  private String convertToStr(int type) {
    if (type == 0)
      return "all"; 
    if (1 == type)
      return "file"; 
    return "dir";
  }
  
  private List<Platform> getPlatformList(Tproject tProject) {
    Platforms platforms = tProject.getPlatforms();
    if (platforms == null) {
      logger.error("cannot find platforms element in " + 
          this.project.getName());
      return null;
    } 
    List<Platform> platformList = platforms.getPlatform();
    if (platformList.isEmpty()) {
      logger.error("cannot find platform elements in " + 
          this.project.getName());
      return null;
    } 
    return platformList;
  }
  
  private Tproject makeModel(TizenProjectDescription desc) {
    ObjectFactory objFactory = new ObjectFactory();
    Tproject tProject = objFactory.createTproject();
    Platform platform = objFactory.createPlatform();
    Platforms platforms = objFactory.createPlatforms();
    if (desc.canUsePrebuiltIndexer().booleanValue()) {
      CanUsePrebuiltIndex canUsePreBuiltIndex = objFactory.createCanUsePrebuiltIndex();
      canUsePreBuiltIndex.setValue(true);
      tProject.setCanUsePrebuiltIndex(canUsePreBuiltIndex);
    } 
    platform.setName(desc.getPlatformName());
    platforms.getPlatform().add(platform);
    tProject.setPlatforms(platforms);
    Package pkg = objFactory.createPackage();
    Blacklist blacklist = objFactory.createBlacklist();
    pkg.setBlacklist(blacklist);
    tProject.setPackage(pkg);
    List<Name> nameList = blacklist.getName();
    List<Path> pathList = blacklist.getPath();
    List<Regex> regexList = blacklist.getRegex();
    Name name = null;
    Path path = null;
    Regex regex = null;
    for (PackageResourceInfo resInfo : desc.getBlacklist()) {
      if (resInfo.getElementType() == 0) {
        name = objFactory.createName();
        name.setType(convertToStr(resInfo.getResourceType()));
        name.setValue(resInfo.getName());
        makeExcludeElements(resInfo, name.getExclude());
        nameList.add(name);
        continue;
      } 
      if (resInfo.getElementType() == 1) {
        path = objFactory.createPath();
        path.setType(convertToStr(resInfo.getResourceType()));
        path.setValue(resInfo.getName());
        pathList.add(path);
        continue;
      } 
      regex = objFactory.createRegex();
      regex.setType(convertToStr(resInfo.getResourceType()));
      regex.setValue(resInfo.getName());
      makeExcludeElements(resInfo, regex.getExclude());
      regexList.add(regex);
    } 
    makeSubProjects(desc, pkg, objFactory);
    pkg.getReferencedProject().addAll(
        desc.getReferencedLibraryProjectList());
    Boolean isAutoGen = desc.isAutoGenResMetaFileForTproject();
    if (isAutoGen != null) {
      ResFallback resFallback = objFactory.createResFallback();
      resFallback.setAutoGen(desc.isAutoGenResMetaFile());
      pkg.setResFallback(resFallback);
    } 
    return tProject;
  }
  
  private void makeSubProjects(TizenProjectDescription desc, Package pkg, ObjectFactory objFactory) {
    List<TizenProjectDescription.RefTizenProject> subProjectsList = getSubProjects(desc);
    if (subProjectsList == null || subProjectsList.isEmpty())
      return; 
    SubProjects subProjects = objFactory.createSubProjects();
    List<TizenProject> tizenProjectList = subProjects.getTizenProject();
    for (TizenProjectDescription.RefTizenProject subProject : subProjectsList) {
      TizenProject tizenProject = objFactory.createTizenProject();
      tizenProject.setProject(subProject.getName());
      if (!StringUtil.isEmpty(subProject.getStyle()))
        tizenProject.setStyle(subProject.getStyle()); 
      tizenProjectList.add(tizenProject);
    } 
    pkg.setSubProjects(subProjects);
  }
  
  private List<TizenProjectDescription.RefTizenProject> getSubProjects(TizenProjectDescription desc) {
    List<TizenProjectDescription.RefTizenProject> subProjectList = new ArrayList<>(desc.getSubProjectList());
    for (Library library : desc.getReferencedLibraryProjectList()) {
      for (TizenProjectDescription.RefTizenProject refTizenProject : desc.getSubProjectList()) {
        if (library.getProject().equals(refTizenProject.getName())) {
          subProjectList.remove(refTizenProject);
          break;
        } 
      } 
    } 
    return subProjectList;
  }
  
  private void makeExcludeElements(PackageResourceInfo resInfo, List<Exclude> excludeList) {
    Exclude exclude = null;
    for (PackageResourceInfo resChild : resInfo.getExcludeList()) {
      exclude = objFactory.createExclude();
      exclude.setType(convertToStr(resChild.getResourceType()));
      exclude.setValue(resChild.getName());
      excludeList.add(exclude);
    } 
  }
}
