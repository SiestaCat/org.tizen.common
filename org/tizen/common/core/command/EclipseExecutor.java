package org.tizen.common.core.command;

import org.tizen.common.Factory;
import org.tizen.common.core.command.policy.PolicyRegistry;
import org.tizen.common.file.EclipseFileHandler;
import org.tizen.common.file.FileHandler;

public class EclipseExecutor extends Executor {
  public EclipseExecutor(Prompter prompter) {
    super(new Factory<ExecutionContext>(prompter) {
          protected FileHandler fileHandler = new EclipseFileHandler();
          
          protected Factory<PolicyRegistry> factory = new PolicyRegistryFactory();
          
          protected PolicyRegistry getRegistry() {
            return this.factory.create();
          }
          
          public ExecutionContext create() {
            return new ExecutionContext(
                getRegistry(), 
                prompter, 
                this.fileHandler);
          }
        });
  }
}
