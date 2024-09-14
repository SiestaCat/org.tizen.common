package org.tizen.common.ui.view.console;

import org.eclipse.ui.console.MessageConsole;

public class MessageConsoleSpy extends MessageConsole {
  public MessageConsoleSpy(String name) {
    super(name, null);
  }
  
  public void setName(String name) {
    super.setName(name);
  }
}
