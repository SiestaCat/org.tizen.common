package org.tizen.common.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.Factory;

public class Executor {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  
  protected Factory<ExecutionContext> contextFactory;
  
  protected ThreadLocal<ExecutionContext> contexts = new ThreadLocal<ExecutionContext>() {
      protected ExecutionContext initialValue() {
        if (Executor.this.contextFactory == null)
          return null; 
        return Executor.this.contextFactory.create();
      }
    };
  
  public Executor() {}
  
  public Executor(Factory<ExecutionContext> factory) {
    setContextFactory(factory);
  }
  
  public Factory<ExecutionContext> getContextFactory() {
    return this.contextFactory;
  }
  
  public void setContextFactory(Factory<ExecutionContext> factory) {
    this.logger.trace("ExecutionContextFactory :{}", factory);
    this.contextFactory = factory;
  }
  
  public void execute(Command... commands) {
    this.logger.trace("Commands :{}", (Object[])commands);
    this.contexts.remove();
    execute(this.contexts.get(), (Command<?>[])commands);
  }
  
  public void execute(ExecutionContext context, Command... commands) {
    try {
      byte b;
      int i;
      Command[] arrayOfCommand;
      for (i = (arrayOfCommand = commands).length, b = 0; b < i; ) {
        Command<?> command = arrayOfCommand[b];
        command.run(this, context);
        b++;
      } 
    } catch (Exception e) {
      Policy policy = context.getPolicy("exception.unhandled");
      if (policy == null) {
        this.logger.error("Error occured", e);
        return;
      } 
      Thread.UncaughtExceptionHandler handler = policy.<Thread.UncaughtExceptionHandler>adapt(Thread.UncaughtExceptionHandler.class);
      if (handler == null)
        throw new IllegalStateException(e); 
      handler.uncaughtException(Thread.currentThread(), e);
    } finally {
      this.contexts.remove();
    } 
  }
  
  public void run(Command<?> command, ExecutionContext context) throws Exception {
    command.run(this, context);
  }
  
  public ExecutionContext getContext() {
    return this.contexts.get();
  }
}
