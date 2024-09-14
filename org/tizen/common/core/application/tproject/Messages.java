package org.tizen.common.core.application.tproject;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "org.tizen.common.core.application.tproject.messages";
  
  public static String TPROJECTHANDLER_ERR;
  
  static {
    NLS.initializeMessages("org.tizen.common.core.application.tproject.messages", Messages.class);
  }
}
