package org.tizen.common;

import org.eclipse.core.resources.IResourceChangeEvent;

public interface ITizenLocalizer {
  void checkFirstImpression();
  
  void updateLocalizationContainer();
  
  void updateLocalizedString();
  
  String[][] getSupportLangTags();
  
  boolean isValidKey(String paramString);
  
  int resourceChanged(IResourceChangeEvent paramIResourceChangeEvent);
}
