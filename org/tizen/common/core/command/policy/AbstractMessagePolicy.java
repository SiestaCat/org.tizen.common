package org.tizen.common.core.command.policy;

import java.text.MessageFormat;

public abstract class AbstractMessagePolicy implements MessagePolicy {
  protected String format(String format, Object... args) {
    if (format == null)
      return "<<null>>"; 
    return MessageFormat.format(format, args);
  }
}
