package org.tizen.common.core.command.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.Prompter;

public interface MessagePolicy {
  public static final MessagePolicy LOGGING = new AbstractMessagePolicy() {
      protected final Logger logger = LoggerFactory.getLogger(getClass());
      
      public void print(Prompter prompter, String format, Object... args) {
        this.logger.debug(format(format, args));
      }
      
      public void error(Prompter prompter, String format, Object... args) {
        this.logger.error(format(format, args));
      }
    };
  
  public static final MessagePolicy PROMPTER = new AbstractMessagePolicy() {
      public void print(Prompter prompter, String format, Object... args) {
        prompter.notify(format(format, args));
      }
      
      public void error(Prompter prompter, String format, Object... args) {
        prompter.error(format(format, args));
      }
    };
  
  void print(Prompter paramPrompter, String paramString, Object... paramVarArgs);
  
  void error(Prompter paramPrompter, String paramString, Object... paramVarArgs);
}
