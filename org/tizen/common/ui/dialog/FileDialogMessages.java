package org.tizen.common.ui.dialog;

import org.eclipse.osgi.util.NLS;

public class FileDialogMessages extends NLS {
  private static final String BUNDLE_NAME = String.valueOf(FileDialogMessages.class.getPackage().getName()) + ".FileDialogMessages";
  
  public static String FileDialog_Message_Title;
  
  public static String FileDialog_Overwrite_Message;
  
  public static String FileDialog_Duplicate_Message;
  
  public static String FileDialog_Delete_Message;
  
  public static String FileDialog_TabName_Message;
  
  public static String FILEDIALOG_DELETE_FOLDER_TITLE;
  
  public static String FILEDIALOG_DELETE_FOLDER_MESSAGE;
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, FileDialogMessages.class);
  }
}
