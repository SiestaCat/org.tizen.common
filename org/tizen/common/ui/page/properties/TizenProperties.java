package org.tizen.common.ui.page.properties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.tizen.common.ui.page.preference.Messages;

public class TizenProperties extends PropertyPage implements IWorkbenchPropertyPage {
  public TizenProperties() {
    String description = Messages.PROPERTIES_PAGE_BODY;
    setDescription(description);
  }
  
  protected Control createContents(Composite parent) {
    noDefaultAndApplyButton();
    return null;
  }
}
