package org.tizen.common.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.tizen.common.util.CollectionUtil;
import org.tizen.common.util.FileUtil;
import org.tizen.common.util.FilenameUtil;
import org.tizen.common.util.ObjectUtil;

public class EclipseFileHandler extends StandardFileHandler {
  protected IWorkspaceRoot getWorkspace() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
  
  protected IResource getResource(String path) throws IOException {
    IFolder iFolder;
    String root = getWorkspace().getLocation().toPortableString();
    this.logger.trace("Path :{}, Root :{}", path, root);
    File fileChecker = new File(path);
    String relativePath = path;
    if (FilenameUtil.isAncestor(root, path)) {
      relativePath = FilenameUtil.getRelativePath(root, path);
    } else if (fileChecker.isAbsolute()) {
      return null;
    } 
    Queue<String> fragments = 
      new LinkedList<>(Arrays.asList(FilenameUtil.getCanonicalFragments(relativePath)));
    String projectName = fragments.remove();
    if (".metadata".equals(projectName))
      return null; 
    IProject project = getWorkspace().getProject(projectName);
    if (!project.exists())
      return null; 
    if (fragments.isEmpty())
      return (IResource)project; 
    String fileName = CollectionUtil.<String>removeLast(fragments);
    IProject iProject1 = project;
    if (!fragments.isEmpty())
      iFolder = project.getFolder(CollectionUtil.concatenate(fragments, "/")); 
    try {
      if (iFolder.exists()) {
        IResource[] children = iFolder.members();
        byte b;
        int i;
        IResource[] arrayOfIResource1;
        for (i = (arrayOfIResource1 = children).length, b = 0; b < i; ) {
          IResource chlid = arrayOfIResource1[b];
          if (ObjectUtil.equals(fileName, chlid.getName()))
            return chlid; 
          b++;
        } 
      } 
      fragments.add(fileName);
      String relativeFile = CollectionUtil.concatenate(fragments, "/");
      return (IResource)project.getFile(relativeFile);
    } catch (CoreException e) {
      this.logger.warn("Exception :{}", (Throwable)e);
      return null;
    } 
  }
  
  protected IContainer getFolder(String path) {
    IFolder iFolder;
    String root = getWorkspace().getLocation().toPortableString();
    this.logger.trace("Path :{}, Root :{}", path, root);
    File fileChecker = new File(path);
    String relativePath = path;
    if (FilenameUtil.isAncestor(root, path)) {
      relativePath = FilenameUtil.getRelativePath(root, path);
    } else if (fileChecker.isAbsolute()) {
      return null;
    } 
    Queue<String> fragments = 
      new LinkedList<>(Arrays.asList(FilenameUtil.getCanonicalFragments(relativePath)));
    String projectName = fragments.remove();
    if (".metadata".equals(projectName))
      return null; 
    IProject project = getWorkspace().getProject(projectName);
    if (fragments.isEmpty())
      return (IContainer)project; 
    String fileName = CollectionUtil.<String>removeLast(fragments);
    IProject iProject1 = project;
    if (!fragments.isEmpty())
      iFolder = project.getFolder(CollectionUtil.concatenate(fragments, "/")); 
    try {
      if (iFolder.exists()) {
        IResource[] children = iFolder.members();
        byte b;
        int i;
        IResource[] arrayOfIResource1;
        for (i = (arrayOfIResource1 = children).length, b = 0; b < i; ) {
          IResource child = arrayOfIResource1[b];
          if (child instanceof IContainer && 
            ObjectUtil.equals(fileName, child.getName()))
            return (IContainer)child; 
          b++;
        } 
      } 
      fragments.add(fileName);
      String relativeFile = CollectionUtil.concatenate(fragments, "/");
      return (IContainer)project.getFolder(relativeFile);
    } catch (CoreException e) {
      this.logger.warn("Exception :{}", (Throwable)e);
      return null;
    } 
  }
  
  public String getCurrentWorkingDirectory() {
    return getWorkspace().getLocation().toPortableString();
  }
  
  public void setCurrentWorkingDirectory(String cwd) {
    throw new UnsupportedOperationException();
  }
  
  public void makeDirectory(String path) throws IOException {
    File file = new File(path);
    if (file.isAbsolute()) {
      super.makeDirectory(path);
      Path p = new Path(path);
      try {
        IFile fileLocation = getWorkspace().getFileForLocation((IPath)p);
        if (fileLocation != null)
          fileLocation.refreshLocal(-1, (IProgressMonitor)new NullProgressMonitor()); 
      } catch (CoreException e) {
        throw new IOException(e);
      } 
    } 
  }
  
