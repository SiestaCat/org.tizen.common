package org.tizen.common.core.command;

public interface Command<T> {
  void run(Executor paramExecutor, ExecutionContext paramExecutionContext) throws Exception;
  
  void undo(Executor paramExecutor, ExecutionContext paramExecutionContext) throws Exception;
  
  T getResult();
}
