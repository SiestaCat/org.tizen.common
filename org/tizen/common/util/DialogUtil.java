package org.tizen.common.util;

import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class DialogUtil {
  public static void openMessageDialog(String message) {
    openMessageDialog(getShell(), message);
  }
  
  public static void openMessageDialog(Shell shell, String message) {
    openMessageDialog(shell, "Info", message);
  }
  
  public static void openMessageDialog(final Shell shell, final String title, final String message) {
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            MessageDialog.openInformation(shell, title, message);
          }
        });
  }
  
  @Deprecated
  public static void openProxyDialog(final String text, String message) {
    final String msg = message;
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            MessageDialog.openError(DialogUtil.getShell(), text, msg);
          }
        });
  }
  
  public static void openErrorDialog(String message) {
    openErrorDialog("Error", message);
  }
  
  public static void openErrorDialog(String title, String message) {
    openErrorDialog(getShell(), title, message);
  }
  
  public static void openErrorDialog(final Shell shell, final String title, final String message) {
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            MessageDialog.openError(shell, title, message);
          }
        });
  }
  
  private static final Object lockQuestion = new Object();
  
  public static int openQuestionDialog(String message) {
    return openQuestionDialog("Question", message);
  }
  
  public static int openQuestionDialog(String title, String message) {
    return openQuestionDialog(getShell(), title, message);
  }
  
  public static int openQuestionDialog(final Shell shell, final String title, final String message) {
    synchronized (lockQuestion) {
      final AtomicInteger retValue = new AtomicInteger();
      SWTUtil.syncExec(new Runnable() {
            public void run() {
              if (MessageDialog.openQuestion(shell, title, message)) {
                retValue.set(64);
              } else {
                retValue.set(128);
              } 
            }
          });
      return retValue.get();
    } 
  }
  
  public static Shell getActiveShell() {
    IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
    return (window != null) ? window.getShell() : null;
  }
  
  private static Shell getShell() {
    Shell shell = getActiveShell();
    if (shell == null)
      shell = new Shell(); 
    return shell;
  }
}
