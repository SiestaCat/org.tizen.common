package org.tizen.common.util;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;

public class AdapterUtil {
  public static Object getAdapter(IAdaptable adaptable, Class adapterType) {
    IAdapterManager adapterManager = Platform.getAdapterManager();
    int adapterStatus = adapterManager.queryAdapter(adaptable, adapterType.getName());
    if (1 == adapterStatus)
      adapterManager.loadAdapter(adaptable, adapterType.getName()); 
    return adaptable.getAdapter(adapterType);
  }
}
