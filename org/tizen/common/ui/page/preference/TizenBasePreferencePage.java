package org.tizen.common.ui.page.preference;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tizen.common.CommonPlugin;
import org.tizen.common.util.SWTUtil;

public class TizenBasePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String TIZENSDK_PATH = "org.tizen.common.TizenSDKBasePreferencePage";
  
  public static final String KEY_SDKLOCATION = "sdkpath";
  
  public static final String VALUE_SDKLOCATION_DEFAULT = null;
  
  private boolean applyOk = true;
  
  public TizenBasePreferencePage() {
    setPreferenceStore(CommonPlugin.getDefault().getPreferenceStore());
    setDescription(Messages.DESCRIPTION);
  }
  
  public void init(IWorkbench workbench) {}
  
  protected void createFieldEditors() {
    Composite composite = getFieldEditorParent();
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    composite.setLayout((Layout)layout);
    GridData data = new GridData(1808);
    data.verticalAlignment = 4;
    data.horizontalAlignment = 4;
    composite.setLayoutData(data);
    SWTUtil.createSpacer(composite, 1);
    createSDKPreferences(composite);
  }
  
  private void createSDKPreferences(Composite parent) {
    Group group = SWTUtil.createGroup(parent, Messages.GROUP, 1);
    Composite formatComposite = SWTUtil.createCompositeEx((Composite)group, 1, 768);
    DirectoryFieldEditor mDirectoryField = new DirectoryFieldEditor("sdkpath", Messages.LOCATION, formatComposite);
    addField((FieldEditor)mDirectoryField);
    mDirectoryField.setEnabled(false, formatComposite);
  }
  
  protected void performDefaults() {
    super.performDefaults();
  }
  
  protected void performApply() {
    setErrorMessage(null);
    this.applyOk = true;
  }
  
  public boolean performOk() {
    performApply();
    if (!this.applyOk)
      return false; 
    return super.performOk();
  }
}
