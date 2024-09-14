package org.tizen.common.rds.ui.preference;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class DialogField {
  private boolean fEnabled = true;
  
  private Label fLabel = null;
  
  protected String fLabelText = "";
  
  private IDialogFieldListener fDialogFieldListener;
  
  public void setLabelText(String labeltext) {
    this.fLabelText = labeltext;
    if (isOkToUse((Control)this.fLabel))
      this.fLabel.setText(labeltext); 
  }
  
  public final void setDialogFieldListener(IDialogFieldListener listener) {
    this.fDialogFieldListener = listener;
  }
  
  public void dialogFieldChanged() {
    if (this.fDialogFieldListener != null)
      this.fDialogFieldListener.dialogFieldChanged(this); 
  }
  
  public boolean setFocus() {
    return false;
  }
  
  public void postSetFocusOnDialogField(Display display) {
    if (display != null)
      display.asyncExec(
          new Runnable() {
            public void run() {
              DialogField.this.setFocus();
            }
          }); 
  }
  
  public Control[] doFillIntoGrid(Composite parent, int nColumns) {
    assertEnoughColumns(nColumns);
    Label label = getLabelControl(parent);
    label.setLayoutData(gridDataForLabel(nColumns));
    return new Control[] { (Control)label };
  }
  
  public int getNumberOfControls() {
    return 1;
  }
  
  protected static GridData gridDataForLabel(int span) {
    GridData gd = new GridData(256);
    gd.horizontalSpan = span;
    return gd;
  }
  
  public Label getLabelControl(Composite parent) {
    if (this.fLabel == null) {
      assertCompositeNotNull(parent);
      this.fLabel = new Label(parent, 16448);
      this.fLabel.setFont(parent.getFont());
      this.fLabel.setEnabled(this.fEnabled);
      if (this.fLabelText != null && !"".equals(this.fLabelText)) {
        this.fLabel.setText(this.fLabelText);
      } else {
        this.fLabel.setText(".");
        this.fLabel.setVisible(false);
      } 
    } 
    return this.fLabel;
  }
  
  public static Control createEmptySpace(Composite parent) {
    return createEmptySpace(parent, 1);
  }
  
  public static Control createEmptySpace(Composite parent, int span) {
    Label label = new Label(parent, 16384);
    GridData gd = new GridData();
    gd.horizontalAlignment = 1;
    gd.grabExcessHorizontalSpace = false;
    gd.horizontalSpan = span;
    gd.horizontalIndent = 0;
    gd.widthHint = 0;
    gd.heightHint = 0;
    label.setLayoutData(gd);
    return (Control)label;
  }
  
  protected final boolean isOkToUse(Control control) {
    return (control != null && Display.getCurrent() != null && !control.isDisposed());
  }
  
  public final void setEnabled(boolean enabled) {
    if (enabled != this.fEnabled) {
      this.fEnabled = enabled;
      updateEnableState();
    } 
  }
  
  protected void updateEnableState() {
    if (this.fLabel != null)
      this.fLabel.setEnabled(this.fEnabled); 
  }
  
  public void refresh() {
    updateEnableState();
  }
  
  public final boolean isEnabled() {
    return this.fEnabled;
  }
  
  protected final void assertCompositeNotNull(Composite comp) {
    Assert.isNotNull(comp, "uncreated control requested with composite null");
  }
  
  protected final void assertEnoughColumns(int nColumns) {
    Assert.isTrue((nColumns >= getNumberOfControls()), "given number of columns is too small");
  }
}
