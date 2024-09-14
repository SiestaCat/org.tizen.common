package org.tizen.common.file;

import java.io.IOException;
import java.io.InputStream;

public interface IResource {
  String getName();
  
  String getPath();
  
  FileHandler getFileHandler();
  
  InputStream getContents() throws IOException;
  
  void setContents(InputStream paramInputStream) throws IOException;
  
  void setContents(byte[] paramArrayOfbyte) throws IOException;
}
