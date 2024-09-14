package org.tizen.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface FileHandler {
  String getCurrentWorkingDirectory();
  
  void setCurrentWorkingDirectory(String paramString);
  
  void makeDirectory(String paramString) throws IOException;
  
  void makeDirectory(String paramString, boolean paramBoolean) throws IOException;
  
  void moveDirectory(String paramString1, String paramString2) throws IOException;
  
  void copyDirectory(String paramString1, String paramString2) throws IOException;
  
  void removeDirectory(String paramString) throws IOException;
  
  Collection<String> list(String paramString) throws IOException;
  
  void write(String paramString, InputStream paramInputStream) throws IOException;
  
  InputStream read(String paramString) throws IOException;
  
  void moveFile(String paramString1, String paramString2) throws IOException;
  
  void copyFile(String paramString1, String paramString2) throws IOException;
  
  void removeFile(String paramString) throws IOException;
  
  Object get(String paramString, Attribute paramAttribute) throws IOException;
  
  boolean is(String paramString, Attribute paramAttribute) throws IOException;
  
  void set(String paramString, Attribute paramAttribute, Object paramObject) throws IOException;
  
  public enum Attribute {
    CWD, PATH, NAME, TYPE, EXISTS, READABLE, WRITABLE, HIDDEN, READONLY, MODIFIED, SIZE, URI, URL, QUALIFIED, CUSTOM;
  }
  
  public enum Type {
    FILE, DIRECTORY, UNKNOWN;
  }
}
