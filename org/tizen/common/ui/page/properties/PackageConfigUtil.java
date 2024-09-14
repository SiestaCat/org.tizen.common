package org.tizen.common.ui.page.properties;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.application.PackageResourceInfo;
import org.tizen.common.core.application.TizenPackageInfoStore;
import org.tizen.common.util.FilenameUtil;

public class PackageConfigUtil {
  private static Logger logger = LoggerFactory.getLogger(PackageConfigUtil.class);
  
  public static final String TPROJECT_OPTION_ID = "org.tizen.common.option.tproject";
  
  public static final String BLACKLIST_DIRTY_OPTION_ID = "org.tizen.common.preferences.rds";
  
  public static final QualifiedName TPROJECT_BLACKLIST_DIRTY_OPTION_NAME = new QualifiedName("org.tizen.common.option.tproject", "org.tizen.common.preferences.rds");
  
  public static final String OPTION_ENABLE = "true";
  
  public static final String OPTION_DISABLE = "false";
  
  public static boolean isBlackListDirty(IProject project) {
    String option = "";
    try {
      option = project.getPersistentProperty(TPROJECT_BLACKLIST_DIRTY_OPTION_NAME);
    } catch (CoreException coreException) {}
    if (option == null)
      return false; 
    return "true".equals(option);
  }
  
  public static void setBlackListDirty(IProject project, boolean dirty) {
    try {
      if (dirty) {
        project.setPersistentProperty(TPROJECT_BLACKLIST_DIRTY_OPTION_NAME, "true");
      } else {
        project.setPersistentProperty(TPROJECT_BLACKLIST_DIRTY_OPTION_NAME, "false");
      } 
    } catch (CoreException e) {
      logger.debug("Failed to get TPROJECT_BLACKLIST_OPTION_NAME: " + project, (Throwable)e);
    } 
  }
  
  public static boolean isMatch(IResource res, PackageResourceInfo pkgResInfo) {
    IPath resPath = res.getFullPath();
    String[] resSegments = resPath.segments();
    Path path = new Path("/" + res.getProject().getName() + "/" + pkgResInfo.getName());
    String[] pkgResInfoSegments = path.segments();
    if (resSegments.length != pkgResInfoSegments.length)
      return false; 
    for (int i = 0; i < resSegments.length; i++) {
      if (!resSegments[i].equals(pkgResInfoSegments[i]))
        return false; 
    } 
    return pkgResInfo.isSupportedType(res.getType());
  }
  
  public static void addRequirement(TizenPackageInfoStore pkgInfoStore, List<PackageResourceInfo> pkgResInfoList) {
    List<PackageResourceInfo> requirementList = pkgInfoStore.getRequirementRes();
    String parentName = null;
    String[] requirementResSegments = null;
    for (PackageResourceInfo parent : pkgResInfoList) {
      parentName = (new Path(parent.getName())).lastSegment();
      for (PackageResourceInfo child : requirementList) {
        requirementResSegments = (new Path(child.getName())).segments();
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = requirementResSegments).length, b = 0; b < i; ) {
          String requirementResSegment = arrayOfString[b];
          if (requirementResSegment.equals(parentName)) {
            parent.addExcludeItem(
                new PackageResourceInfo(child.getName(), child.getResourceType(), 3, "", child.isRequirement()));
            break;
          } 
          b++;
        } 
      } 
    } 
  }
  
  public static boolean contains(IResource parent, PackageResourceInfo child) {
    String[] parentSegments = parent.getFullPath().segments();
    String[] childSegments = (new Path(String.valueOf(parent.getProject().getName()) + "/" + child.getName())).segments();
    if (parentSegments.length > childSegments.length)
      return false; 
    if (parentSegments.length == childSegments.length) {
      if (!child.isSupportedType(parent.getType()))
        return false; 
    } else if (parent.getType() != 2) {
      return false;
    } 
    for (int i = 0; i < childSegments.length && 
      i < parentSegments.length; i++) {
      if (!parentSegments[i].equals(childSegments[i]))
        return false; 
    } 
    return true;
  }
  
  public static boolean contains(PackageResourceInfo parent, IResource child) {
    return contains(parent, child.getFullPath().toString(), child.getType(), child.getProject());
  }
  
  public static boolean contains(PackageResourceInfo parent, String childResourcePath, int childResourceType, IProject project) {
    switch (parent.getElementType()) {
      case 2:
        if (childResourcePath.matches(parent.getName()))
          return true; 
        return false;
    } 
    String[] childSegments = FilenameUtil.getCanonicalFragments(childResourcePath);
    String[] parentSegments = (new Path(String.valueOf(project.getName()) + "/" + parent.getName())).segments();
    if (parentSegments.length > childSegments.length)
      return false; 
    if (parentSegments.length == childSegments.length) {
      if (!parent.isSupportedType(childResourceType))
        return false; 
    } else if (!parent.isSupportedType(2)) {
      return false;
    } 
    for (int i = 0; i < childSegments.length && 
      i < parentSegments.length; i++) {
      if (!parentSegments[i].equals(childSegments[i]))
        return false; 
    } 
    return true;
  }
}
