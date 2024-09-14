package org.tizen.common.rds;

import org.eclipse.osgi.util.NLS;

public class RdsMessages {
  public static String RDS_PUSH_LOG;
  
  public static String RDS_PUSH_ERROR;
  
  public static String RDS_DELETE_ERROR;
  
  public static String RDS_RES_INFO_PUSH_ERROR;
  
  public static String RDS_MODE_DISABLED;
  
  public static String RDS_MODE_ENABLED;
  
  public static String RDS_MODE_PREFIX;
  
  public static String CANNOT_FIND_APPLICATION;
  
  public static String CANNOT_FIND_RDS_INFO;
  
  public static String CANNOT_FIND_DELTA;
  
  public static String CANNOT_INSTALL;
  
  public static String CANNOT_PARTIALLY_INSTALL;
  
  static {
    NLS.initializeMessages(RdsMessages.class.getName(), RdsMessages.class);
  }
}
