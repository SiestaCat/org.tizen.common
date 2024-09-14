package org.tizen.common.core.command.prompter;

import java.util.Collection;
import java.util.Map;
import org.tizen.common.core.command.Prompter;
import org.tizen.common.core.command.UserField;

public class NopPrompter extends AbstractPrompter implements Prompter {
  protected char[] password;
  
  public Option interact(String question, Option... options) {
    byte b;
    int i;
    Option[] arrayOfOption;
    for (i = (arrayOfOption = options).length, b = 0; b < i; ) {
      Option option = arrayOfOption[b];
      if (option.isDefault())
        return option; 
      b++;
    } 
    throw new IllegalArgumentException();
  }
  
  public void notify(String message) {
    this.logger.info("Notification >>> {}", message);
  }
  
  public void cancel() {}
  
  public Object password(String message) {
    return this.password;
  }
  
  public void error(String message) {
    this.logger.error("Error >>> {}", message);
  }
  
  public void setPassword(char[] password) {
    this.password = password;
  }
  
  public void batch(Collection<UserField> userFields, Map<String, Object> options) {}
}
