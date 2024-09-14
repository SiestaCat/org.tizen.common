package org.tizen.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ClasspathFileHandler extends AbstractFileHandler {
  protected String cwd;
  
  public String getCurrentWorkingDirectory() {
    return this.cwd;
  }
  
  public void setCurrentWorkingDirectory(String cwd) {
    this.cwd = cwd;
  }
  
  public void makeDirectory(String path) throws IOException {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }
  
  public void write(String path, InputStream out) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public InputStream read(String path) throws IOException {
    return null;
  }
  
  public void moveFile(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void copyFile(String source, String target) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void removeFile(String path) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public Object get(String path, FileHandler.Attribute name) throws IOException {
    return null;
  }
  
  public void set(String path, FileHandler.Attribute name, Object value) throws IOException {}
}
