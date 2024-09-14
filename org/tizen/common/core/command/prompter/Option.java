package org.tizen.common.core.command.prompter;

public interface Option {
  String getName();
  
  boolean isDefault();
  
  boolean isMatch(String paramString);
}
