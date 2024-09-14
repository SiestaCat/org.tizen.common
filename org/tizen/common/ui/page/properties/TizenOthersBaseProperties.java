package org.tizen.common.ui.page.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.tizen.common.util.SWTUtil;

public class TizenOthersBaseProperties extends PropertyPage implements IWorkbenchPropertyPage {
  protected Control createContents(Composite parent) {
    noDefaultAndApplyButton();
    SWTUtil.createLabel(parent, PropertiesMessages.TizenOthersBaseProperties_Label, PropertiesMessages.TizenOthersBaseProperties_Tooltip);
    return null;
  }
}
