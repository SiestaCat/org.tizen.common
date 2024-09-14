package org.tizen.common.core.command.policy;

import org.tizen.common.core.command.Policy;

public class SimplePolicy implements Policy {
  protected String name;
  
  public SimplePolicy(String name) {
    this.name = name;
  }
  
  public <T> T adapt(Class<T> clazz) {
    return null;
  }
  
  public String getName() {
    return this.name;
  }
}
