package org.tizen.common.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.ArrayUtil;
import org.tizen.common.util.FilenameUtil;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.StringUtil;

public class VirtualFileHandler extends AbstractFileHandler {
  protected static final String ROOT_PREFIX = "/";
  
  private class File implements Cloneable {
    String name;
    
    byte[] contents;
    
    HashMap<FileHandler.Attribute, Object> attr = new HashMap<>();
    
    public File() {}
    
    public File(String name, byte[] contents) {
      this.name = name;
      this.contents = contents;
    }
    
    public String getName() {
      return this.name;
    }
    
    public byte[] getContents() {
      return this.contents;
    }
    
    public void setContents(byte[] contents) {
      this.contents = contents;
    }
    
    public void setAttribute(FileHandler.Attribute name, Object value) {
      this.attr.put(name, value);
    }
    
    public Object getAttribute(FileHandler.Attribute name) {
      return this.attr.get(name);
    }
    
    public Object clone() {
      File file = new File();
      file.name = this.name;
      file.contents = this.contents;
      return file;
    }
  }
  
  class Directory extends File implements Cloneable {
    protected final HashMap<String, VirtualFileHandler.File> name2file = new HashMap<>();
    
    public Directory() {}
    
    public Directory(String name) {
      super(name, null);
    }
    
    public VirtualFileHandler.File get(String name) {
      return this.name2file.get(name);
    }
    
    public VirtualFileHandler.File remove(String name) {
      return this.name2file.remove(name);
    }
    
    public void add(String name, byte[] contents) {
      this.name2file.put(name, new VirtualFileHandler.File(name, contents));
    }
    
    public void add(String name) {
      this.name2file.put(name, new Directory(name));
    }
    
    public void add(VirtualFileHandler.File file) {
      this.name2file.put(file.getName(), file);
    }
    
    public Object clone() {
      Directory dir = new Directory();
      dir.contents = this.contents;
      dir.name = this.name;
      for (String name : this.name2file.keySet())
        this.name2file.put(name, (VirtualFileHandler.File)((VirtualFileHandler.File)this.name2file.get(name)).clone()); 
      return dir;
    }
    
    public String toString() {
      return this.name2file.keySet().toString();
    }
  }
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final Directory root = new Directory();
  
  protected String cwd = "/";
  
  protected File getFile(String path) {
    this.logger.trace("Path :{}", path);
    if (path == null)
      return null; 
    String[] fragments = FilenameUtil.getCanonicalFragments(path);
    File iter = this.root;
    for (int i = 0, n = fragments.length; i < n; i++) {
      Directory dir = (Directory)iter;
      iter = dir.get(fragments[i]);
      if (iter == null) {
        this.logger.debug("fragment :{}", fragments[i]);
        this.logger.debug("in-processing directory :{}", dir);
        return null;
      } 
    } 
    return iter;
  }
  
  public Directory getParent(String path) throws IOException {
    File parent = getFile(FilenameUtil.removeTailingPath(path, 1));
    if (parent == null)
      return null; 
    if (!(parent instanceof Directory))
      throw new IOException(); 
    return (Directory)parent;
  }
  
  public void makeDirectory(String path) throws IOException {
    this.logger.debug("Making directory : {}", path);
    Directory parent = getParent(path);
    String dirName = FilenameUtil.getFilename(path);
    if (parent.name2file.containsKey(dirName))
      throw new IOException(); 
    parent.add(dirName);
  }
  
  public void moveDirectory(String source, String target) throws IOException {
    String filename = FilenameUtil.getFilename(source);
    Directory parent = getParent(source);
    File file = parent.get(filename);
    if (!(file instanceof Directory))
      throw new IOException(); 
    Directory newParent = (Directory)getFile(target);
    newParent.add(newParent.remove(filename));
  }
  
  public void copyDirectory(String source, String target) throws IOException {
    Directory dir = (Directory)getFile(source);
    Directory targetDir = (Directory)getFile(target);
    targetDir.add((File)dir.clone());
  }
  
  public void removeDirectory(String path) throws IOException {
    Directory dir = getParent(path);
    String name = FilenameUtil.getFilename(path);
    dir.remove(name);
  }
  
  public Collection<String> list(String path) throws IOException {
    Directory dir = (Directory)getFile(path);
    Collection<String> childNames = dir.name2file.keySet();
    this.logger.trace("Directory[{}]'s child :{}", path, childNames);
    ArrayList<String> fullNames = new ArrayList<>();
    for (String name : dir.name2file.keySet())
      fullNames.add(FilenameUtil.addTailingPath(path, name)); 
    this.logger.debug("Fullnames :\n{}", fullNames);
    return fullNames;
  }
  
