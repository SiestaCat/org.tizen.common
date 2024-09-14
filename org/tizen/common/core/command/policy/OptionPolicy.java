package org.tizen.common.core.command.policy;

import org.tizen.common.core.command.prompter.Option;

public class OptionPolicy {
  protected Option[] options;
  
  public OptionPolicy(Option... options) {
    this.options = options;
  }
  
  public Option[] getOptions() {
    return this.options;
  }
}
