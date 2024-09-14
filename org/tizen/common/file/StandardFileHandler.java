package org.tizen.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.tizen.common.util.FilenameUtil;
import org.tizen.common.util.IOUtil;

public class StandardFileHandler extends AbstractFileHandler {
  protected String cwd;
  
  public StandardFileHandler() {
    try {
      this.cwd = (new File(".")).getCanonicalPath();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public StandardFileHandler(String cwd) {
    try {
      this.cwd = (new File(cwd)).getCanonicalPath();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  protected File getFile(String path) {
    File dummy = new File(path);
    if (dummy.isAbsolute()) {
      this.logger.trace("{} is absolute path", path);
      return dummy;
    } 
    File ret = new File(getCurrentWorkingDirectory(), path);
    this.logger.trace("File {} for {}", ret, path);
    return ret;
  }
  
  public String getCurrentWorkingDirectory() {
    return this.cwd;
  }
  
  public void setCurrentWorkingDirectory(String cwd) {
    this.cwd = cwd;
  }
  
  public void makeDirectory(String path) throws IOException {
    getFile(path).mkdirs();
  }
  
  public void moveDirectory(String source, String target) throws IOException {
    File file = getFile(source);
    if (!file.isDirectory())
      throw new IOException(); 
    file.renameTo(getFile(target));
  }
  
  public void copyDirectory(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void removeDirectory(String path) throws IOException {
    if (!FileHandler.Type.DIRECTORY.equals(get(path, FileHandler.Attribute.TYPE)))
      throw new IOException(); 
    for (String childPath : list(path)) {
      Object type = get(childPath, FileHandler.Attribute.TYPE);
      if (FileHandler.Type.DIRECTORY.equals(type)) {
        removeDirectory(childPath);
        continue;
      } 
      if (FileHandler.Type.FILE.equals(type))
        removeFile(childPath); 
    } 
    getFile(path).delete();
  }
  
  public Collection<String> list(String path) throws IOException {
    File file = getFile(path);
    File[] children = file.listFiles();
    ArrayList<String> ret = new ArrayList<>();
    if (children == null)
      return ret; 
    byte b;
    int i;
    File[] arrayOfFile1;
    for (i = (arrayOfFile1 = children).length, b = 0; b < i; ) {
      File child = arrayOfFile1[b];
      ret.add(FilenameUtil.getCanonicalForm(child.getAbsolutePath()));
      b++;
    } 
    return ret;
  }
  
  public void write(String path, InputStream in) throws IOException {
    File file = getFile(path);
    File parentFile = file.getParentFile();
    if (parentFile != null && !parentFile.exists())
      parentFile.mkdirs(); 
    this.logger.trace("Path :{}", path);
    FileOutputStream fileOut = new FileOutputStream(file);
    try {
      IOUtil.redirect(in, fileOut);
    } finally {
      IOUtil.tryClose(new Object[] { fileOut });
    } 
  }
  
  public InputStream read(String path) throws IOException {
    return new FileInputStream(getFile(path));
  }
  
  public void moveFile(String source, String target) throws IOException {
    File file = getFile(source);
    if (!file.isFile())
      throw new IOException(); 
    file.renameTo(getFile(target));
  }
  
  public void copyFile(String source, String target) throws IOException {
    File file = getFile(source);
    if (!file.isFile())
      throw new IOException(); 
    FileInputStream sourceIn = new FileInputStream(getFile(source));
    FileOutputStream targetOut = new FileOutputStream(getFile(target));
    try {
      IOUtil.redirect(sourceIn, targetOut);
    } finally {
      IOUtil.tryClose(new Object[] { targetOut, sourceIn });
    } 
  }
  
  public void removeFile(String path) throws IOException {
    File file = getFile(path);
    if (!file.isFile()) {
      if (!file.exists()) {
        this.logger.error("{} is not exist.", path);
        throw new FileNotFoundException(String.valueOf(path) + " is not exist.");
      } 
      this.logger.error("{} is not a file", path);
      throw new IOException(String.valueOf(path) + " is not a file.");
    } 
    file.delete();
  }
  
  public Object get(String path, FileHandler.Attribute name) throws IOException {
    this.logger.trace("Path :{}, Attribute :{}", path, name);
    File file = getFile(path);
    if (FileHandler.Attribute.TYPE.equals(name)) {
      if (file.isDirectory())
        return FileHandler.Type.DIRECTORY; 
      if (file.isFile())
        return FileHandler.Type.FILE; 
    } else {
      if (FileHandler.Attribute.EXISTS.equals(name))
        return Boolean.valueOf(file.exists()); 
      if (FileHandler.Attribute.PATH.equals(name))
        return file.getCanonicalFile().getCanonicalPath(); 
      if (FileHandler.Attribute.URI.equals(name) || FileHandler.Attribute.URL.equals(name))
        return file.toURI().toURL().toString(); 
      if (FileHandler.Attribute.MODIFIED.equals(name))
        return Long.valueOf(file.lastModified()); 
      if (FileHandler.Attribute.HIDDEN.equals(name))
        return Boolean.valueOf(file.isHidden()); 
      if (FileHandler.Attribute.READABLE.equals(name))
        return Boolean.valueOf(file.canRead()); 
      if (FileHandler.Attribute.WRITABLE.equals(name))
        return Boolean.valueOf(file.canWrite()); 
      if (FileHandler.Attribute.NAME.equals(name))
        return file.getName(); 
    } 
    throw new IOException(String.valueOf(path) + "'s Unknown attribute :" + name);
  }
  
  public void set(String path, FileHandler.Attribute name, Object value) throws IOException {}
}
