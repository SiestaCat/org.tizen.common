package org.tizen.common.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.tizen.common.util.StringUtil;
import org.tizen.common.util.ThreadLocalMap;

public class UserLogger {
  protected static ThreadLocalMap<String, Long> id2perform = new ThreadLocalMap<>();
  
  private static final String FQCN = UserLogger.class.getName();
  
  private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  
  private static Logger logger = Logger.getLogger("UserLogger");
  
  public static void page(String category) {
    page(category, null);
  }
  
  public static void page(String category, Throwable t) {
    Page message = new Page(category);
    logger.log(FQCN, (Priority)Level.PAGE, message, t);
  }
  
  public static void event(String category) {
    event(category, "", null);
  }
  
  public static void event(String category, String action) {
    event(category, action, null);
  }
  
  public static void event(String category, String action, Throwable t) {
    Event message = new Event(category, action);
    logger.log(FQCN, (Priority)Level.EVENT, message, t);
  }
  
  public static void start(String category) {
    start(category, "");
  }
  
  public static void start(String category, String variableName) {
    long start = System.currentTimeMillis();
    id2perform.put(String.valueOf(category) + variableName, Long.valueOf(start));
    PerformanceInfo message = new PerformanceInfo(category, start, 0L, variableName);
    logger.log(FQCN, (Priority)Level.PERFORM_START, message, null);
  }
  
  public static void end(String category) {
    end(category, "");
  }
  
  public static void end(String category, String variableName) {
    long end = System.currentTimeMillis();
    Long start = id2perform.remove(String.valueOf(category) + variableName);
    if (start == null)
      return; 
    PerformanceInfo message = new PerformanceInfo(category, start.longValue(), end, variableName);
    logger.log(FQCN, (Priority)Level.PERFORM_END, message, null);
  }
  
  public static class PerformanceInfo {
    private final String category;
    
    private final long start;
    
    private final long end;
    
    private final String variableName;
    
    private final String message;
    
    public PerformanceInfo(String category, long start, long end) {
      this(category, start, end, null);
    }
    
    public PerformanceInfo(String category, long start, long end, String variableName) {
      this.category = category;
      this.start = start;
      this.end = end;
      this.variableName = variableName;
      if (end == 0L) {
        if (StringUtil.isEmpty(variableName)) {
          this.message = String.format("[Category: %s] at %s", new Object[] { category, UserLogger.access$0().format(new Date(start)) });
        } else {
          this.message = String.format("[Category: %s\tVariable: %s] at %s", new Object[] { category, variableName, UserLogger.access$0().format(new Date(start)) });
        } 
      } else if (StringUtil.isEmpty(variableName)) {
        this.message = String.format("[Category: %s] at %s \t [%s(ms)]", new Object[] { category, UserLogger.access$0().format(new Date(end)), getPerformanceString() });
      } else {
        this.message = String.format("[Category: %s\tVariable: %s] at %s \t [%s(ms)]", new Object[] { category, variableName, UserLogger.access$0().format(new Date(end)), getPerformanceString() });
      } 
    }
    
    public String toString() {
      return this.message;
    }
    
    public String getCategory() {
      return this.category;
    }
    
    public String getVariableName() {
      return this.variableName;
    }
    
    public String getPerformanceString() {
      return String.format("%d", new Object[] { Long.valueOf(this.end - this.start) });
    }
  }
  
  public static class Event {
    private final String category;
    
    private final String action;
    
    private final String message;
    
    public Event(String category) {
      this(category, null);
    }
    
    public Event(String category, String action) {
      this.category = category;
      this.action = action;
      if (StringUtil.isEmpty(action)) {
        this.message = String.format(
            "[Category: %s]", new Object[] { category });
      } else {
        this.message = String.format(
            "[Category: %s\tAction: %s]", new Object[] { category, 
              action });
      } 
    }
    
    public String getCategory() {
      return this.category;
    }
    
    public String getAction() {
      return this.action;
    }
    
    public String toString() {
      return this.message;
    }
  }
  
  public static class Page {
    private final String category;
    
    private final String message;
    
    public Page(String category) {
      this.category = category;
      this.message = String.format("[Category: %s]", new Object[] { this.category });
    }
    
    public String getCategory() {
      return this.category;
    }
    
    public String toString() {
      return this.message;
    }
  }
}
