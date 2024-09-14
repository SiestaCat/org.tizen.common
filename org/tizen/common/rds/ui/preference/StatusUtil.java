package org.tizen.common.rds.ui.preference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;

public class StatusUtil {
  public static IStatus getMoreSevere(IStatus s1, IStatus s2) {
    if (s1.getSeverity() > s2.getSeverity())
      return s1; 
    return s2;
  }
  
  public static IStatus getMostSevere(IStatus[] status) {
    IStatus max = null;
    for (int i = 0; i < status.length; i++) {
      IStatus curr = status[i];
      if (curr.matches(4))
        return curr; 
      if (max == null || curr.getSeverity() > max.getSeverity())
        max = curr; 
    } 
    return max;
  }
  
  public static void applyToStatusLine(DialogPage page, IStatus status) {
    String message = status.getMessage();
    if (message != null && message.length() == 0)
      message = null; 
    switch (status.getSeverity()) {
      case 0:
        page.setMessage(message, 0);
        page.setErrorMessage(null);
        return;
      case 2:
        page.setMessage(message, 2);
        page.setErrorMessage(null);
        return;
      case 1:
        page.setMessage(message, 1);
        page.setErrorMessage(null);
        return;
    } 
    page.setMessage(null);
    page.setErrorMessage(message);
  }
}
