package org.tizen.common.core.command;

import org.tizen.common.Factory;
import org.tizen.common.core.command.policy.AbstractPolicy;
import org.tizen.common.core.command.policy.FilePolicy;
import org.tizen.common.core.command.policy.MessagePolicy;
import org.tizen.common.core.command.policy.OptionPolicy;
import org.tizen.common.core.command.policy.PolicyRegistry;
import org.tizen.common.core.command.policy.SimplePolicy;
import org.tizen.common.core.command.policy.UncaughtExceptionHandlingPolicy;
import org.tizen.common.core.command.prompter.FileHandlingOption;
import org.tizen.common.core.command.prompter.Option;

public class PolicyRegistryFactory implements Factory<PolicyRegistry> {
  class EclipsePolicy extends AbstractPolicy {
    public EclipsePolicy(String name) {
      super(name);
    }
    
    public <T> T adapt(Class<T> clazz) {
      if (clazz.isAssignableFrom(FilePolicy.class))
        return (T)FilePolicy.STOP_PROCESS; 
      if (clazz.isAssignableFrom(MessagePolicy.class))
        return (T)MessagePolicy.PROMPTER; 
      if (clazz.isAssignableFrom(UncaughtExceptionHandlingPolicy.class))
        return (T)UncaughtExceptionHandlingPolicy.INSTANCE; 
      return null;
    }
  }
  
  public PolicyRegistry create() {
    PolicyRegistry registry = new PolicyRegistry(new Policy[0]);
    registry.register(new Policy[] { new EclipsePolicy(this, "exist.file.when.copy") {
            public <T> T adapt(Class<T> clazz) {
              if (clazz.isAssignableFrom(OptionPolicy.class))
                return (T)new OptionPolicy(new Option[] { FileHandlingOption.OVERWRITE, FileHandlingOption.IGNORE, FileHandlingOption.OVERWRITE_ALL, FileHandlingOption.IGNORE_ALL, FileHandlingOption.CANCEL }); 
              return super.adapt(clazz);
            }
          } });
    registry.register(new Policy[] { new EclipsePolicy(this, "exception.unhandled") {
            public <T> T adapt(Class<T> clazz) {
              if (clazz.isAssignableFrom(UncaughtExceptionHandlingPolicy.class))
                return (T)UncaughtExceptionHandlingPolicy.INSTANCE; 
              return null;
            }
          } });
    registry.register(new Policy[] { new EclipsePolicy("exist.file.out") });
    registry.register(new Policy[] { new EclipsePolicy("nonexist.file.in") });
    registry.register(new Policy[] { new SimplePolicy("printout.result.signing") });
    registry.register(new Policy[] { new EclipsePolicy("nonexist.dir.in.project") });
    return registry;
  }
}
