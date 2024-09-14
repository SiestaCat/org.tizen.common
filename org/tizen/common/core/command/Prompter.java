package org.tizen.common.core.command;

import java.util.Collection;
import java.util.Map;
import org.tizen.common.core.command.prompter.Option;

public interface Prompter {
  Option interact(String paramString, Option... paramVarArgs);
  
  Object password(String paramString);
  
  void error(String paramString);
  
  void notify(String paramString);
  
  void cancel();
  
  void batch(Collection<UserField> paramCollection, Map<String, Object> paramMap);
}
