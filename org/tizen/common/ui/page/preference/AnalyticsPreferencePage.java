package org.tizen.common.ui.page.preference;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tizen.common.CommonPlugin;
import org.tizen.common.RemoteLogger;
import org.tizen.common.util.SWTUtil;

public class AnalyticsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String id = "org.tizen.common.preferences.tools.anaytics";
  
  private static Button analyticsCheckBox;
  
  private Button deleteButton;
  
  private Composite parent;
  
  private Composite analyticsGroupComp;
  
  public AnalyticsPreferencePage() {
    setPreferenceStore(CommonPlugin.getDefault().getPreferenceStore());
    setDescription(Messages.ANALYTICS_DESCRIPTION);
  }
  
  public void init(IWorkbench workbench) {}
  
  protected void createFieldEditors() {
    this.parent = getFieldEditorParent();
    SWTUtil.setGridLayout(this.parent, 1, false, -1, -1, -1, -1, 
        1808);
    Group group = SWTUtil.createGroup(this.parent, 
        Messages.ANALYTICS_GROUP_INFO, 1);
    SWTUtil.setGridLayout((Composite)group, 2, false, -1, -1, -1, -1, 
        768);
    this.analyticsGroupComp = new Composite((Composite)group, 0);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 50;
    this.analyticsGroupComp.setLayout((Layout)gridLayout);
    GridData gridData = new GridData(768);
    gridData.horizontalSpan = 2;
    this.analyticsGroupComp.setLayoutData(gridData);
    analyticsCheckBox = SWTUtil.createCheckbox(this.analyticsGroupComp, Messages.ANALYTICS_CHECKBOX_LABEL, null, true, true);
    gridData = new GridData(768);
    analyticsCheckBox.setLayoutData(gridData);
    analyticsCheckBox.setSelection(RemoteLogger.isLoggingEnabled().booleanValue());
    analyticsCheckBox.addSelectionListener((SelectionListener)new SelectionAdapter() {
          public void widgetSelected(SelectionEvent arg0) {
            if (!AnalyticsPreferencePage.analyticsCheckBox.getSelection()) {
              boolean result = MessageDialog.openConfirm(AnalyticsPreferencePage.this.getShell(), Messages.ANALYTICS_DELETE_TITLE, Messages.ANALYTICS_DELETE_DESCRIPTION);
              if (result)
                RemoteLogger.deleteAnalytics(); 
            } 
          }
        });
  }
  
  protected void performDefaults() {
    analyticsCheckBox.setSelection(RemoteLogger.isLoggingEnabled().booleanValue());
    super.performDefaults();
  }
  
  protected void performApply() {
    RemoteLogger.writeLoggingInfoToFile(Boolean.valueOf(analyticsCheckBox.getSelection()));
  }
  
  public boolean performOk() {
    performApply();
    return super.performOk();
  }
}
