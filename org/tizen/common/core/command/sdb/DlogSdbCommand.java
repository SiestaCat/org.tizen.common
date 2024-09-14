package org.tizen.common.core.command.sdb;

import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;

public class DlogSdbCommand extends ShellSdbCommand {
  protected String DLOG_COMMAND = "dlogutil";
  
  public void setFilter(String filter) {
    this.DLOG_COMMAND = "dlogutil " + filter;
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    setCommand(this.DLOG_COMMAND);
    super.run(executor, context);
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {}
}
