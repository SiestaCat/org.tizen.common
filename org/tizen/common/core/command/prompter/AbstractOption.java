package org.tizen.common.core.command.prompter;

import org.tizen.common.util.ObjectUtil;
import org.tizen.common.util.StringUtil;

public class AbstractOption implements Option {
  protected final String name;
  
  protected final boolean bPermitAbbreviation;
  
  protected final boolean bDefault;
  
  public AbstractOption(String name, boolean bDefault, boolean bPermitAbbreviation) {
    this.name = StringUtil.trim(name);
    this.bDefault = bDefault;
    this.bPermitAbbreviation = bPermitAbbreviation;
  }
  
  public String getName() {
    return this.name;
  }
  
  public boolean isDefault() {
    return this.bDefault;
  }
  
  public boolean isMatch(String value) {
    if (this.name.equalsIgnoreCase(StringUtil.trim(value)))
      return true; 
    if (StringUtil.isEmpty(value))
      return false; 
    if (this.bPermitAbbreviation)
      return this.name.substring(0, 1).equalsIgnoreCase(value.substring(0, 1)); 
    return false;
  }
  
  public int hashCode() {
    return this.name.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof AbstractOption))
      return false; 
    AbstractOption other = (AbstractOption)obj;
    return ObjectUtil.equals(this.name, other.name);
  }
  
  public String toString() {
    if (isDefault())
      return "[" + this.name + "]"; 
    return this.name;
  }
}
