package org.tizen.common.core.application;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = Messages.class.getName();
  
  public static String FileExistDialogTitle;
  
  public static String FileExistMsg;
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
