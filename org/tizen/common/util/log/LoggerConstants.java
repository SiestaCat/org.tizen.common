package org.tizen.common.util.log;

public class LoggerConstants {
  public static final String DEFAULT_CONVERSION_PATTERN = "[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n";
  
  public static final int DEFAULT_BUFFER_SIZE = 5000;
  
  public static final long DEFAULT_TIMER_PERIOD = 3000L;
  
  public static final String KEY_WORKSPACE = "workspace";
  
  public static final String KEY_SDK_HOME = "tizensdk";
  
  public static final String KEY_SDK_USER_DATA_LOG = "userdata_log";
  
  public static final String NAME_FILE_APPENDER = "TIZEN_FILE_APPENDER";
  
  public static final String NAME_ECLIPSE_APPENDER = "TIZEN_ECLIPSE_APPENDER";
  
  public static final String NAME_GOOGLE_APPENDER = "TIZEN_GOOGLE_APPENDER";
  
  public static final String NAME_ASYNC_APPENDER = "TIZEN_ASYNC_APPENDER";
  
  public static final String NAME_USER_LOGGER = "UserLogger";
  
  public static final String DEFAULT_LOG_LOCATION = "%X{userdata_log}/ide-%d{yyyyMMdd}_%d{HHmmss}.log";
}
