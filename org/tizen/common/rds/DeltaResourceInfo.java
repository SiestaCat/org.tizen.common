package org.tizen.common.rds;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IResource;

public class DeltaResourceInfo {
  public static final String TYPE_MODIFY = "modify";
  
  public static final String TYPE_ADD = "add";
  
  public static final String TYPE_DELETE = "delete";
  
  public static final String TYPE_INSTALL = "install";
  
  private static final String SEPERATOR = "/";
  
  private String name;
  
  private String type;
  
  private int resourceType;
  
  private String fullPath;
  
  private String remotePath;
  
  private String projectName;
  
  private IResource res;
  
  private List<DeltaResourceInfo> children = new ArrayList<>();
  
  public DeltaResourceInfo() {}
  
  public DeltaResourceInfo(String name, String fullPath, String remotePath, String type) {
    this(name, fullPath, remotePath, type, -1);
  }
  
  public DeltaResourceInfo(String name, String fullPath, String type, int resourceType) {
    this(name, fullPath, null, type, resourceType);
  }
  
  public DeltaResourceInfo(String name, String fullPath, String type) {
    this(name, fullPath, null, type, -1);
  }
  
  public DeltaResourceInfo(String fullPath, String remotePath) {
    this(null, fullPath, remotePath, null, -1);
  }
  
  public DeltaResourceInfo(String fullPath) {
    this(null, fullPath, fullPath, null, -1);
  }
  
  public DeltaResourceInfo(String name, String fullPath, String remotePath, String type, int resourceType) {
    this.name = name;
    this.fullPath = fullPath;
    this.remotePath = remotePath;
    this.type = type;
    this.resourceType = resourceType;
  }
  
  public DeltaResourceInfo(String name, String fullPath, String remotePath, String type, int resourceType, String projectName) {
    this.name = name;
    this.fullPath = fullPath;
    this.remotePath = remotePath;
    this.type = type;
    this.resourceType = resourceType;
    this.projectName = projectName;
  }
  
  public DeltaResourceInfo(String name, String fullPath, String remotePath, String type, int resourceType, String projectName, IResource res) {
    this.name = name;
    this.fullPath = fullPath;
    this.remotePath = remotePath;
    this.type = type;
    this.resourceType = resourceType;
    this.projectName = projectName;
    this.res = res;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getType() {
    return this.type;
  }
  
  public int getResourceType() {
    return this.resourceType;
  }
  
  public void setFullPath(String fullPath) {
    this.fullPath = fullPath;
  }
  
  public String getFullPath() {
    return this.fullPath;
  }
  
  public String getRealPath() {
    if (this.res != null && this.res.isLinked()) {
      StringBuffer sb = new StringBuffer();
      byte b;
      int i;
      String[] arrayOfString;
      for (i = (arrayOfString = this.res.getLocation().segments()).length, b = 0; b < i; ) {
        String segment = arrayOfString[b];
        sb.append("/" + segment);
        b++;
      } 
      return this.res.getLocation().toString();
    } 
    return this.fullPath;
  }
  
  public void setRemotePath(String path) {
    this.remotePath = path;
  }
  
  public String getRemotePath() {
    return this.remotePath;
  }
  
  public String getProjectName() {
    return this.projectName;
  }
  
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
  
  public List<DeltaResourceInfo> getChildren() {
    return this.children;
  }
  
  public List<DeltaResourceInfo> setChildren(List<DeltaResourceInfo> children) {
    return this.children = children;
  }
  
  public IResource getResource() {
    return this.res;
  }
  
  public String toString() {
    if (this.name == null)
      this.name = getDeltaNameOfFullPath(this.fullPath); 
    return this.name;
  }
  
  private String getDeltaNameOfFullPath(String fullPath) {
    String name = null;
    int lastIndexOf = fullPath.lastIndexOf("/");
    if (lastIndexOf >= 0) {
      int length = fullPath.length();
      if (lastIndexOf != length - 1) {
        name = fullPath.substring(lastIndexOf + 1);
      } else {
        fullPath = fullPath.substring(0, length - 1);
        name = getDeltaNameOfFullPath(fullPath);
      } 
    } 
    return name;
  }
  
  public static String convertToRemotePath(String hostPath, String prefix, String replacement) {
    String result = null;
    if (hostPath.startsWith(prefix))
      result = String.valueOf(replacement) + hostPath.substring(prefix.length(), hostPath.length()); 
    return result;
  }
}
