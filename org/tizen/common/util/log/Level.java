package org.tizen.common.util.log;

import org.apache.log4j.Level;

public class Level extends Level {
  private static final long serialVersionUID = 5540784843364335844L;
  
  public static final Level PAGE = new Level(25000, "PAGE", 5);
  
  public static final Level EVENT = new Level(23000, "EVENT", 5);
  
  public static final Level PERFORM_START = new Level(27000, "PERFORM_S", 5);
  
  public static final Level PERFORM_END = new Level(27000, "PERFORM_E", 5);
  
  public static final Level OFF = new Level(Level.OFF, Messages.LOGGER_OFF_DES);
  
  public static final Level ERROR = new Level(Level.ERROR, Messages.LOGGER_ERROR_DES);
  
  public static final Level INFO = new Level(Level.INFO, Messages.LOGGER_INFO_DES);
  
  public static final Level DEBUG = new Level(Level.DEBUG, Messages.LOGGER_DEBUG_DES);
  
  public static final Level ALL = new Level(Level.ALL, Messages.LOGGER_ALL_DES);
  
  private String description;
  
  protected Level(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
    this.description = "";
  }
  
  protected Level(Level level, String desc) {
    this(level.toInt(), level.toString(), level.getSyslogEquivalent());
    this.description = desc;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public int hashCode() {
    return toString().hashCode();
  }
}
