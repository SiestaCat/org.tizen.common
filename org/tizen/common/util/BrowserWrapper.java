package org.tizen.common.util;

import java.io.File;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class BrowserWrapper {
  private Browser browser;
  
  public BrowserWrapper(Browser browser) {
    this.browser = browser;
  }
  
  public BrowserWrapper(Composite composite) {
    this.browser = new Browser(composite, 0);
  }
  
  public BrowserWrapper(Composite composite, int style) {
    this.browser = new Browser(composite, style);
  }
  
  public BrowserWrapper(Composite composite, int style, GridData data) {
    this.browser = new Browser(composite, style);
    this.browser.setLayoutData(data);
  }
  
  public Browser getBrowser() {
    return this.browser;
  }
  
  public void setUrl(final String url) {
    SWTUtil.asyncExec(new Runnable() {
          public void run() {
            if (!BrowserWrapper.this.browser.isDisposed())
              BrowserWrapper.this.browser.setUrl(url); 
          }
        });
  }
  
  public void setUrl(File url) {
    setUrl(url.getAbsolutePath());
  }
  
  public void setText(String html) {
    this.browser.setText(html);
  }
  
  public void refresh() {
    this.browser.refresh();
  }
  
  public void setFocus() {
    this.browser.setFocus();
  }
  
  public Object evaluate(String script) throws SWTException {
    return this.browser.evaluate(script);
  }
  
  public void setRedraw(boolean redraw) {
    this.browser.setRedraw(redraw);
  }
  
  public void setBackground(Color color) {
    this.browser.setBackground(color);
  }
  
  public void dispose() {
    this.browser.dispose();
  }
}
