package org.tizen.common.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tizen.common.util.IOUtil;
import org.tizen.sdblib.util.FilenameUtil;

public class Resource implements IResource {
  protected String path;
  
  private FileHandler fh;
  
  public Resource(FileHandler fh, String path) {
    this.fh = fh;
    this.path = path;
  }
  
  public String getName() {
    return FilenameUtil.getFilename(this.path);
  }
  
  public String getPath() {
    return this.path;
  }
  
  public InputStream getContents() throws IOException {
    return getFileHandler().read(this.path);
  }
  
  public void setContents(InputStream in) throws IOException {
    getFileHandler().write(this.path, in);
  }
  
  public void setContents(byte[] contents) throws IOException {
    ByteArrayInputStream bais = null;
    try {
      bais = new ByteArrayInputStream(contents);
      setContents(bais);
    } finally {
      IOUtil.tryClose(new Object[] { bais });
    } 
  }
  
  public int hashCode() {
    return (new HashCodeBuilder()).append(this.fh).append(this.path).toHashCode();
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Resource))
      return false; 
    Resource other = (Resource)obj;
    return (new EqualsBuilder()).append(this.fh, other.fh).append(this.path, other.path).isEquals();
  }
  
  public String toString() {
    return getPath();
  }
  
  public FileHandler getFileHandler() {
    return this.fh;
  }
}
