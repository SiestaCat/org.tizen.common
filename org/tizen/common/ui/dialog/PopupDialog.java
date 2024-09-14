package org.tizen.common.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class PopupDialog extends Dialog {
  private String title;
  
  public PopupDialog(Shell parentShell, String title) {
    super(parentShell);
    this.title = title;
  }
  
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(this.title);
  }
  
  protected void setShellStyle(int newShellStyle) {
    super.setShellStyle(newShellStyle | 0x860);
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, 0);
    GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 20;
    layout.verticalSpacing = 10;
    composite.setLayout((Layout)layout);
    composite.setLayoutData(new GridData(1808));
    return (Control)composite;
  }
  
  protected Control createButtonBar(Composite parent) {
    Canvas separator = new Canvas(parent, 0);
    separator.setBackground(parent.getDisplay().getSystemColor(18));
    GridData separatorData = new GridData(768);
    separatorData.heightHint = 1;
    separator.setLayoutData(separatorData);
    Composite composite = new Composite(parent, 0);
    composite.setBackground(parent.getBackground());
    GridLayout layout = new GridLayout();
    layout.numColumns = 0;
    layout.makeColumnsEqualWidth = true;
    layout.marginWidth = 20;
    layout.marginHeight = 18;
    layout.horizontalSpacing = 8;
    layout.verticalSpacing = 18;
    composite.setLayout((Layout)layout);
    GridData data = new GridData(132);
    composite.setLayoutData(data);
    composite.setFont(parent.getFont());
    createButtonsForButtonBar(composite);
    return (Control)composite;
  }
}
