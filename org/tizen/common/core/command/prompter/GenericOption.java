package org.tizen.common.core.command.prompter;

import org.tizen.common.util.Assert;
import org.tizen.common.util.StringUtil;

public class GenericOption extends AbstractOption {
  protected String answer;
  
  public GenericOption() {
    super("", false, false);
  }
  
  public GenericOption(String name) {
    super(name, false, false);
    this.answer = name;
  }
  
  public GenericOption(String name, boolean bDefault) {
    super(name, bDefault, false);
    this.answer = name;
  }
  
  public GenericOption(String name, boolean bDefault, boolean bPermitAbbreviation) {
    super(name, bDefault, bPermitAbbreviation);
    Assert.isTrue(StringUtil.hasText(name));
    this.answer = name;
  }
  
  public boolean isMatch(String value) {
    this.answer = value;
    return true;
  }
  
  public String getAnswer() {
    return this.answer;
  }
  
  public String toString() {
    if (isDefault())
      return "[" + this.answer + "]"; 
    return this.answer;
  }
}
