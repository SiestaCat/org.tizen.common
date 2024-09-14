package org.tizen.common.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class PreferenceLinkMessageDialog extends MessageDialog {
  protected String linkMessage;
  
  protected String preferencePageId;
  
  public PreferenceLinkMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, String linkMessage, String preferencePageId) {
    super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    this.linkMessage = linkMessage;
    this.preferencePageId = preferencePageId;
  }
  
  protected Control createMessageArea(Composite composite) {
    Image image = getImage();
    if (image != null) {
      this.imageLabel = new Label(composite, 0);
      image.setBackground(this.imageLabel.getBackground());
      this.imageLabel.setImage(image);
      addAccessibleListeners(this.imageLabel, image);
      GridDataFactory.fillDefaults().align(16777216, 1)
        .applyTo((Control)this.imageLabel);
    } 
    if (StringUtil.isEmpty(this.linkMessage)) {
      if (!StringUtil.isEmpty(this.message)) {
        this.messageLabel = new Label(composite, getMessageLabelStyle());
        this.messageLabel.setText(this.message);
        GridDataFactory.fillDefaults()
          .align(4, 1)
          .grab(true, false)
          .hint(
            convertHorizontalDLUsToPixels(300), 
            -1).applyTo((Control)this.messageLabel);
      } 
    } else {
      String fullMessage = "";
      if (!StringUtil.isEmpty(this.message))
        fullMessage = this.message; 
      fullMessage = String.valueOf(fullMessage) + " <a>" + this.linkMessage + "</a>";
      Link linkLabel = new Link(composite, getMessageLabelStyle());
      linkLabel.setText(fullMessage);
      GridDataFactory.fillDefaults()
        .align(4, 1)
        .grab(true, false)
        .hint(
          convertHorizontalDLUsToPixels(300), 
          -1).applyTo((Control)linkLabel);
      linkLabel.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
              String preferencePageId = PreferenceLinkMessageDialog.this.getPreferencePageId();
              if (!StringUtil.isEmpty(preferencePageId)) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(PreferenceLinkMessageDialog.this.getShell(), preferencePageId, new String[] { preferencePageId }, null);
                dialog.open();
              } 
            }
            
            public void widgetDefaultSelected(SelectionEvent e) {}
          });
    } 
    return (Control)composite;
  }
  
  public static String[] getButtonLabels(int kind) {
    String[] dialogButtonLabels;
    switch (kind) {
      case 1:
      case 2:
      case 4:
        dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
        return dialogButtonLabels;
      case 5:
        dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
        return dialogButtonLabels;
      case 3:
        dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
        return dialogButtonLabels;
      case 6:
        dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
        return dialogButtonLabels;
    } 
    throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()");
  }
  
  private void addAccessibleListeners(Label label, final Image image) {
    label.getAccessible().addAccessibleListener((AccessibleListener)new AccessibleAdapter() {
          public void getName(AccessibleEvent event) {
            String accessibleMessage = PreferenceLinkMessageDialog.this.getAccessibleMessageFor(image);
            if (accessibleMessage == null)
              return; 
            event.result = accessibleMessage;
          }
        });
  }
  
  private String getAccessibleMessageFor(Image image) {
    if (image.equals(getErrorImage()))
      return JFaceResources.getString("error"); 
    if (image.equals(getWarningImage()))
      return JFaceResources.getString("warning"); 
    if (image.equals(getInfoImage()))
      return JFaceResources.getString("info"); 
    if (image.equals(getQuestionImage()))
      return JFaceResources.getString("question"); 
    return null;
  }
  
  protected String getPreferencePageId() {
    return this.preferencePageId;
  }
  
  public static boolean open(int kind, Shell parent, String title, String message, int style, String linkMessage, String preferencePageId) {
    PreferenceLinkMessageDialog dialog = new PreferenceLinkMessageDialog(parent, title, null, message, 
        kind, getButtonLabels(kind), 0, linkMessage, preferencePageId);
    style &= 0x10000000;
    dialog.setShellStyle(dialog.getShellStyle() | style);
    return (dialog.open() == 0);
  }
  
  public static boolean openConfirm(Shell parent, String title, String message, String linkMessage, String preferencePageId) {
    return open(5, parent, title, message, 0, linkMessage, preferencePageId);
  }
  
  public static void openError(Shell parent, String title, String message, String linkMessage, String preferencePageId) {
    open(1, parent, title, message, 0, linkMessage, preferencePageId);
  }
  
  public static void openInformation(Shell parent, String title, String message, String linkMessage, String preferencePageId) {
    open(2, parent, title, message, 0, linkMessage, preferencePageId);
  }
  
  public static boolean openQuestion(Shell parent, String title, String message, String linkMessage, String preferencePageId) {
    return open(3, parent, title, message, 0, linkMessage, preferencePageId);
  }
  
  public static void openWarning(Shell parent, String title, String message, String linkMessage, String preferencePageId) {
    open(4, parent, title, message, 0, linkMessage, preferencePageId);
  }
}
