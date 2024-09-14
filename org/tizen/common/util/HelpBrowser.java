package org.tizen.common.util;

import java.net.URL;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpBrowser {
  private static final String HELP_PAGE_PREFIX = "help:/";
  
  private static final String BACK_ICON = "icons/back.gif";
  
  private static final String FORWARD_ICON = "icons/forward.gif";
  
  private Logger logger = LoggerFactory.getLogger(getClass());
  
  private Browser browser;
  
  public Browser getBrowser() {
    return this.browser;
  }
  
  public HelpBrowser(Composite parent, int style) {
    this(parent, style, false);
  }
  
  public HelpBrowser(Composite parent, int style, boolean hasToolbar) {
    if (hasToolbar)
      generateToolBar(parent); 
    this.browser = new Browser(parent, style);
    GridData gd = new GridData(1808);
    this.browser.setLayoutData(gd);
    enableHelpPage();
  }
  
  private void generateToolBar(Composite parent) {
    ToolBar toolbar = new ToolBar(parent, 0);
    ToolItem backButton = new ToolItem(toolbar, 8);
    Image backIcon = ImageUtil.getImage("org.tizen.common", "icons/back.gif");
    backButton.setImage(backIcon);
    ToolItem forwardButton = new ToolItem(toolbar, 8);
    Image forwardIcon = ImageUtil.getImage("org.tizen.common", "icons/forward.gif");
    forwardButton.setImage(forwardIcon);
    forwardButton.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            HelpBrowser.this.browser.forward();
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {}
        });
    backButton.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            HelpBrowser.this.browser.back();
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {}
        });
  }
  
  private void enableHelpPage() {
    getBrowser().addLocationListener(new LocationListener() {
          public void changing(LocationEvent event) {
            String url = event.location;
            if (url.startsWith("help:/")) {
              Browser browser = (Browser)event.widget;
              String tempUrl = url.substring("help:/".length(), url.length());
              URL helpSystem = PlatformUI.getWorkbench().getHelpSystem().resolve(tempUrl, false);
              if (helpSystem != null) {
                browser.setUrl(helpSystem.toString());
              } else {
                HelpBrowser.this.logger.error("HelpSystem URL is Null!");
              } 
            } 
          }
          
          public void changed(LocationEvent event) {}
        });
  }
}
