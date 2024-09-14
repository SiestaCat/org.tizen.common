package org.tizen.common.util;

import java.io.File;
import java.util.Properties;

public class ControlFileConfig {
  private String path;
  
  private boolean isExist;
  
  private Properties props;
  
  public ControlFileConfig(String path) {
    this.path = path;
    if (HostUtil.exists(path)) {
      this.isExist = true;
      this.props = PropertyUtil.loadProperties(path);
    } else {
      this.props = new Properties();
    } 
  }
  
  public String getPath() {
    return this.path;
  }
  
  public boolean exists() {
    return this.isExist;
  }
  
  public String getValue(String key) {
    return this.props.getProperty(key);
  }
  
  public String getValue(String key, String defaultValue) {
    return this.props.getProperty(key, defaultValue);
  }
  
  public boolean getValue(String key, boolean defaultValue) {
    String value = this.props.getProperty(key, Boolean.toString(defaultValue));
    return Boolean.parseBoolean(value);
  }
  
  public void setValue(String key, String value) {
    this.props.setProperty(key, value);
  }
  
  public void setValue(String key, boolean value) {
    this.props.setProperty(key, Boolean.toString(value));
  }
  
  public boolean store() {
    File folder = (new File(this.path)).getParentFile();
    if (folder != null && !folder.exists())
      folder.mkdirs(); 
    this.isExist = PropertyUtil.storeProperties(this.path, this.props);
    return this.isExist;
  }
}
