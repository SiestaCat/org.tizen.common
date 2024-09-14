package org.tizen.common.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ViewUtil {
  public static void showView(String id, boolean sync) {
    ViewRunnable runnable = new ViewRunnable(id);
    if (sync) {
      SWTUtil.syncExec(runnable);
    } else {
      SWTUtil.asyncExec(runnable);
    } 
  }
  
  static class ViewRunnable implements Runnable {
    String id;
    
    public ViewRunnable(String id) {
      this.id = id;
    }
    
    public void run() {
      IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
      if (window == null)
        return; 
      try {
        window.getActivePage().showView(this.id);
      } catch (PartInitException e) {
        MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", "showView failed: " + e.getMessage());
      } 
    }
  }
  
  public static IViewPart getView(String id) {
    return getView(id, true);
  }
  
  public static IViewPart getView(String id, boolean show) {
    IWorkbenchWindow window = getWorkbenchWindow();
    if (window == null)
      return null; 
    IWorkbenchPage activeWorkbenchPage = window.getActivePage();
    if (activeWorkbenchPage == null)
      return null; 
    IViewPart view = activeWorkbenchPage.findView(id);
    if (view != null && show)
      showView(id, true); 
    return view;
  }
  
  public static IWorkbenchWindow getWorkbenchWindow() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    if (window != null)
      return window; 
    if (workbench.getWorkbenchWindowCount() == 0)
      return null; 
    return workbench.getWorkbenchWindows()[0];
  }
}