  public void write(String path, InputStream in) throws IOException {
    Directory parent = getParent(path);
    if (parent == null) {
      makeDirectory(FilenameUtil.removeTailingPath(path, 1), true);
      parent = getParent(path);
    } 
    String filename = FilenameUtil.getFilename(path);
    this.logger.debug("Filename :{}", filename);
    File file = parent.get(filename);
    if (file == null)
      file = new File(FilenameUtil.getFilename(path), null); 
    if (file instanceof Directory)
      throw new IOException(); 
    getParent(path).add(file);
    byte[] contents = IOUtil.getBytes(in);
    if (this.logger.isTraceEnabled()) {
      int nSize = ArrayUtil.size(contents);
      this.logger.trace(
          "Save file[{}] :{} byte(s)\n{}", 
          new Object[] { path, 
            Integer.valueOf(nSize), 
            String.valueOf(StringUtil.text2hexa(contents, 0, Math.min(64, ArrayUtil.size(contents)))) + ((64 < ArrayUtil.size(contents)) ? "..." : "") });
    } 
    file.setContents(contents);
  }
  
  public void write(String path, String contents) throws IOException {
    this.logger.trace("Save file :{}\n{}", path, contents);
    InputStream in = new ByteArrayInputStream((contents == null) ? new byte[0] : contents.getBytes());
    try {
      write(path, in);
    } finally {
      IOUtil.tryClose(new Object[] { in });
    } 
  }
  
  public InputStream read(String path) throws IOException {
    this.logger.trace("Read {}", path);
    File file = getFile(path);
    if (file instanceof Directory)
      throw new IOException(); 
    return new ByteArrayInputStream(file.contents);
  }
  
  public void moveFile(String source, String target) throws IOException {
    Directory directory = getParent(source);
    String fileName = FilenameUtil.getFilename(source);
    File file = directory.remove(fileName);
    if (file == null)
      throw new IOException(); 
    Directory targetDirectory = (Directory)getFile(target);
    targetDirectory.add(file);
  }
  
  public void copyFile(String source, String target) throws IOException {
    Directory directory = getParent(source);
    String fileName = FilenameUtil.getFilename(source);
    File file = directory.get(fileName);
    if (file == null)
      throw new IOException(); 
    Directory targetDirectory = (Directory)getFile(target);
    targetDirectory.add((File)file.clone());
  }
  
  public void removeFile(String path) throws IOException {
    File file = getFile(path);
    if (file instanceof Directory)
      throw new IOException(); 
    Directory directory = getParent(path);
    String fileName = FilenameUtil.getFilename(path);
    directory.remove(fileName);
  }
  
  public Object get(String path, FileHandler.Attribute name) throws IOException {
    this.logger.trace("Path :{}, Name :{}", path, name);
    File file = getFile(path);
    if (FileHandler.Attribute.TYPE.equals(name)) {
      if (file instanceof Directory)
        return FileHandler.Type.DIRECTORY; 
      return FileHandler.Type.FILE;
    } 
    if (FileHandler.Attribute.EXISTS.equals(name))
      return (file != null) ? Boolean.valueOf(true) : Boolean.valueOf(false); 
    if (FileHandler.Attribute.SIZE.equals(name)) {
      if (file == null || file.getContents() == null)
        return Long.valueOf(0L); 
      return Long.valueOf((file.getContents()).length);
    } 
    if (FileHandler.Attribute.PATH.equals(name)) {
      if (path.startsWith("/"))
        return FilenameUtil.getCanonicalForm(path); 
      return FilenameUtil.getCanonicalForm(FilenameUtil.addTailingPath(this.cwd, path));
    } 
    if (FileHandler.Attribute.NAME.equals(name))
      return FilenameUtil.getFilename(path); 
    if (FileHandler.Attribute.URI.equals(name) || FileHandler.Attribute.URL.equals(name))
      return "vf://" + path; 
    file.getAttribute(name);
    return Boolean.valueOf(false);
  }
  
  public void set(String path, FileHandler.Attribute name, Object value) throws IOException {
    File file = getFile(path);
    if (file != null)
      file.setAttribute(name, value); 
  }
  
  public String getCurrentWorkingDirectory() {
    return this.cwd;
  }
  
  public void setCurrentWorkingDirectory(String cwd) {
    this.cwd = cwd;
    this.logger.info("CWD changed :{}", this.cwd);
  }
}
