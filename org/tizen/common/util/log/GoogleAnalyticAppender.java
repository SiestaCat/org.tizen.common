package org.tizen.common.util.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.ObjectUtil;

public class GoogleAnalyticAppender extends AppenderSkeleton {
  private boolean isUsage = false;
  
  private boolean isPerform = false;
  
  static final String URL_GOOGLE_ANALYTIC = "http://www.google-analytics.com/collect";
  
  private static final int MAX_STACK_TRACE = 2;
  
  private static final String KEY_ANALYTIC_VER = "v";
  
  private static final String KEY_TRACKING_ID = "tid";
  
  private static final String KEY_CLIENT_ID = "cid";
  
  private static final String KEY_HIT_TYPE = "t";
  
  private static final String KEY_APP_NAME = "an";
  
  private static final String KEY_APP_VER = "av";
  
  private static final String KEY_CONTENT_DES = "cd";
  
  private static final String KEY_EVENT_CATEGORY = "ec";
  
  private static final String KEY_EVENT_ACTION = "ea";
  
  private static final String KEY_PERFORM_CATEGORY = "utc";
  
  private static final String KEY_PERFORM_VARIABLE = "utv";
  
  private static final String KEY_PERFORM_TIME = "utt";
  
  private static final String KEY_EXCEPTION = "exd";
  
  private static final String KEY_FATAL = "exf";
  
  private static final String VALUE_ANALYTIC_VER = "1";
  
  private static final String VALUE_TRACKING_ID = "UA-33537119-1";
  
  private static final String VALUE_CLIENT_ID = ObjectUtil.generateGUID(GoogleAnalyticAppender.class);
  
  private static final String VALUE_APP_NAME = "tizen-ide";
  
  private static final String VALUE_APP_VER = "dev";
  
  private static final String VALUE_HIT_TYPE_TIMING = "timing";
  
  private static final String VALUE_HIT_TYPE_EVENT = "event";
  
  private static final String VALUE_HIT_TYPE_APP = "appview";
  
  int rc;
  
  public GoogleAnalyticAppender(boolean isUsage, boolean isPerform) {
    this(isUsage, isPerform, (Layout)new EnhancedPatternLayout("[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n"));
  }
  
  public GoogleAnalyticAppender(boolean isUsage, boolean isPerform, Layout layout) {
    setName("TIZEN_GOOGLE_APPENDER");
    this.isUsage = isUsage;
    this.isPerform = isPerform;
    setLayout(layout);
  }
  
  public void setUsage(boolean isUsage) {
    this.isUsage = isUsage;
  }
  
  public void setPerform(boolean isPerform) {
    this.isPerform = isPerform;
  }
  
  public void close() {}
  
  public boolean requiresLayout() {
    return false;
  }
  
  protected String assembly(String key, String value) throws UnsupportedEncodingException {
    return String.valueOf(URLEncoder.encode(key, "utf-8")) + "=" + URLEncoder.encode(value, "utf-8");
  }
  
  protected String join(String... props) {
    StringBuilder buffer = new StringBuilder(1000);
    for (int i = 0, n = props.length; i < n; i++) {
      if (i != 0)
        buffer.append("&"); 
      String prop = props[i];
      buffer.append(prop);
    } 
    return buffer.toString();
  }
  
