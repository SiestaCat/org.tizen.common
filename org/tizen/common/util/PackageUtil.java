package org.tizen.common.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.ITizenProject;
import org.tizen.common.TizenProjectType;
import org.tizen.common.core.application.TizenProjectDescription;
import org.tizen.common.file.FileHandler;
import org.tizen.common.file.IResource;
import org.tizen.common.file.SimpleFileFilter;
import org.tizen.common.packager.PackageResource;

public class PackageUtil extends ZipUtil {
  private static final Logger logger = LoggerFactory.getLogger(PackageUtil.class);
  
  public static boolean createPackage(Collection<IResource> resources, OutputStream os) throws IOException {
    ZipArchiveOutputStream zaos = null;
    if (resources == null)
      return false; 
    try {
      int permissions = -1;
      zaos = new ZipArchiveOutputStream(os);
      zaos.setMethod(8);
      zaos.setLevel(9);
      for (IResource resource : resources) {
        String path = "";
        if (resource instanceof PackageResource) {
          path = ((PackageResource)resource).getDestination();
        } else {
          path = resource.getPath();
        } 
        if (path.startsWith("/") || path.startsWith(File.separator))
          path = path.substring(1); 
        if (OSChecker.isLinux())
          permissions = EFSUtil.getPermissions(
              FilenameUtil.addTailingPath(
                resource.getFileHandler().getCurrentWorkingDirectory(), 
                resource.getPath())); 
        write(zaos, resource, path, permissions);
      } 
      return true;
    } finally {
      IOUtil.tryClose(new Object[] { zaos });
    } 
  }
  
  public static HashSet<IResource> getResources(FileHandler fh, String relativePath, String destPath) throws IOException {
    return getResources(fh, relativePath, destPath, (SimpleFileFilter)null);
  }
  
  public static HashSet<IResource> getResources(FileHandler fh, String relativePath, String destPath, SimpleFileFilter filter) throws IOException {
    HashSet<IResource> fileList = new HashSet<>();
    if (filter != null && !filter.accept(fh.getCurrentWorkingDirectory(), relativePath))
      return fileList; 
    if (!fh.is(relativePath, FileHandler.Attribute.EXISTS))
      return fileList; 
    if (".".equals(destPath) || destPath == null)
      destPath = ""; 
    if (FileHandler.Type.FILE.equals(fh.get(relativePath, FileHandler.Attribute.TYPE))) {
      PackageResource resource = new PackageResource(fh, relativePath, destPath);
      fileList.add(resource);
    } else if (FileHandler.Type.DIRECTORY.equals(fh.get(relativePath, FileHandler.Attribute.TYPE))) {
      fileList.add(new PackageResource(fh, relativePath, destPath));
      Collection<String> list = fh.list(relativePath);
      for (String child : list) {
        String childName = FilenameUtil.getFilename(child);
        fileList.addAll(getResources(fh, FilenameUtil.addTailingPath(relativePath, childName), FilenameUtil.addTailingPath(destPath, childName), filter));
      } 
    } 
    return fileList;
  }
  
  public static List<TizenProjectDescription.RefTizenProject> getMigratingReferencedProjects(IProject targetProject) {
    List<IProject> referencedProjects = ProjectUtil.getReferencedProjects(targetProject);
    IProject[] deprecatedReferencedProjects = null;
    try {
      deprecatedReferencedProjects = targetProject.getReferencedProjects();
    } catch (CoreException e) {
      logger.error(e.getMessage(), (Throwable)e);
      return null;
    } 
    List<IProject> migratingReferencedProjects = new ArrayList<>();
    ArrayList<TizenProjectDescription.RefTizenProject> results = null;
    if (!CollectionUtil.isEmpty(referencedProjects))
      return null; 
    if (ArrayUtil.isEmpty(deprecatedReferencedProjects))
      return null; 
    deprecatedReferencedProjects = sort(deprecatedReferencedProjects);
    TizenProjectType targetProjectType = ProjectUtil.getTizenProjectType(targetProject);
    byte b;
    int i;
    IProject[] arrayOfIProject1;
    for (i = (arrayOfIProject1 = deprecatedReferencedProjects).length, b = 0; b < i; ) {
      IProject deprecatedReferencedProject = arrayOfIProject1[b];
      TizenProjectType drpt = ProjectUtil.getTizenProjectType(deprecatedReferencedProject);
      if (drpt != null && drpt.isReferencedProject(targetProjectType))
        if (!drpt.isNativeSharedLibraryProject())
          if (canAdd(targetProjectType, drpt, migratingReferencedProjects))
            migratingReferencedProjects.add(deprecatedReferencedProject);   
      b++;
    } 
    results = new ArrayList<>();
    for (IProject migratingReferencedProject : migratingReferencedProjects)
      results.add(new TizenProjectDescription.RefTizenProject(migratingReferencedProject.getName(), null)); 
    return results;
  }
  
  private static boolean canAdd(TizenProjectType targetProjectType, TizenProjectType drpt, List<IProject> migratingReferencedProjects) {
    boolean canAdd = true;
    TizenProjectType.SelectableReferenceNum selectableNum = targetProjectType.getSelectableReferenceNum(drpt);
    if (TizenProjectType.SelectableReferenceNum.ZERO.equals(selectableNum)) {
      canAdd = false;
    } else if (TizenProjectType.SelectableReferenceNum.ONE.equals(selectableNum)) {
      for (IProject migratingReferencedProject : migratingReferencedProjects) {
        TizenProjectType projectType = ProjectUtil.getTizenProjectType(migratingReferencedProject);
        if (projectType != null && projectType.equals(drpt)) {
          canAdd = false;
          break;
        } 
      } 
    } 
    return canAdd;
  }
  
  private static IProject[] sort(IProject[] projects) {
    List<IProject> projectList = Arrays.asList(projects);
    Collections.sort(projectList, new Comparator<IProject>() {
          public int compare(IProject project1, IProject project2) {
            return project1.getName().compareTo(project2.getName());
          }
        });
    return projectList.<IProject>toArray(new IProject[0]);
  }
  
  public static void migrateReferencedProjects(IProject targetProject, List<TizenProjectDescription.RefTizenProject> refTizenProjectList) {
    if (!CollectionUtil.isEmpty(refTizenProjectList)) {
      ITizenProject tProject = ProjectUtil.getTizenProject(targetProject);
      TizenProjectDescription desc = tProject.getDescription();
      desc.setSubProjectList(refTizenProjectList);
      tProject.setDescription(desc);
    } 
  }
}
