package org.tizen.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.resources.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.file.FileHandler;
import org.tizen.common.file.IResource;
import org.tizen.common.file.Resource;
import org.tizen.common.file.StandardFileHandler;

public class ResourceUtil {
  private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);
  
  public static IResource getResource(String relativePath, IResource... resources) {
    String findPath = relativePath;
    byte b;
    int i;
    IResource[] arrayOfIResource;
    for (i = (arrayOfIResource = resources).length, b = 0; b < i; ) {
      IResource resource = arrayOfIResource[b];
      String compPath = FilenameUtil.getCanonicalForm(resource.getPath());
      findPath = FilenameUtil.getCanonicalForm(relativePath);
      if (compPath.equals(findPath))
        return resource; 
      b++;
    } 
    return null;
  }
  
  public static void removeResource(String companionFullPath, IResource[] array) {}
  
  public static IResource convertResource(IResource eclipseResource) {
    IResource resource = null;
    try {
      FileHandler fh = new StandardFileHandler(eclipseResource.getProject().getLocation().toOSString());
      resource = new Resource(fh, FilenameUtil.getRelativePath(fh.getCurrentWorkingDirectory(), eclipseResource.getLocation().toOSString()));
    } catch (NullPointerException e) {
      logger.error(e.getMessage(), e);
    } 
    return resource;
  }
  
  public static Collection<IResource> getChildren(IResource resource, boolean recursively) throws IOException {
    Collection<IResource> resources = new ArrayList<>();
    try {
      FileHandler fh = resource.getFileHandler();
      for (String path : fh.list(resource.getPath())) {
        IResource childResource = new Resource(fh, FilenameUtil.getRelativePath(fh.getCurrentWorkingDirectory(), path));
        resources.add(childResource);
        if (FileHandler.Type.DIRECTORY.equals(fh.get(path, FileHandler.Attribute.TYPE)) && recursively)
          resources.addAll(getChildren(childResource, recursively)); 
      } 
    } catch (NullPointerException e) {
      logger.error(e.getMessage(), e);
    } 
    return resources;
  }
}
