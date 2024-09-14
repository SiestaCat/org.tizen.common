package org.tizen.common.rds.ui.preference;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.tizen.common.util.SWTUtil;

public class SelectionButtonDialogField extends DialogField {
  private Button fButton;
  
  private boolean fIsSelected;
  
  private DialogField[] fAttachedDialogFields;
  
  private int fButtonStyle;
  
  public SelectionButtonDialogField(int buttonStyle) {
    this.fIsSelected = false;
    this.fAttachedDialogFields = null;
    this.fButtonStyle = buttonStyle;
  }
  
  public void attachDialogField(DialogField dialogField) {
    attachDialogFields(new DialogField[] { dialogField });
  }
  
  public void attachDialogFields(DialogField[] dialogFields) {
    this.fAttachedDialogFields = dialogFields;
    for (int i = 0; i < dialogFields.length; i++)
      dialogFields[i].setEnabled(this.fIsSelected); 
  }
  
  public boolean isAttached(DialogField editor) {
    if (this.fAttachedDialogFields != null)
      for (int i = 0; i < this.fAttachedDialogFields.length; i++) {
        if (this.fAttachedDialogFields[i] == editor)
          return true; 
      }  
    return false;
  }
  
  public Control[] doFillIntoGrid(Composite parent, int nColumns) {
    assertEnoughColumns(nColumns);
    Button button = getSelectionButton(parent);
    GridData gd = new GridData();
    gd.horizontalSpan = nColumns;
    gd.horizontalAlignment = 4;
    if (this.fButtonStyle == 8)
      gd.widthHint = SWTUtil.getButtonWidthHint(button); 
    button.setLayoutData(gd);
    return new Control[] { (Control)button };
  }
  
  public int getNumberOfControls() {
    return 1;
  }
  
  public Button getSelectionButton(Composite group) {
    if (this.fButton == null) {
      assertCompositeNotNull(group);
      this.fButton = new Button(group, this.fButtonStyle);
      this.fButton.setFont(group.getFont());
      this.fButton.setText(this.fLabelText);
      this.fButton.setEnabled(isEnabled());
      this.fButton.setSelection(this.fIsSelected);
      this.fButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
              SelectionButtonDialogField.this.doWidgetSelected(e);
            }
            
            public void widgetSelected(SelectionEvent e) {
              SelectionButtonDialogField.this.doWidgetSelected(e);
            }
          });
    } 
    return this.fButton;
  }
  
  private void doWidgetSelected(SelectionEvent e) {
    if (isOkToUse((Control)this.fButton))
      changeValue(this.fButton.getSelection()); 
  }
  
  private void changeValue(boolean newState) {
    if (this.fIsSelected != newState) {
      this.fIsSelected = newState;
      if (this.fAttachedDialogFields != null) {
        boolean focusSet = false;
        for (int i = 0; i < this.fAttachedDialogFields.length; i++) {
          this.fAttachedDialogFields[i].setEnabled(this.fIsSelected);
          if (this.fIsSelected && !focusSet)
            focusSet = this.fAttachedDialogFields[i].setFocus(); 
        } 
      } 
      dialogFieldChanged();
    } else if (this.fButtonStyle == 8) {
      dialogFieldChanged();
    } 
  }
  
  public void setLabelText(String labeltext) {
    this.fLabelText = labeltext;
    if (isOkToUse((Control)this.fButton))
      this.fButton.setText(labeltext); 
  }
  
  public boolean isSelected() {
    return this.fIsSelected;
  }
  
  public void setSelection(boolean selected) {
    changeValue(selected);
    if (isOkToUse((Control)this.fButton))
      this.fButton.setSelection(selected); 
  }
  
  protected void updateEnableState() {
    super.updateEnableState();
    if (isOkToUse((Control)this.fButton))
      this.fButton.setEnabled(isEnabled()); 
  }
  
  public void refresh() {
    super.refresh();
    if (isOkToUse((Control)this.fButton))
      this.fButton.setSelection(this.fIsSelected); 
  }
}
