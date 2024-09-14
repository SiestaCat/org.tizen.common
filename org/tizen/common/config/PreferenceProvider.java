package org.tizen.common.config;

import java.util.Collection;

public interface PreferenceProvider {
  Collection<String> keys();
  
  String get(String paramString);
  
  void set(String paramString1, String paramString2);
}
