package org.tizen.common.ui;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.OSChecker;
import org.tizen.common.util.OpenBrowserUtil;
import org.tizen.common.util.StringUtil;

public class HelpToolTip extends DefaultToolTip {
  final Logger logger = LoggerFactory.getLogger(HelpToolTip.class);
  
  private Cursor helpCursor;
  
  private TooltipShowListener showListener;
  
  private TooltipHideListener hideListener;
  
  private Composite composite;
  
  private Control tooltipControl;
  
  private int popupDelay;
  
  private boolean showOnMouseDown;
  
  private boolean focusOnHover;
  
  private SelectionListener linkListener;
  
  public HelpToolTip(Composite c) {
    this(c, true, false);
  }
  
  public HelpToolTip(Composite c, boolean showOnMouseDown, boolean hideOnMouseDown) {
    super((Control)createHelpIcon(c), 2, false);
    this.tooltipControl = (Control)getToolTipArea(null);
    this.helpCursor = new Cursor((Device)this.tooltipControl.getShell().getDisplay(), 21);
    this.tooltipControl.setCursor(this.helpCursor);
    this.showListener = new TooltipShowListener();
    this.hideListener = new TooltipHideListener();
    this.linkListener = (SelectionListener)new TooltipSelectionListener();
    this.tooltipControl.addListener(3, this.showListener);
    setPopupDelay(1000);
    setShowOnMouseDown(showOnMouseDown);
    setHideOnMouseDown(hideOnMouseDown);
  }
  
  public void addControlSelectionListener(Listener listener) {
    this.tooltipControl.addListener(3, listener);
  }
  
  public void deactivate() {
    super.deactivate();
    if (this.tooltipControl != null)
      this.tooltipControl.removeListener(3, this.showListener); 
  }
  
  public void activate() {
    super.activate();
    setShowOnMouseDown(this.showOnMouseDown);
  }
  
  protected final Composite createToolTipContentArea(Event event, Composite parent) {
    this.composite = doCreateToolTipContentArea(event, parent);
    this.tooltipControl.removeListener(3, this.showListener);
    if (this.composite != null)
      this.tooltipControl.addListener(7, this.hideListener); 
    return this.composite;
  }
  
  protected Composite doCreateToolTipContentArea(Event event, Composite parent) {
    Label label;
    String text = getText(event);
    Color fgColor = getForegroundColor(event);
    Color bgColor = getBackgroundColor(event);
    Font font = getFont(event);
    SelectionListener listener = getLinkListener(event);
    Composite composite = new Composite(parent, 0);
    composite.setLayout((Layout)new GridLayout());
    if (listener != null) {
      setFocusOnHover(true);
      Link link = new Link(composite, getStyle(event) | 0x40);
      link.addSelectionListener(listener);
      if (text != null)
        link.setText(text); 
    } else {
      label = new Label(composite, getStyle(event) | 0x40);
      if (text != null)
        label.setText(text); 
    } 
    if ((label.computeSize(-1, -1)).x > 350) {
      GridData gd = new GridData();
      gd.widthHint = 350;
      label.setLayoutData(gd);
    } 
    if (fgColor != null)
      label.setForeground(fgColor); 
    if (bgColor != null) {
      composite.setBackground(bgColor);
      label.setBackground(bgColor);
    } 
    if (font != null)
      label.setFont(font); 
    return composite;
  }
  
  protected void afterHideToolTip(Event event) {
    this.tooltipControl.removeListener(3, this.showListener);
    if (this.showOnMouseDown)
      this.tooltipControl.addListener(3, this.showListener); 
    this.tooltipControl.removeListener(7, this.hideListener);
    super.afterHideToolTip(event);
  }
  
  public void setPopupDelay(int popupDelay) {
    this.popupDelay = popupDelay;
    super.setPopupDelay(popupDelay);
  }
  
  protected SelectionListener getLinkListener(Event event) {
    return this.linkListener;
  }
  
  public void setShowOnMouseDown(boolean showOnMouseDown) {
    this.showOnMouseDown = showOnMouseDown;
    if (this.tooltipControl != null) {
      this.tooltipControl.removeListener(3, this.showListener);
      if (showOnMouseDown)
        this.tooltipControl.addListener(3, this.showListener); 
    } 
  }
  
  public boolean isShowOnMouseDown() {
    return this.showOnMouseDown;
  }
  
  public void setFocusOnHover(boolean focusOnHover) {
    this.focusOnHover = focusOnHover;
  }
  
  public boolean isFocusOnHover() {
    return this.focusOnHover;
  }
  
  private static Label createHelpIcon(Composite c) {
    Label helpIcon = new Label(c, 0);
    helpIcon.setImage(PlatformUI.getWorkbench().getSharedImages().getImage("IMG_LCL_LINKTO_HELP"));
    return helpIcon;
  }
  
  public void dispose() {
    if (this.helpCursor != null)
      this.helpCursor.dispose(); 
  }
  
  class TooltipShowListener implements Listener {
    public void handleEvent(Event event) {
      Point cursorAbsLocation;
      Point containerAbsLocation;
      int relativeX;
      int relativeY;
      switch (event.type) {
        case 3:
          HelpToolTip.this.hide();
          HelpToolTip.this.setPopupDelay(0);
          cursorAbsLocation = HelpToolTip.this.tooltipControl.getDisplay().getCursorLocation();
          containerAbsLocation = HelpToolTip.this.tooltipControl.getParent().toDisplay(HelpToolTip.this.tooltipControl.getLocation());
          relativeX = cursorAbsLocation.x - containerAbsLocation.x;
          relativeY = cursorAbsLocation.y - containerAbsLocation.y;
          HelpToolTip.this.show(new Point(relativeX, relativeY));
          HelpToolTip.this.setPopupDelay(HelpToolTip.this.popupDelay);
          break;
      } 
    }
  }
  
  class TooltipHideListener implements Listener {
    public void handleEvent(Event event) {
      if (HelpToolTip.this.composite.isDisposed())
        return; 
      Shell shell = HelpToolTip.this.composite.getShell();
      switch (event.type) {
        case 3:
          if (HelpToolTip.this.isHideOnMouseDown())
            HelpToolTip.this.hide(); 
          break;
        case 7:
          if (!shell.getBounds().contains(HelpToolTip.this.composite.getDisplay().getCursorLocation())) {
            HelpToolTip.this.hide();
            break;
          } 
          if (HelpToolTip.this.focusOnHover) {
            shell.setActive();
            HelpToolTip.this.composite.setFocus();
            if (OSChecker.isLinux()) {
              Listener[] listeners = shell.getListeners(7);
              if (listeners != null)
                shell.removeListener(7, listeners[0]); 
            } 
          } 
          break;
      } 
    }
  }
  
  class TooltipSelectionListener extends SelectionAdapter {
    public void widgetSelected(SelectionEvent e) {
      HelpToolTip.this.hide();
      String link = e.text.toLowerCase();
      if (link == null)
        return; 
      if (link.startsWith("http://")) {
        try {
          OpenBrowserUtil.openExternal(new URL(e.text), HelpToolTip.this.tooltipControl.getDisplay());
        } catch (MalformedURLException e1) {
          HelpToolTip.this.logger.error(e1.getMessage());
        } 
      } else if (link.startsWith("help://")) {
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/" + StringUtil.removeStart(link, "help://"));
      } 
    }
  }
}
