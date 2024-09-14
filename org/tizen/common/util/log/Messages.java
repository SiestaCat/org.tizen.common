package org.tizen.common.util.log;

import org.eclipse.osgi.util.NLS;

public class Messages {
  private static final String BUNDLE_NAME = "org.tizen.common.util.log.messages";
  
  public static String UserLogger_EVENT_STRING;
  
  public static String FileAppender_EXCEPTION_FLUSHING_BUFFER;
  
  public static String FileAppender_EXCEPTION_CREATING_BUFFER;
  
  public static String FileAppender_EXCEPTION_CREATING_LOGFILE;
  
  public static String FileAppender_EXCEPTION_WRITING_LOG;
  
  public static String FileAppender_EXCEPTION_DIRECTORY_EXISTING;
  
  public static String LOGGER_OFF_DES;
  
  public static String LOGGER_ERROR_DES;
  
  public static String LOGGER_INFO_DES;
  
  public static String LOGGER_DEBUG_DES;
  
  public static String LOGGER_ALL_DES;
  
  static {
    NLS.initializeMessages("org.tizen.common.util.log.messages", Messages.class);
  }
}
