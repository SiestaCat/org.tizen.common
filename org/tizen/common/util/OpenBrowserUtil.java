package org.tizen.common.util;

import java.net.URL;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenBrowserUtil {
  public static void open(final URL url, final Display display) {
    display.syncExec(new Runnable() {
          public void run() {
            if (url.toString().equals("https://www.tizen.org")) {
              OpenBrowserUtil.openExternal(url, display);
            } else {
              OpenBrowserUtil.internalOpen(url, false);
            } 
          }
        });
  }
  
  public static void openExternal(final URL url, Display display) {
    display.syncExec(new Runnable() {
          public void run() {
            OpenBrowserUtil.internalOpen(url, true);
          }
        });
  }
  
  private static void internalOpen(final URL url, final boolean useExternalBrowser) {
    BusyIndicator.showWhile(null, new Runnable() {
          public void run() {
            try {
              IWebBrowser browser;
              IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
              if (useExternalBrowser) {
                browser = browserSupport.getExternalBrowser();
              } else {
                browser = browserSupport.createBrowser(null);
              } 
              browser.openURL(url);
            } catch (PartInitException ex) {
              MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", "Opening url failed: " + ex.getMessage());
            } 
          }
        });
  }
  
  public static void open(URL url, Display display, String title) {
    open(url, display);
  }
}
