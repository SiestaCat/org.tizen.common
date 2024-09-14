package org.tizen.common.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;
import org.tizen.common.CommonPlugin;
import org.tizen.common.util.ViewUtil;

public class ShowQuestionToggleDialog implements Runnable {
  private boolean wantToContinue = false;
  
  private String toggleMessage;
  
  private String togglePreferenceKey;
  
  private String message;
  
  private String title;
  
  public ShowQuestionToggleDialog(String title, String message, String toggleMessage, String togglePreferenceKey) {
    this.title = title;
    this.message = message;
    this.togglePreferenceKey = togglePreferenceKey;
    this.toggleMessage = toggleMessage;
  }
  
  public void run() {
    IPreferenceStore ps = CommonPlugin.getDefault().getPreferenceStore();
    boolean dontShowDialog = ps.getBoolean(this.togglePreferenceKey);
    if (dontShowDialog) {
      this.wantToContinue = true;
      return;
    } 
    IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
    if (window == null) {
      this.wantToContinue = true;
      return;
    } 
    MessageDialogWithToggle dialog = MessageDialogWithToggle.open(3, 
        window.getShell(), 
        this.title, 
        this.message, 
        this.toggleMessage, 
        false, 
        ps, 
        this.togglePreferenceKey, 
        0);
    if (dialog.getReturnCode() == 2) {
      this.wantToContinue = true;
      ps.setValue(this.togglePreferenceKey, dialog.getToggleState());
    } 
  }
  
  public boolean wantToContinue() {
    return this.wantToContinue;
  }
}
