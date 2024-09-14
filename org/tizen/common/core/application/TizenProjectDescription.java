package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.tizen.common.core.application.tproject.Library;

public class TizenProjectDescription {
  private IProject project;
  
  private String platform = "";
  
  private List<PackageResourceInfo> blacklist;
  
  private List<Library> referencedLibraryProjectList = new ArrayList<>();
  
  private Boolean isAutoGenResMetaFile = null;
  
  private List<RefTizenProject> subProjectList = new ArrayList<>();
  
  private Boolean canUsePrebuiltIndexer = Boolean.valueOf(false);
  
  public TizenProjectDescription(IProject project) {
    this.project = project;
  }
  
  public String getPlatformName() {
    return this.platform;
  }
  
  public void setPlatformName(String platformName) {
    this.platform = platformName;
  }
  
  public void setPlatform(ProfileInfo profileInfo) {
    setPlatformName(profileInfo.getLatestPlatformName());
  }
  
  public ProfileInfo getPlatformInfo() {
    String version = getVersion();
    String profile = getProfileName();
    if (version == null || profile == null)
      return null; 
    ProfileInfo profileInfo = InstallProfileConfig.getProfileInfoToProfile(profile);
    if (profileInfo == null)
      return new ProfileInfo(profile, version, null); 
    return profileInfo;
  }
  
  public ProfileInfo getProfileInfo() {
    String version = getVersion();
    String profile = getProfileName();
    if (version == null || profile == null)
      return null; 
    ProfileInfo profileInfo = InstallPathConfig.getProfileInfo(profile);
    if (profileInfo == null)
      return new ProfileInfo(profile, version, null); 
    return new ProfileInfo(profile, version, profileInfo.getPlatformPath(version));
  }
  
  public String getProfileName() {
    return InstallPathConfig.getProfile(this.platform);
  }
  
  public String getVersion() {
    return InstallPathConfig.getVersion(this.platform);
  }
  
  public String toString() {
    return "TizenProjectDescription [project: " + this.project + "platform: " + 
      this.platform + "]";
  }
  
  public boolean isSupportedPlatform() {
    String platformName = getPlatformName();
    for (ProfileInfo profileInfo : InstallPathConfig.getProfileInfos()) {
      for (String version : profileInfo.getVersions()) {
        String idePlatformName = profileInfo.getPlatformName(version);
        if (idePlatformName.equals(platformName))
          return true; 
      } 
    } 
    return false;
  }
  
  public void setBlacklist(List<PackageResourceInfo> blacklist) {
    this.blacklist = blacklist;
  }
  
  public List<PackageResourceInfo> getBlacklist() {
    if (this.blacklist == null)
      this.blacklist = new ArrayList<>(); 
    return this.blacklist;
  }
  
  public void setReferencedLibraryProjectList(List<Library> referencedLibraryProjectList) {
    if (this.referencedLibraryProjectList.size() > 0)
      clearReferencedProjectList(); 
    this.referencedLibraryProjectList.addAll(referencedLibraryProjectList);
  }
  
  public void addReferencedLibraryProject(String projectName, String path) {
    Library lib = new Library();
    lib.setProject(projectName);
    lib.setPath(path);
    this.referencedLibraryProjectList.add(lib);
  }
  
  public List<Library> getReferencedLibraryProjectList() {
    return new ArrayList<>(this.referencedLibraryProjectList);
  }
  
  public Map<String, String> getReferencedLibraryProjectMap() {
    Map<String, String> referencedProjectMap = new HashMap<>();
    for (Library libProject : this.referencedLibraryProjectList)
      referencedProjectMap.put(libProject.getProject(), 
          libProject.getPath()); 
    return referencedProjectMap;
  }
  
  public void clearReferencedProjectList() {
    this.referencedLibraryProjectList.clear();
  }
  
  public void setAutoGenResMetaFile(Boolean isAutoGen) {
    this.isAutoGenResMetaFile = isAutoGen;
  }
  
  public boolean isAutoGenResMetaFile() {
    if (this.isAutoGenResMetaFile == null || this.isAutoGenResMetaFile.booleanValue())
      return true; 
    return false;
  }
  
  public Boolean isAutoGenResMetaFileForTproject() {
    return this.isAutoGenResMetaFile;
  }
  
  public void addSubProject(IProject project) {
    this.subProjectList.add(new RefTizenProject(project.getName(), ""));
  }
  
  public void addSubProject(IProject project, String style) {
    this.subProjectList.add(new RefTizenProject(project.getName(), style));
  }
  
  public void setSubProjectList(List<RefTizenProject> subProjectList) {
    this.subProjectList = subProjectList;
  }
  
  public List<RefTizenProject> getSubProjectList() {
    return this.subProjectList;
  }
  
  public Boolean canUsePrebuiltIndexer() {
    return this.canUsePrebuiltIndexer;
  }
  
  public void setUsePrebuiltIndexer(Boolean canUsePrebuiltIndexer) {
    this.canUsePrebuiltIndexer = canUsePrebuiltIndexer;
  }
  
  public static class RefTizenProject {
    private String name;
    
    private String style;
    
    public RefTizenProject(String name, String style) {
      this.name = name;
      this.style = style;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String getStyle() {
      return this.style;
    }
    
    public void setStyle(String style) {
      this.style = style;
    }
  }
}
