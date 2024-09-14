package org.tizen.common.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.tizen.common.util.SWTUtil;

public abstract class TableToolTipListener implements Listener {
  private Shell tip = null;
  
  private Label label = null;
  
  private Table table = null;
  
  public void setTable(Table table) {
    this.table = table;
  }
  
  public void handleEvent(Event event) {
    int eventType = event.type;
    if ((5 == eventType || 37 == eventType || 12 == eventType) && 
      this.tip != null) {
      this.tip.dispose();
      this.tip = null;
      this.label = null;
      return;
    } 
    if (32 == eventType) {
      TableItem item = this.table.getItem(new Point(event.x, event.y));
      if (item != null) {
        if (this.tip != null && !this.tip.isDisposed())
          this.tip.dispose(); 
        this.tip = new Shell(SWTUtil.getActiveShell(), 16388);
        this.tip.setLayout((Layout)new FillLayout());
        this.label = new Label((Composite)this.tip, 0);
        this.label.setText(getLabelText(item));
        this.label.setData(item);
        this.label.addListener(7, this.labelListener);
        this.label.addListener(3, this.labelListener);
        this.label.addListener(37, this.labelListener);
        Point size = this.tip.computeSize(-1, -1);
        Rectangle rect = item.getBounds(0);
        Point pt = this.table.toDisplay(rect.x, rect.y);
        this.tip.setBounds(pt.x, pt.y, size.x, size.y);
        this.tip.setVisible(true);
      } 
    } 
  }
  
  private final Listener labelListener = new Listener() {
      public void handleEvent(Event event) {
        Label label = (Label)event.widget;
        Shell shell = label.getShell();
        int eventType = event.type;
        if (3 == eventType) {
          if (TableToolTipListener.this.table != null) {
            Event e = new Event();
            e.item = (Widget)label.getData();
            TableToolTipListener.this.table.setSelection((TableItem)e.item);
            TableToolTipListener.this.table.notifyListeners(13, e);
          } 
          return;
        } 
        if (7 == eventType || 37 == eventType) {
          shell.dispose();
          return;
        } 
      }
    };
  
  protected abstract String getLabelText(TableItem paramTableItem);
}
