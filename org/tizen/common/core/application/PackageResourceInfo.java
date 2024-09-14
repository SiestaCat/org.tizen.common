package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Path;

public class PackageResourceInfo {
  private String name;
  
  private int resourceType;
  
  private int elementType;
  
  private boolean isRequirement;
  
  private String remotePath;
  
  private List<PackageResourceInfo> excludeList = new ArrayList<>();
  
  public static final int NAME = 0;
  
  public static final int PATH = 1;
  
  public static final int REGEX = 2;
  
  public static final int EXCLUDE = 3;
  
  public PackageResourceInfo(String name, int resourceType, int elementType, String remotePath, boolean isRequirement) {
    this.name = name;
    this.resourceType = resourceType;
    this.elementType = elementType;
    this.remotePath = remotePath;
    this.isRequirement = isRequirement;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getResourceType() {
    return this.resourceType;
  }
  
  public int getElementType() {
    return this.elementType;
  }
  
  @Deprecated
  public String remotePath() {
    return this.remotePath;
  }
  
  public String getRemotePath() {
    return this.remotePath;
  }
  
  public boolean isRequirement() {
    return this.isRequirement;
  }
  
  public List<PackageResourceInfo> getExcludeList() {
    return this.excludeList;
  }
  
  public void addExcludeItem(PackageResourceInfo pkgResInfo) {
    boolean isValid = false;
    if (this.elementType != 0 || pkgResInfo.getElementType() != 3)
      return; 
    String parentName = (new Path(this.name)).lastSegment();
    String[] newChildSegments = (new Path(pkgResInfo.getName())).segments();
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = newChildSegments).length, b = 0; b < i; ) {
      String newChildSegment = arrayOfString1[b];
      if (newChildSegment.equals(parentName)) {
        isValid = true;
        break;
      } 
      b++;
    } 
    if (!isValid)
      return; 
    for (PackageResourceInfo exclude : this.excludeList) {
      if (exclude.isNameMatch(pkgResInfo))
        return; 
    } 
    this.excludeList.add(pkgResInfo);
  }
  
  public boolean isSupportedType(int type) {
    if (this.resourceType == 0)
      return true; 
    if (this.resourceType == type)
      return true; 
    return false;
  }
  
  public boolean isNameMatch(PackageResourceInfo pkgResInfo) {
    String[] segments = (new Path(this.name)).segments();
    String[] newSegments = (new Path(pkgResInfo.getName())).segments();
    if (segments.length != newSegments.length)
      return false; 
    for (int i = 0; i < segments.length; i++) {
      if (!segments[i].equals(newSegments[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean isMatch(PackageResourceInfo pkgResInfo) {
    if (!isSupportedType(pkgResInfo.getResourceType()))
      return false; 
    if (this.elementType != pkgResInfo.getElementType())
      return false; 
    return isNameMatch(pkgResInfo);
  }
  
  public String toString() {
    return getName();
  }
}
