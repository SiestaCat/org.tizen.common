package org.tizen.common.ui.page.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TizenToolsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  public TizenToolsPreferencePage() {
    String description = "Expand the tree to edit preferences for a specific feature";
    setDescription(description);
  }
  
  public void init(IWorkbench workbench) {}
  
  protected Control createContents(Composite parent) {
    noDefaultAndApplyButton();
    return null;
  }
}
