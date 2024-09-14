package org.tizen.common.ui.dialog;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.tizen.common.util.SWTUtil;

public abstract class ClosableTray extends DialogTray {
  private Image closeNormal;
  
  private Image closeHover;
  
  private Shell shell;
  
  private void createImages() {
    Display display = SWTUtil.getDisplay();
    int[] shape = { 
        3, 3, 5, 3, 7, 5, 8, 5, 10, 3, 
        12, 3, 12, 5, 10, 7, 10, 8, 12, 10, 
        12, 12, 10, 12, 8, 10, 7, 10, 5, 12, 
        3, 12, 3, 10, 5, 8, 5, 7, 3, 5 };
    Color border = display.getSystemColor(17);
    Color background = display.getSystemColor(25);
    Color backgroundHot = new Color((Device)display, new RGB(252, 160, 160));
    Color transparent = display.getSystemColor(11);
    PaletteData palette = new PaletteData(new RGB[] { transparent.getRGB(), border.getRGB(), background.getRGB(), backgroundHot.getRGB() });
    ImageData data = new ImageData(16, 16, 8, palette);
    data.transparentPixel = 0;
    this.closeNormal = new Image((Device)display, data);
    this.closeNormal.setBackground(transparent);
    GC gc = new GC((Drawable)this.closeNormal);
    gc.setBackground(background);
    gc.fillPolygon(shape);
    gc.setForeground(border);
    gc.drawPolygon(shape);
    gc.dispose();
    this.closeHover = new Image((Device)display, data);
    this.closeHover.setBackground(transparent);
    gc = new GC((Drawable)this.closeHover);
    gc.setBackground(backgroundHot);
    gc.fillPolygon(shape);
    gc.setForeground(border);
    gc.drawPolygon(shape);
    gc.dispose();
    backgroundHot.dispose();
  }
  
  protected Control createContents(Composite parent) {
    this.shell = parent.getShell();
    Composite container = new Composite(parent, 0);
    GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    layout.verticalSpacing = 0;
    container.setLayout((Layout)layout);
    container.addListener(12, new Listener() {
          public void handleEvent(Event event) {
            ClosableTray.this.dispose();
          }
        });
    ToolBarManager tbm = new ToolBarManager(8388608);
    tbm.createControl(container);
    GridData gd = new GridData(128);
    gd.grabExcessHorizontalSpace = true;
    tbm.getControl().setLayoutData(gd);
    Label separator = new Label(container, 258);
    gd = new GridData(256);
    gd.heightHint = 1;
    separator.setLayoutData(gd);
    createExternalControl(container);
    createImages();
    tbm.add((IContributionItem)new ContributionItem() {
          public void fill(ToolBar parent, int index) {
            ToolItem item = new ToolItem(parent, 8);
            item.setImage(ClosableTray.this.closeNormal);
            item.setHotImage(ClosableTray.this.closeHover);
            item.addListener(13, new Listener() {
                  public void handleEvent(Event event) {
                    TrayDialog dialog = (TrayDialog)(ClosableTray.null.access$0(ClosableTray.null.this)).shell.getData();
                    dialog.closeTray();
                    (ClosableTray.null.access$0(ClosableTray.null.this)).shell.setFocus();
                  }
                });
          }
        });
    tbm.update(true);
    return (Control)container;
  }
  
  protected void dispose() {
    if (this.closeNormal != null)
      this.closeNormal.dispose(); 
    if (this.closeHover != null)
      this.closeHover.dispose(); 
  }
  
  public abstract void createExternalControl(Composite paramComposite);
}
