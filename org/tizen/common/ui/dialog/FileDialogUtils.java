package org.tizen.common.ui.dialog;

import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

public class FileDialogUtils {
  public static final int YES_ID = 0;
  
  public static final int YES_TO_ALL_ID = 1;
  
  public static final int NO_ID = 2;
  
  public static final int CANCEL_ID = 3;
  
  public static int checkOverwrite(String filename) {
    String message = NLS.bind(FileDialogMessages.FileDialog_Overwrite_Message, filename);
    String[] labels = { IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
    int type = 3;
    return openFileDialog(labels, message, type);
  }
  
  public static int confirmDelete(String filename) {
    String message = NLS.bind(FileDialogMessages.FileDialog_Delete_Message, filename);
    String[] labels = { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
    int type = 3;
    return openFileDialog(labels, message, type);
  }
  
  public static int confirmFolderDelete(String filename) {
    String message = NLS.bind(FileDialogMessages.FILEDIALOG_DELETE_FOLDER_MESSAGE, filename);
    String[] labels = { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
    int type = 3;
    return openFileDialog(labels, FileDialogMessages.FILEDIALOG_DELETE_FOLDER_TITLE, message, type);
  }
  
  public static int confirmMultiFolderDelete(String filename) {
    String message = NLS.bind(FileDialogMessages.FILEDIALOG_DELETE_FOLDER_MESSAGE, filename);
    String[] labels = { IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
    int type = 3;
    return openFileDialog(labels, FileDialogMessages.FILEDIALOG_DELETE_FOLDER_TITLE, message, type);
  }
  
  public static int allowFileOverwrite(String filename) {
    String message = NLS.bind(FileDialogMessages.FileDialog_Overwrite_Message, filename);
    String[] labels = { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
    int type = 3;
    return openFileDialog(labels, message, type);
  }
  
  public static int checkTabName(String filename) {
    String message = NLS.bind(FileDialogMessages.FileDialog_TabName_Message, filename);
    String[] labels = { IDialogConstants.OK_LABEL };
    int type = 1;
    return openFileDialog(labels, message, type);
  }
  
  public static void notifyDuplication(String filename) {
    String message = NLS.bind(FileDialogMessages.FileDialog_Duplicate_Message, filename);
    String[] labels = { IDialogConstants.OK_LABEL };
    int type = 1;
    openFileDialog(labels, message, type);
  }
  
  public static int openFileDialog(final String[] labels, final String title, final String message, final int type) {
    final AtomicInteger resultAtomicInteger = new AtomicInteger();
    Runnable query = new Runnable() {
        public void run() {
          MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), title, null, message, type, labels, 0);
          dialog.open();
          if (dialog.getReturnCode() == -1) {
            resultAtomicInteger.set(1);
          } else {
            resultAtomicInteger.set(dialog.getReturnCode());
          } 
        }
      };
    PlatformUI.getWorkbench().getDisplay().syncExec(query);
    return resultAtomicInteger.get();
  }
  
  public static int openFileDialog(String[] labels, String message, int type) {
    return openFileDialog(labels, FileDialogMessages.FileDialog_Message_Title, message, type);
  }
}
