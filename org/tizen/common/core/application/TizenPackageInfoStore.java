package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IProject;
import org.tizen.common.ITizenProject;
import org.tizen.common.rds.DeltaResourceInfo;
import org.tizen.common.util.ProjectUtil;

public class TizenPackageInfoStore {
  private IProject project;
  
  private final List<PackageResourceInfo> resInfoList;
  
  private final List<PackageResourceInfo> invisibleResInfoList;
  
  private List<PackageResourceInfo> defaultBlackList = new ArrayList<>();
  
  public TizenPackageInfoStore(IProject project) {
    this(project, null, null);
  }
  
  public TizenPackageInfoStore(IProject project, List<PackageResourceInfo> resInfoList, List<PackageResourceInfo> invisibleResInfoList) {
    this.project = project;
    if (resInfoList != null) {
      this.resInfoList = resInfoList;
    } else {
      this.resInfoList = new ArrayList<>();
    } 
    if (invisibleResInfoList != null) {
      this.invisibleResInfoList = invisibleResInfoList;
    } else {
      this.invisibleResInfoList = new ArrayList<>();
    } 
  }
  
  public List<PackageResourceInfo> getRequirementRes() {
    List<PackageResourceInfo> requirementResInfo = new ArrayList<>();
    for (PackageResourceInfo resInfo : this.resInfoList) {
      if (resInfo.isRequirement())
        requirementResInfo.add(resInfo); 
    } 
    for (PackageResourceInfo resInfo : this.invisibleResInfoList) {
      if (resInfo.isRequirement())
        requirementResInfo.add(resInfo); 
    } 
    return requirementResInfo;
  }
  
  public List<PackageResourceInfo> getPackageRes() {
    return this.resInfoList;
  }
  
  public List<PackageResourceInfo> getInvisiblePkgRes() {
    return this.invisibleResInfoList;
  }
  
  public List<PackageResourceInfo> getBlackList() {
    ITizenProject tizenProject = ProjectUtil.getTizenProject(this.project);
    TizenProjectDescription tizenDesc = tizenProject.getDescription();
    return tizenDesc.getBlacklist();
  }
  
  public List<PackageResourceInfo> getDefaultBlackList() {
    return this.defaultBlackList;
  }
  
  public List<PackageResourceInfo> getExcludeList() {
    List<PackageResourceInfo> excludeList = new ArrayList<>();
    ITizenProject tizenProject = ProjectUtil.getTizenProject(this.project);
    TizenProjectDescription tizenDesc = tizenProject.getDescription();
    for (PackageResourceInfo blackListItem : tizenDesc.getBlacklist())
      excludeList.addAll(blackListItem.getExcludeList()); 
    return excludeList;
  }
  
  public Map<String, String> getIncludeMap() {
    return new HashMap<>();
  }
  
  public List<DeltaResourceInfo> getInterestList() {
    List<DeltaResourceInfo> interestList = new ArrayList<>();
    return interestList;
  }
  
  public List<DeltaResourceInfo> getIgnoreList() {
    List<DeltaResourceInfo> ignoreList = new ArrayList<>();
    return ignoreList;
  }
  
  public IProject getProject() {
    return this.project;
  }
  
  public List<PackageResourceInfo> getResInfoList() {
    return this.resInfoList;
  }
}