  protected void append(LoggingEvent event) {
    try {
      this.rc = 0;
      ArrayList<String> result = new ArrayList<>();
      try {
        if (!assembleString(result, event))
          return; 
      } catch (UnsupportedEncodingException e) {
        LogLog.error("Exception occurred while creating google analytic message", e);
        return;
      } 
      String resultData = join(result.<String>toArray(new String[result.size()]));
      try {
        URL u = new URL("http://www.google-analytics.com/collect");
        HttpURLConnection con = (HttpURLConnection)u.openConnection();
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
          con.setDoOutput(true);
          out = con.getOutputStream();
          writer = new OutputStreamWriter(out);
          writer.write(resultData);
          writer.flush();
          this.rc = con.getResponseCode();
          if (200 <= this.rc && this.rc < 300)
            LogLog.debug("Send log data to google analytic"); 
        } finally {
          IOUtil.tryClose(new Object[] { writer, out });
          con.disconnect();
        } 
      } catch (MalformedURLException e) {
        LogLog.debug(String.format("Malformed URL: %s", new Object[] { "http://www.google-analytics.com/collect" }), e);
      } catch (IOException e) {
        e.printStackTrace();
        LogLog.debug("Exception occurred while transmitting google analytic data.", e);
      } 
    } catch (Throwable t) {
      LogLog.error(MessageFormat.format("Exception occurred while logging message: {0}", new Object[] { event.getMessage() }), t);
    } 
  }
  
  private boolean assembleString(ArrayList<String> result, LoggingEvent event) throws UnsupportedEncodingException {
    Level level = event.getLevel();
    if (level == Level.PERFORM_END) {
      if (!this.isPerform)
        return false; 
      UserLogger.PerformanceInfo perform = (UserLogger.PerformanceInfo)event.getMessage();
      addPerformPreVariables(result, perform);
      result.add(assembly("utt", perform.getPerformanceString()));
    } else if (level == Level.PERFORM_START) {
      if (!this.isPerform)
        return false; 
      UserLogger.PerformanceInfo perform = (UserLogger.PerformanceInfo)event.getMessage();
      addPerformPreVariables(result, perform);
    } else if (level == Level.PAGE) {
      if (!this.isUsage)
        return false; 
      addPageVariable(result, (UserLogger.Page)event.getMessage());
    } else if (level == Level.EVENT) {
      if (!this.isUsage)
        return false; 
      addEventVariable(result, (UserLogger.Event)event.getMessage());
    } else {
      result.add(assembly("t", "appview"));
    } 
    addBasicInfo(result, event);
    return true;
  }
  
  private void addPageVariable(ArrayList<String> result, UserLogger.Page page) throws UnsupportedEncodingException {
    result.add(assembly("t", "event"));
    result.add(assembly("ec", page.getCategory()));
  }
  
  private void addEventVariable(ArrayList<String> result, UserLogger.Event event) throws UnsupportedEncodingException {
    result.add(assembly("t", "event"));
    result.add(assembly("ec", event.getCategory()));
    result.add(assembly("ea", event.getAction()));
  }
  
  private void addPerformPreVariables(ArrayList<String> result, UserLogger.PerformanceInfo performanceInfo) throws UnsupportedEncodingException {
    result.add(assembly("t", "timing"));
    result.add(assembly("utc", performanceInfo.getCategory()));
    result.add(assembly("utv", performanceInfo.getVariableName()));
  }
  
  private void addBasicInfo(ArrayList<String> result, LoggingEvent event) throws UnsupportedEncodingException {
    String[] tss = event.getThrowableStrRep();
    Level level = event.getLevel();
    StringBuffer msg = new StringBuffer();
    msg.append(getLayout().format(event));
    if (tss != null) {
      if (level == Level.FATAL)
        result.add(assembly("exf", "FATAL")); 
      StringBuffer tBuffer = new StringBuffer();
      int i = 0;
      byte b;
      int j;
      String[] arrayOfString;
      for (j = (arrayOfString = tss).length, b = 0; b < j; ) {
        String ts = arrayOfString[b];
        if (2 > i + 1) {
          tBuffer.append(String.valueOf(ts) + "\n");
          i++;
          b++;
        } 
        break;
      } 
      result.add(assembly("exd", tBuffer.toString()));
      if (getLayout().ignoresThrowable())
        msg.append(tBuffer); 
    } 
    result.add(assembly("v", "1"));
    result.add(assembly("tid", "UA-33537119-1"));
    result.add(assembly("cid", VALUE_CLIENT_ID));
    result.add(assembly("an", "tizen-ide"));
    result.add(assembly("av", "dev"));
    result.add(assembly("cd", msg.toString()));
  }
}
