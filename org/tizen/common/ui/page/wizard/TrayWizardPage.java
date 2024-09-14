package org.tizen.common.ui.page.wizard;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.tizen.common.ui.dialog.ClosableTray;

public abstract class TrayWizardPage extends WizardPage {
  protected TrayWizardPage(String pageName) {
    super(pageName);
  }
  
  protected TrayWizardPage(String pageName, String title, ImageDescriptor titleImage) {
    super(pageName, title, titleImage);
  }
  
  protected Control createTrayButtonControl(Composite parent, String buttonText, final ClosableTray tray) {
    Composite child = new Composite(parent, 0);
    child.setLayout((Layout)new GridLayout());
    child.setLayoutData(new GridData(128));
    Button expandButton = new Button(child, 0);
    expandButton.setText(buttonText);
    expandButton.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            TrayWizardPage.this.openClosableTray(tray);
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {}
        });
    return (Control)expandButton;
  }
  
  protected void openClosableTray(ClosableTray tray) {
    IWizardContainer wc = getContainer();
    if (wc instanceof TrayDialog) {
      TrayDialog wd = (TrayDialog)wc;
      DialogTray currentTray = wd.getTray();
      if (!(currentTray instanceof ClosableTray)) {
        if (currentTray != null)
          wd.closeTray(); 
        wd.openTray(tray);
      } 
    } 
  }
}
