package org.tizen.common.ui.dialog;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TransferProgressMonitorDialog extends ProgressMonitorDialog {
  private Label fromLabel;
  
  private Label toLabel;
  
  private Label sizeLabel;
  
  private String titleStr = "Progress Information";
  
  public TransferProgressMonitorDialog(Shell parent) {
    super(parent);
  }
  
  public TransferProgressMonitorDialog(Shell parent, String title) {
    super(parent);
    if (title != null)
      this.titleStr = title; 
  }
  
  protected Control createDialogArea(Composite parent) {
    setMessage(this.titleStr, false);
    createMessageArea(parent);
    this.taskLabel = this.messageLabel;
    this.progressIndicator = new ProgressIndicator(parent);
    GridData gd = new GridData();
    gd.heightHint = convertVerticalDLUsToPixels(9);
    gd.horizontalAlignment = 4;
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 2;
    this.progressIndicator.setLayoutData(gd);
    this.fromLabel = new Label(parent, 16448);
    gd = new GridData(768);
    gd.horizontalSpan = 3;
    this.fromLabel.setLayoutData(gd);
    this.fromLabel.setFont(parent.getFont());
    this.toLabel = new Label(parent, 16448);
    this.toLabel.setLayoutData(gd);
    this.toLabel.setFont(parent.getFont());
    this.sizeLabel = new Label(parent, 16448);
    this.sizeLabel.setLayoutData(gd);
    this.sizeLabel.setFont(parent.getFont());
    return (Control)parent;
  }
  
  public Label getFromLabel() {
    return this.fromLabel;
  }
  
  public Label getToLabel() {
    return this.toLabel;
  }
  
  public Label getSizeLabel() {
    return this.sizeLabel;
  }
  
  private void setMessage(String messageString, boolean force) {
    this.message = (messageString == null) ? "" : messageString;
    if (this.messageLabel == null || this.messageLabel.isDisposed())
      return; 
    if (force || this.messageLabel.isVisible()) {
      this.messageLabel.setToolTipText(this.message);
      this.messageLabel.setText(shortenText(this.message, (Control)this.messageLabel));
    } 
  }
}
