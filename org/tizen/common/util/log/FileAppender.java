package org.tizen.common.util.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Category;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.osgi.util.NLS;
import org.tizen.common.util.IOUtil;

public class FileAppender extends AppenderSkeleton {
  private int bufferSize;
  
  private static final LoggingEvent dummy = new LoggingEvent(null, (Category)Logger.getLogger("FileAppender"), (Priority)Level.OFF, "", null);
  
  OutputStream FILE_OUTPUT_STREAM;
  
  private FileAppenderScheduler SCHEDULER;
  
  private static final Timer TIMER = new Timer("File appender timer", true);
  
  protected String logPath;
  
  protected final OutputStream NOOP_OUTPUT_STREAM = new OutputStream() {
      public void write(int b) throws IOException {}
    };
  
  protected final FileAppenderScheduler NOOP_SCHEDULER = new FileAppenderScheduler(this) {
      public void run() {}
      
      public boolean cancel() {
        return true;
      }
    };
  
  public FileAppender(String logPath) {
    this(logPath, (Layout)new EnhancedPatternLayout("[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n"), 5000);
  }
  
  public FileAppender(String logPath, Layout layout) {
    this(logPath, layout, 5000);
  }
  
  public FileAppender(String logPath, Layout layout, int bufferSize) {
    setName("TIZEN_FILE_APPENDER");
    this.logPath = (new EnhancedPatternLayout(logPath)).format(dummy);
    this.bufferSize = bufferSize;
    setLayout(layout);
    initializeAppender();
  }
  
  public synchronized String getLogPath() {
    return this.logPath;
  }
  
  public void close() {}
  
  public boolean requiresLayout() {
    return false;
  }
  
  public synchronized String setFilePath(String path) {
    IOUtil.tryFlush(



        
        new Flushable[] { this.FILE_OUTPUT_STREAM });
    IOUtil.tryClose(new Object[] { this.FILE_OUTPUT_STREAM });
    EnhancedPatternLayout layout = new EnhancedPatternLayout(path);
    this.logPath = layout.format(dummy);
    return initializeAppender();
  }
  
  public synchronized void setBufferSize(int bufferSize) {
    IOUtil.tryFlush(


        
        new Flushable[] { this.FILE_OUTPUT_STREAM });
    IOUtil.tryClose(new Object[] { this.FILE_OUTPUT_STREAM });
    this.bufferSize = bufferSize;
    initializeAppender();
  }
  
  protected synchronized void append(LoggingEvent arg0) {
    try {
      try {
        this.FILE_OUTPUT_STREAM.write(this.layout.format(arg0).getBytes());
        if (getLayout().ignoresThrowable()) {
          String[] tss = arg0.getThrowableStrRep();
          if (tss != null) {
            byte b;
            int i;
            String[] arrayOfString;
            for (i = (arrayOfString = tss).length, b = 0; b < i; ) {
              String ts = arrayOfString[b];
              this.FILE_OUTPUT_STREAM.write((String.valueOf(ts) + "\n").getBytes());
              b++;
            } 
          } 
        } 
      } catch (IOException e) {
        LogLog.error(Messages.FileAppender_EXCEPTION_WRITING_LOG, e);
      } 
      this.SCHEDULER.cancel();
      this.SCHEDULER = new FileAppenderScheduler();
      TIMER.scheduleAtFixedRate(this.SCHEDULER, 3000L, 3000L);
    } catch (Throwable t) {
      LogLog.error(MessageFormat.format("Exception occurred while logging message: {0}", new Object[] { arg0.getMessage() }), t);
    } 
  }
  
  private synchronized String initializeAppender() {
    createLogFile(this.logPath);
    File logFile = new File(this.logPath);
    String message = "";
    if (!logFile.exists()) {
      File parent = logFile.getAbsoluteFile().getParentFile();
      if (parent != null) {
        parent.mkdirs();
        try {
          logFile.createNewFile();
        } catch (IOException e) {
          this.FILE_OUTPUT_STREAM = this.NOOP_OUTPUT_STREAM;
          this.SCHEDULER = this.NOOP_SCHEDULER;
          message = NLS.bind(Messages.FileAppender_EXCEPTION_CREATING_LOGFILE, this.logPath);
          LogLog.error(NLS.bind(Messages.FileAppender_EXCEPTION_CREATING_LOGFILE, this.logPath), e);
          return message;
        } 
      } 
    } else if (logFile.isDirectory()) {
      this.FILE_OUTPUT_STREAM = this.NOOP_OUTPUT_STREAM;
      this.SCHEDULER = this.NOOP_SCHEDULER;
      message = NLS.bind(Messages.FileAppender_EXCEPTION_DIRECTORY_EXISTING, this.logPath);
      LogLog.error(NLS.bind(Messages.FileAppender_EXCEPTION_DIRECTORY_EXISTING, this.logPath));
      return message;
    } 
    IOUtil.tryFlush(new Flushable[] { this.FILE_OUTPUT_STREAM });
    IOUtil.tryClose(new Object[] { this.FILE_OUTPUT_STREAM });
    try {
      this.FILE_OUTPUT_STREAM = new BufferedOutputStream(new FileOutputStream(this.logPath, true), this.bufferSize);
    } catch (IOException e) {
      this.FILE_OUTPUT_STREAM = this.NOOP_OUTPUT_STREAM;
      this.SCHEDULER = this.NOOP_SCHEDULER;
      message = Messages.FileAppender_EXCEPTION_CREATING_BUFFER;
      LogLog.error(Messages.FileAppender_EXCEPTION_CREATING_BUFFER, e);
      return message;
    } 
    this.SCHEDULER = new FileAppenderScheduler();
    TIMER.scheduleAtFixedRate(this.SCHEDULER, 3000L, 3000L);
    return message;
  }
  
  private void createLogFile(String logPath2) {}
  
  public class FileAppenderScheduler extends TimerTask {
    public void run() {
      synchronized (FileAppender.this) {
        try {
          FileAppender.this.FILE_OUTPUT_STREAM.flush();
        } catch (IOException e) {
          LogLog.error(Messages.FileAppender_EXCEPTION_FLUSHING_BUFFER, e);
        } 
      } 
    }
  }
}
