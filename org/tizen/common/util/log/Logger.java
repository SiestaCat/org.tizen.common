package org.tizen.common.util.log;

import java.text.MessageFormat;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.tizen.common.CommonPlugin;
import org.tizen.common.ui.view.console.ConsoleManager;

public class Logger {
  private static String loggerName = Logger.class.getName();
  
  public static void log(IStatus status) {
    CommonPlugin plugin = CommonPlugin.getDefault();
    if (plugin != null) {
      plugin.getLog().log(status);
    } else {
      ConsoleManager conManager = new ConsoleManager("Error Log", false);
      conManager.println(String.valueOf(status.getPlugin()) + " : " + status.getMessage());
    } 
  }
  
  private static String getCallerName() {
    StackTraceElement[] stack = (new Throwable()).getStackTrace();
    int ix = 0;
    while (ix < stack.length) {
      StackTraceElement frame = stack[ix];
      String cname = frame.getClassName();
      if (!cname.equals(loggerName))
        return cname; 
      ix++;
    } 
    return "noname";
  }
  
  public static void log(Throwable e) {
    if (e instanceof CoreException) {
      log((IStatus)new Status(4, getCallerName(), ((CoreException)e).getStatus().getSeverity(), e.getMessage(), e.getCause()));
    } else {
      log((IStatus)new Status(4, getCallerName(), e.toString(), e));
    } 
  }
  
  public static void info(String message, Object... arguments) {
    log((IStatus)new Status(1, getCallerName(), getPossiblyFormattedString(message, arguments)));
  }
  
  public static void error(Object message, Throwable t) {
    log((IStatus)new Status(4, getCallerName(), message.toString(), t));
  }
  
  public static void error(String message, Object... arguments) {
    log((IStatus)new Status(4, getCallerName(), getPossiblyFormattedString(message, arguments)));
  }
  
  public static void warning(String message, Object... arguments) {
    log((IStatus)new Status(2, getCallerName(), getPossiblyFormattedString(message, arguments)));
  }
  
  private static String getPossiblyFormattedString(String message, Object... arguments) {
    return (arguments.length > 0) ? MessageFormat.format(message, arguments) : 
      message;
  }
  
  public static void debug(String message, Object... args) {}
}
