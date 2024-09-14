package org.tizen.common.core.command.prompter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.Assert;

public class AbstractPrompter {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected void checkOptions(ChoiceOption[] options) {
    Assert.notNull(options);
    HashSet<Option> reducedOptions = new LinkedHashSet<>();
    Option defaultOption = null;
    ArrayList<String> optionNames = new ArrayList<>();
    byte b;
    int i;
    ChoiceOption[] arrayOfChoiceOption;
    for (i = (arrayOfChoiceOption = options).length, b = 0; b < i; ) {
      Option option = arrayOfChoiceOption[b];
      if (reducedOptions.contains(option))
        throw new IllegalArgumentException(
            "Question can't have duplicated choice(s) :" + option); 
      optionNames.add(option.getName());
      reducedOptions.add(option);
      if (option.isDefault()) {
        if (defaultOption != null)
          throw new IllegalArgumentException(
              "Question must have only one default choice"); 
        defaultOption = option;
      } 
      b++;
    } 
  }
}
