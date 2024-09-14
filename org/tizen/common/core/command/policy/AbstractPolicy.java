package org.tizen.common.core.command.policy;

import org.tizen.common.core.command.Policy;

public abstract class AbstractPolicy implements Policy {
  protected final String name;
  
  public AbstractPolicy(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
}
