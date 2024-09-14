package org.tizen.common.core.command.prompter;

import org.tizen.common.util.Assert;
import org.tizen.common.util.StringUtil;

public class ChoiceOption extends AbstractOption {
  protected String shortName;
  
  public ChoiceOption(String name) {
    this(name, false);
  }
  
  public ChoiceOption(String name, boolean bDefault) {
    this(name, bDefault, true);
  }
  
  public ChoiceOption(String name, boolean bDefault, boolean bPermitAbbreviation) {
    this(name, bDefault, bPermitAbbreviation, name.substring(0, 1));
    Assert.isTrue(StringUtil.hasText(name));
  }
  
  public ChoiceOption(String name, boolean bDefault, boolean bPermitAbbreviation, String shortName) {
    super(name, bDefault, bPermitAbbreviation);
    this.shortName = shortName;
  }
  
  public ChoiceOption(String name, String shortName) {
    this(name);
    this.shortName = shortName;
  }
  
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }
  
  public String getShortName() {
    return this.shortName;
  }
  
  public boolean isMatch(String value) {
    if (this.name.equalsIgnoreCase(StringUtil.trim(value)))
      return true; 
    if (StringUtil.isEmpty(value))
      return false; 
    if (this.shortName.equalsIgnoreCase(StringUtil.trim(value)))
      return true; 
    return false;
  }
}
