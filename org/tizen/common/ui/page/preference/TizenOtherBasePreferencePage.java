package org.tizen.common.ui.page.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tizen.common.util.SWTUtil;

public class TizenOtherBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  protected Control createContents(Composite parent) {
    noDefaultAndApplyButton();
    SWTUtil.createLabel(parent, Messages.TizenOtherBasePreferencePage_Label, Messages.TizenOtherBasePreferencePage_Tooltip);
    return null;
  }
  
  public void init(IWorkbench workbench) {}
}
