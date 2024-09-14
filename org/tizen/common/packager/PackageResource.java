package org.tizen.common.packager;

import org.tizen.common.file.FileHandler;
import org.tizen.common.file.Resource;

public class PackageResource extends Resource {
  private String destPath;
  
  public PackageResource(FileHandler fh, String path, String dest) {
    super(fh, path);
    this.destPath = dest;
  }
  
  public String getDestination() {
    return this.destPath;
  }
  
  public String toString() {
    return String.valueOf(super.toString()) + " [" + getDestination() + "]";
  }
  
  public String setDestination(String path) {
    return this.destPath = path;
  }
}
