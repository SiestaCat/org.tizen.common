package org.tizen.common.core.application;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IViewPart;
import org.tizen.common.util.Assert;

public class StatusLineMessageManager {
  protected final IStatusLineManager manager;
  
  public StatusLineMessageManager(IViewPart part) {
    this(part.getViewSite().getActionBars().getStatusLineManager());
  }
  
  public StatusLineMessageManager(IStatusLineManager manager) {
    Assert.notNull(manager);
    this.manager = manager;
  }
  
  public void setErrorMessage(String message) {
    this.manager.setErrorMessage(message);
  }
  
  public void setMessage(String message) {
    this.manager.setMessage(message);
  }
}