  public void moveDirectory(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void copyDirectory(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void removeDirectory(String path) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public Collection<String> list(String path) throws IOException {
    IContainer folder = getFolder(path);
    if (folder == null || !folder.exists())
      return super.list(path); 
    try {
      IResource[] children = folder.members();
      ArrayList<String> ret = new ArrayList<>();
      byte b;
      int i;
      IResource[] arrayOfIResource1;
      for (i = (arrayOfIResource1 = children).length, b = 0; b < i; ) {
        IResource child = arrayOfIResource1[b];
        String childPath = FilenameUtil.getCanonicalForm(child.getLocation().toPortableString());
        if (child instanceof IContainer)
          childPath = String.valueOf(childPath) + "/"; 
        if (childPath.startsWith("/")) {
          ret.add(childPath.substring(1));
        } else {
          ret.add(childPath);
        } 
        b++;
      } 
      this.logger.trace("Children :{}", ret);
      return ret;
    } catch (CoreException e) {
      throw new IOException(e);
    } 
  }
  
  public void write(String path, InputStream out) throws IOException {
    this.logger.trace("Path :{}", path);
    IResource file = getResource(path);
    if (file == null) {
      super.write(path, out);
      return;
    } 
    IFile f = (IFile)file.getAdapter(IFile.class);
    try {
      if (f.exists()) {
        f.setContents(out, false, false, (IProgressMonitor)new NullProgressMonitor());
      } else {
        f.create(out, false, (IProgressMonitor)new NullProgressMonitor());
      } 
    } catch (CoreException e) {
      throw new IOException(e);
    } 
  }
  
  public InputStream read(String path) throws IOException {
    Path p = new Path(path);
    IFile eFile = getWorkspace().getFileForLocation((IPath)p);
    if (eFile == null)
      return super.read(path); 
    try {
      return eFile.getContents();
    } catch (CoreException e) {
      throw new IOException(e);
    } 
  }
  
  public void moveFile(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void copyFile(String source, String target) throws IOException {
    FileUtil.copyTo(source, target);
    Path targetPath = new Path(target);
    try {
      IFile file = getWorkspace().getFileForLocation((IPath)targetPath);
      if (file != null)
        file.refreshLocal(-1, (IProgressMonitor)new NullProgressMonitor()); 
    } catch (CoreException e) {
      throw new IOException(e);
    } 
  }
  
  public void removeFile(String path) throws IOException {
    this.logger.trace("Path :{}", path);
    IResource file = getResource(path);
    if (file == null || file.getType() != 1)
      throw new IOException(); 
    try {
      file.delete(true, null);
    } catch (CoreException e) {
      throw new IOException(e);
    } 
  }
  
  public Object get(String path, FileHandler.Attribute name) throws IOException {
    this.logger.trace("Path :{}, Attribute :{}", path, name);
    IResource file = getResource(path);
    if (file == null)
      return super.get(path, name); 
    if (FileHandler.Attribute.TYPE.equals(name)) {
      if (file instanceof IContainer)
        return FileHandler.Type.DIRECTORY; 
      if (file instanceof IFile)
        return FileHandler.Type.FILE; 
    } else {
      if (FileHandler.Attribute.EXISTS.equals(name)) {
        if (file.exists())
          return Boolean.valueOf(true); 
        return super.get(path, name);
      } 
      if (FileHandler.Attribute.PATH.equals(name))
        return file.getLocation().toPortableString(); 
      if (FileHandler.Attribute.URI.equals(name) || FileHandler.Attribute.URL.equals(name))
        return file.getLocationURI().toURL().toString(); 
      if (FileHandler.Attribute.MODIFIED.equals(name))
        return Long.valueOf(file.getLocalTimeStamp()); 
      if (FileHandler.Attribute.HIDDEN.equals(name))
        return Boolean.valueOf(file.isHidden()); 
      if (FileHandler.Attribute.READABLE.equals(name))
        return Boolean.valueOf(file.isAccessible()); 
      if (FileHandler.Attribute.NAME.equals(name))
        return file.getName(); 
      if (FileHandler.Attribute.WRITABLE.equals(name))
        return Boolean.valueOf(file.isAccessible()); 
    } 
    throw new IOException(String.valueOf(path) + "'s Unknown attribute :" + name);
  }
  
  public void set(String path, FileHandler.Attribute name, Object value) throws IOException {
    if (FileHandler.Attribute.QUALIFIED.equals(name))
      return; 
    throw new UnsupportedOperationException();
  }
  
  public static void save(StringBuffer buffer, IFile file) throws CoreException {
    String encoding = null;
    try {
      encoding = file.getCharset();
    } catch (CoreException coreException) {}
    byte[] bytes = null;
    if (encoding != null) {
      try {
        bytes = buffer.toString().getBytes(encoding);
      } catch (Exception exception) {}
    } else {
      bytes = buffer.toString().getBytes();
    } 
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
    boolean force = true;
    file.setContents(stream, force, true, null);
  }
  
  public String toString() {
    return String.valueOf(getClass().getSimpleName()) + "@" + Integer.toHexString(hashCode()).substring(0, 4);
  }
}
