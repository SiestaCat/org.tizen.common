package org.tizen.common.ui.page.preference;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tizen.common.CommonPlugin;
import org.tizen.common.util.SWTUtil;

public class SdbPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String id = "org.tizen.common.preferences.tools.sdb";
  
  public static final String KEY_SDB_RESPONSE_TIMEOUT = "response_timeout";
  
  public static final int VALUE_SDB_RESPONSE_TIMEOUT_DEFAULT = 300000;
  
  public SdbPreferencePage() {
    setPreferenceStore(CommonPlugin.getDefault().getPreferenceStore());
    setDescription(Messages.SDB_DESCRIPTION);
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
    createResponseTimeoutPreferences(composite);
  }
  
  private void createResponseTimeoutPreferences(Composite parent) {
    Group group = SWTUtil.createGroup(parent, Messages.SDB_RESPONSE_TIMEOUT_GROUP, 1);
    Composite formatComposite = SWTUtil.createCompositeEx((Composite)group, 2, 768);
    IntegerFieldEditor intField = new IntegerFieldEditor("response_timeout", Messages.SDB_RESPONSE_TIMEOUT_LABEL, formatComposite);
    intField.setValidRange(0, 2147483647);
    intField.getTextControl(formatComposite).setToolTipText(Messages.SDB_RESPONSE_TIMEOUT_CAUTION);
    addField((FieldEditor)intField);
  }
}
