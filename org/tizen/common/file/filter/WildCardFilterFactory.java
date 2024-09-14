package org.tizen.common.file.filter;

import org.tizen.common.FactoryWithArgument;
import org.tizen.common.file.Filter;

public class WildCardFilterFactory implements FactoryWithArgument<Filter, String> {
  public Filter create(String arg) {
    WildCardFilter filter = new WildCardFilter();
    filter.setPattern(arg);
    return filter;
  }
}
