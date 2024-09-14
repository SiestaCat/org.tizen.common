package org.tizen.common.ui.dialog;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.tizen.common.util.SWTUtil;

public class PasswordInputDialog extends InputDialog {
  public PasswordInputDialog(String dialogTitle, String dialogMessage) {
    this(SWTUtil.getActiveShell(), dialogTitle, dialogMessage);
  }
  
  public PasswordInputDialog(Shell parentShell, String dialogTitle, String dialogMessage) {
    this(parentShell, dialogTitle, dialogMessage, null);
  }
  
  public PasswordInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, IInputValidator validator) {
    super(parentShell, dialogTitle, dialogMessage, "", validator);
  }
  
  protected int getInputTextStyle() {
    return 4196356;
  }
}
