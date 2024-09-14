package org.tizen.common.file;

import java.io.IOException;
import java.io.InputStream;

public class LinkedResource extends Resource {
  private boolean isLinked = false;
  
  private String realRelativePath = null;
  
  public LinkedResource(FileHandler fh, String path) {
    super(fh, path);
    this.realRelativePath = path;
  }
  
  public void setLinked(boolean isLinked) {
    this.isLinked = isLinked;
  }
  
  public boolean isLinked() {
    return this.isLinked;
  }
  
  public void setRealRelativePath(String path) {
    this.realRelativePath = path;
  }
  
  public String getRealRelativePath() {
    return this.realRelativePath;
  }
  
  public InputStream getContents() throws IOException {
    return getFileHandler().read(this.realRelativePath);
  }
  
  public void setContents(InputStream in) throws IOException {
    getFileHandler().write(this.realRelativePath, in);
  }
}
