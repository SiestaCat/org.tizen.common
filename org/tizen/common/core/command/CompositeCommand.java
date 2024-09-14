package org.tizen.common.core.command;

import java.util.ArrayList;
import java.util.Arrays;

public class CompositeCommand extends AbstractCommand<Object> {
  protected final ArrayList<Command> commands = new ArrayList<>();
  
  public CompositeCommand(Command... commands) {
    addCommand((Command<?>[])commands);
  }
  
  public void addCommand(Command... commands) {
    if (commands != null)
      this.commands.addAll(Arrays.asList(commands)); 
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    for (Command<Object> command : this.commands)
      command.run(executor, context); 
  }
}
