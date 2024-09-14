package org.tizen.common.util.log;

import java.text.MessageFormat;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.tizen.common.CommonPlugin;
import org.tizen.common.ui.view.console.ConsoleManager;

public class EclipseAppender extends AppenderSkeleton {
  public EclipseAppender() {
    this((Layout)new EnhancedPatternLayout("[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n"));
  }
  
  public EclipseAppender(Layout layout) {
    setName("TIZEN_ECLIPSE_APPENDER");
    setLayout(layout);
  }
  
  public void close() {}
  
  public boolean requiresLayout() {
    return false;
  }
  
  protected void append(LoggingEvent event) {
    try {
      Level level = event.getLevel();
      ThrowableInformation tI = event.getThrowableInformation();
      Throwable t = null;
      if (tI != null)
        t = tI.getThrowable(); 
      String message = getLayout().format(event);
      String caller = event.getLocationInformation().getClassName();
      if (level.isGreaterOrEqual((Priority)Level.OFF))
        return; 
      if (level.isGreaterOrEqual((Priority)Level.ERROR)) {
        log((IStatus)new Status(4, caller, message.toString(), t));
      } else if (level.isGreaterOrEqual((Priority)Level.WARN)) {
        log((IStatus)new Status(2, caller, message.toString(), t));
      } else if (level.isGreaterOrEqual((Priority)Level.INFO)) {
        log((IStatus)new Status(1, caller, message.toString(), t));
      } 
    } catch (Throwable t) {
      LogLog.error(MessageFormat.format("Exception occurred while logging message: {0}", new Object[] { event.getMessage() }), t);
    } 
  }
  
  private static void log(IStatus status) {
    CommonPlugin plugin = CommonPlugin.getDefault();
    if (plugin != null) {
      plugin.getLog().log(status);
    } else {
      ConsoleManager conManager = new ConsoleManager("Error Log", false);
      conManager.println(String.valueOf(status.getPlugin()) + " : " + status.getMessage());
    } 
  }
}
