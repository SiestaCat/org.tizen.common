package org.tizen.common.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommand<T> implements Command<T> {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  
  public static final String COMMAND_CANCEL = "cancel";
  
  protected T result;
  
  public abstract void run(Executor paramExecutor, ExecutionContext paramExecutionContext) throws Exception;
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {
    throw new UnsupportedOperationException();
  }
  
  public T getResult() {
    return this.result;
  }
}
