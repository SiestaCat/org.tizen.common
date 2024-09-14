package org.tizen.common.launch;

import org.eclipse.osgi.util.NLS;

public class LaunchMessages extends NLS {
  public static String DIALOG_TITLE_DIFFERENT_PROFILE;
  
  public static String DIALOG_TITLE_UNKNOWN_PLATFORM;
  
  public static String DIALOG_TOGGLE_ALWAYS_LAUNCH_WITHOUT_ASKING;
  
  public static String DIALOG_QUESTION_CONTINUE_EVEN_IF_IT_HAS_ERROR;
  
  public static String DIALOG_QUESTION_CONTINUE_EVEN_IF_IT_IS_DIFFERENT_PROFILE;
  
  public static String DIALOG_QUESTION_CONTINUE_EVEN_IF_IT_IS_UNKNOWN_PLATFORM;
  
  public static String FAIL;
  
  public static String FASTDEPLOY_SIGN_FAIL;
  
  public static String SUCCESS;
  
  public static String LAUNCHED_ALREADY;
  
  public static String LAUNCH_ERROR;
  
  public static String NO_TARGET;
  
  public static String NOTIFY;
  
  public static String PLATFORM_FAIL_MESSAGE;
  
  public static String PLATFORM_SUCCESS_MESSAGE;
  
  public static String PROJECT_NOT_BUILT;
  
  public static String PROJECT_NOT_OPENED;
  
  public static String PROJECT_NOT_SELECTED;
  
  public static String PROJECT_NOT_SPECIFIED;
  
  public static String PROJECT_NOT_USABLE;
  
  public static String PROJECT_NOT_FOUND;
  
  public static String UPDATE_MODE_LABEL;
  
  public static String UPDATE_MODE_TOOLTIP;
  
  public static String CREATED_PACKAGE;
  
  public static String INSTALL_PACKAGE;
  
  public static String INSTALLED_PACKAGE;
  
  public static String UNINSTALL_PACKAGE;
  
  public static String UNINSTALLED_PACKAGE;
  
  public static String TRANSFER_PACKAGE;
  
  public static String TRANSFERRED_PACKAGE;
  
  public static String RDS_LOG_1;
  
  public static String READ_ONLY_ERROR;
  
  public static String RUN_PACKAGE;
  
  public static String LAUNCH_START;
  
  public static String LAUNCH_END;
  
  public static String LAUNCH_END_WITHOUT_RUN;
  
  public static String LAUNCH_CANCELED;
  
  public static String INCOMPATIBLE_TITLE_VERSION;
  
  public static String INCOMPATIBLE_TITLE_PROFILE;
  
  public static String INCOMPATIBLE_VERSION;
  
  public static String INCOMPATIBLE_PROFILE;
  
  public static String SDB_TIMEOUT_EXCEPTION;
  
  public static String UNKNOWN_PROFILE;
  
  public static String WATCH_APPLICATION;
  
  static {
    NLS.initializeMessages(LaunchMessages.class.getName(), LaunchMessages.class);
  }
  
  public static String getFailMessage(String s) {
    return String.format("%s > %s", new Object[] { s, FAIL });
  }
  
  public static String getSuccessMessage(String s) {
    return String.format("%s > %s", new Object[] { s, SUCCESS });
  }
  
  public static String getTimeInterval(Long start, Long end) {
    Double interval = Double.valueOf((end.longValue() - start.longValue()) / 1000.0D);
    return Double.toString(interval.doubleValue());
  }
  
  public static String getElapsedTimeMessage(Long start, Long end) {
    return "(" + getTimeInterval(start, end) + " sec)";
  }
  
  public static String getStepTitle(String step) {
    return "[" + step + "]";
  }
  
  public static String getStepMessage(String message) {
    return "    " + message;
  }
}
