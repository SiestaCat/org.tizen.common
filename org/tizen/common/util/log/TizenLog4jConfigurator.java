package org.tizen.common.util.log;

import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.Appender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.ui.page.preference.LoggingPreferencePage;

public class TizenLog4jConfigurator extends DOMConfigurator implements IPropertyChangeListener {
  private static final EnhancedPatternLayout layout = new EnhancedPatternLayout();
  
  public void doConfigure(InputStream arg0, LoggerRepository arg1) {}
  
  public void doConfigure(URL arg0, LoggerRepository arg1) {
    arg1.resetConfiguration();
    layout.setConversionPattern(LoggingPreferencePage.getLogConversionPattern());
    configRootLogger(arg1);
  }
  
  private void configRootLogger(LoggerRepository arg1) {
    Logger rootLogger = arg1.getRootLogger();
    rootLogger.removeAllAppenders();
    rootLogger.setLevel(LoggingPreferencePage.getLoggerLevel());
    layout.setConversionPattern(LoggingPreferencePage.getLogConversionPattern());
    MDC.put("workspace", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
    MDC.put("tizensdk", InstallPathConfig.getSDKPath());
    MDC.put("userdata_log", InstallPathConfig.getIdeUserDataLogPath());
    FileAppender fileAppender = new FileAppender(LoggingPreferencePage.getLogLocation(), (Layout)layout);
    rootLogger.addAppender((Appender)fileAppender);
    MDC.remove("workspace");
    MDC.remove("tizensdk");
    MDC.remove("userdata_log");
    EclipseAppender eclipseAppender = new EclipseAppender((Layout)layout);
    rootLogger.addAppender((Appender)eclipseAppender);
  }
  
  public void propertyChange(PropertyChangeEvent event) {
    String property = event.getProperty();
    Logger rootLogger = Logger.getRootLogger();
    if (property.equals("org.tizen.common.logger.level")) {
      String loggerLevel = (String)event.getNewValue();
      Level level = LoggingPreferencePage.getLoggerLevel(loggerLevel);
      rootLogger.setLevel(level);
    } else if (property.equals("org.tizen.common.logger.cp")) {
      String cp = (String)event.getNewValue();
      layout.setConversionPattern(cp);
    } 
  }
}
