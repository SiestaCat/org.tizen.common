package org.tizen.common.core.command.policy;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.CommandCancelException;

public enum UncaughtExceptionHandlingPolicy implements Thread.UncaughtExceptionHandler {
  INSTANCE;
  
  private final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandlingPolicy.class);
  
  private final Map<String, Thread.UncaughtExceptionHandler> repository = new HashMap<>();
  
  public void uncaughtException(Thread t, Throwable e) {
    Thread.UncaughtExceptionHandler handler = null;
    Class<? extends Object> throwableClass = (Class)e.getClass();
    do {
      handler = this.repository.get(throwableClass.getCanonicalName());
      if (handler != null) {
        handler.uncaughtException(t, e);
        return;
      } 
      throwableClass = (Class)throwableClass.getSuperclass();
    } while (!throwableClass.getCanonicalName().equals(Object.class.getCanonicalName()));
    throw new IllegalStateException(e);
  }
  
  UncaughtExceptionHandlingPolicy() {
    installUncaughtExceptionHandlers();
  }
  
  private void installUncaughtExceptionHandlers() {
    putHandler(CommandCancelException.class.getCanonicalName(), new Thread.UncaughtExceptionHandler() {
          public void uncaughtException(Thread t, Throwable e) {
            if (e instanceof CommandCancelException)
              throw (CommandCancelException)e; 
            throw new IllegalStateException();
          }
        });
  }
  
  private void putHandler(String throwableName, Thread.UncaughtExceptionHandler handler) {
    if (this.repository.containsKey(throwableName)) {
      if (!((Thread.UncaughtExceptionHandler)this.repository.get(throwableName)).equals(handler))
        this.logger.error("Handlers are duplicately assigned with one key: " + throwableName); 
      return;
    } 
    this.repository.put(throwableName, handler);
  }
}
