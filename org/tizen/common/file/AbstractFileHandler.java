package org.tizen.common.file;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.FilenameUtil;
import org.tizen.common.util.StringUtil;

public abstract class AbstractFileHandler implements FileHandler {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public boolean is(String path, FileHandler.Attribute name) throws IOException {
    Object obj = get(path, name);
    if (obj instanceof Boolean)
      return ((Boolean)obj).booleanValue(); 
    return false;
  }
  
  public void makeDirectory(String path, boolean recursive) throws IOException {
    if (StringUtil.isEmpty(path))
      return; 
    if (recursive) {
      if (is(path, FileHandler.Attribute.EXISTS))
        return; 
      String parentDir = FilenameUtil.removeTailingPath(path, 1);
      if (!is(parentDir, FileHandler.Attribute.EXISTS))
        makeDirectory(parentDir, true); 
    } 
    makeDirectory(path);
  }
  
  public String toString() {
    String cwd = getCurrentWorkingDirectory();
    return (cwd != null) ? cwd : super.toString();
  }
}
