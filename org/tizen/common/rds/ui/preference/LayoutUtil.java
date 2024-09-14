package org.tizen.common.rds.ui.preference;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class LayoutUtil {
  public static int getNumberOfColumns(DialogField[] editors) {
    int nCulumns = 0;
    for (int i = 0; i < editors.length; i++)
      nCulumns = Math.max(editors[i].getNumberOfControls(), nCulumns); 
    return nCulumns;
  }
  
  public static void doDefaultLayout(Composite parent, DialogField[] editors, boolean labelOnTop) {
    doDefaultLayout(parent, editors, labelOnTop, 0, 0);
  }
  
  public static void doDefaultLayout(Composite parent, DialogField[] editors, boolean labelOnTop, int marginWidth, int marginHeight) {
    int nCulumns = getNumberOfColumns(editors);
    Control[][] controls = new Control[editors.length][];
    for (int i = 0; i < editors.length; i++)
      controls[i] = editors[i].doFillIntoGrid(parent, nCulumns); 
    if (labelOnTop) {
      nCulumns--;
      modifyLabelSpans(controls, nCulumns);
    } 
    GridLayout layout = null;
    if (parent.getLayout() instanceof GridLayout) {
      layout = (GridLayout)parent.getLayout();
    } else {
      layout = new GridLayout();
    } 
    if (marginWidth != -1)
      layout.marginWidth = marginWidth; 
    if (marginHeight != -1)
      layout.marginHeight = marginHeight; 
    layout.numColumns = nCulumns;
    parent.setLayout((Layout)layout);
  }
  
  private static void modifyLabelSpans(Control[][] controls, int nCulumns) {
    for (int i = 0; i < controls.length; i++)
      setHorizontalSpan(controls[i][0], nCulumns); 
  }
  
  public static void setHorizontalSpan(Control control, int span) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData) {
      ((GridData)ld).horizontalSpan = span;
    } else if (span != 1) {
      GridData gd = new GridData();
      gd.horizontalSpan = span;
      control.setLayoutData(gd);
    } 
  }
  
  public static void setWidthHint(Control control, int widthHint) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData)
      ((GridData)ld).widthHint = widthHint; 
  }
  
  public static void setHeightHint(Control control, int heightHint) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData)
      ((GridData)ld).heightHint = heightHint; 
  }
  
  public static void setHorizontalIndent(Control control, int horizontalIndent) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData)
      ((GridData)ld).horizontalIndent = horizontalIndent; 
  }
  
  public static void setHorizontalGrabbing(Control control) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData)
      ((GridData)ld).grabExcessHorizontalSpace = true; 
  }
  
  public static void setVerticalGrabbing(Control control) {
    Object ld = control.getLayoutData();
    if (ld instanceof GridData) {
      GridData gd = (GridData)ld;
      gd.grabExcessVerticalSpace = true;
      gd.verticalAlignment = 4;
    } 
  }
}
