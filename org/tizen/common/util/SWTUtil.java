package org.tizen.common.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.tizen.common.Cabinet;
import org.tizen.common.Messages;
import org.tizen.common.ui.TableToolTipListener;

public class SWTUtil {
  protected static final Collection<?> HTML_EXTENSIONS = Collections.unmodifiableCollection(new HashSet(Arrays.asList((Object[])new String[] { "html", "htm", "shtml", "shtm", "xhtml" })));
  
  public static Display getDisplay() {
    Display dp = Display.getCurrent();
    if (dp != null)
      return dp; 
    return Display.getDefault();
  }
  
  public static Shell getActiveShell() {
    Display dp = getDisplay();
    if (dp == null)
      return null; 
    return dp.getActiveShell();
  }
  
  public static Shell getShell() {
    Shell shell = getActiveShell();
    if (shell == null && getDisplay() != null)
      shell = getDisplay().getShells()[0]; 
    return shell;
  }
  
  public static <T> T exec(Cabinet<T> runnable) {
    syncExec(runnable);
    return runnable.getData();
  }
  
  public static void syncExec(Runnable runnable) {
    syncExec(getDisplay(), runnable);
  }
  
  public static void syncExec(Display dp, Runnable runnable) {
    dp.syncExec(runnable);
  }
  
  public static void asyncExec(Runnable runnable) {
    asyncExec(getDisplay(), runnable);
  }
  
  public static void asyncExec(Display dp, Runnable runnable) {
    dp.asyncExec(runnable);
  }
  
  public static void tryDispose(Widget... disposables) {
    if (disposables == null)
      return; 
    byte b;
    int i;
    Widget[] arrayOfWidget;
    for (i = (arrayOfWidget = disposables).length, b = 0; b < i; ) {
      Widget disposable = arrayOfWidget[b];
      if (disposable != null && !disposable.isDisposed())
        disposable.dispose(); 
      b++;
    } 
  }
  
  public static void tryDispose(Resource... disposables) {
    if (disposables == null)
      return; 
    byte b;
    int i;
    Resource[] arrayOfResource;
    for (i = (arrayOfResource = disposables).length, b = 0; b < i; ) {
      Resource disposable = arrayOfResource[b];
      if (disposable != null && !disposable.isDisposed())
        disposable.dispose(); 
      b++;
    } 
  }
  
  public static Text createNumericText(Composite composite, int style, int limit) {
    Text text = new Text(composite, style);
    text.setTextLimit(limit);
    text.addVerifyListener(new VerifyListener() {
          final Pattern pattern = Pattern.compile("[0-9]*");
          
          public void verifyText(VerifyEvent e) {
            Matcher m = this.pattern.matcher(e.text);
            if (!m.matches())
              e.doit = false; 
          }
        });
    return text;
  }
  
  public static Text createNumericText(Composite composite, int limit) {
    return createNumericText(composite, 2048, limit);
  }
  
  public static ScrolledComposite createScrolledComposite(Composite parent, int width, int height) {
    ScrolledComposite sc = new ScrolledComposite(parent, 768);
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    if (width == 0)
      width = screen.width; 
    if (height == 0)
      height = screen.height; 
    sc.setMinSize(width, height);
    sc.setExpandHorizontal(true);
    sc.setExpandVertical(true);
    return sc;
  }
  
  public static IWorkbenchPage getActivePage() {
    IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
    if (window == null)
      return null; 
    return window.getActivePage();
  }
  
  public static IEditorPart getActiveEditor() {
    IWorkbenchPage page = getActivePage();
    if (page == null)
      return null; 
    return page.getActiveEditor();
  }
  
  public static IEditorPart getCurrentEditor(String ext) {
    if (ext == null)
      throw new IllegalArgumentException("extension can't be null"); 
    IEditorPart editor = getActiveEditor();
    if (editor == null || editor.getEditorInput() == null)
      return null; 
    if (ext.equalsIgnoreCase(FileUtil.getFileExtension(editor.getEditorInput().getName())))
      return editor; 
    return null;
  }
  
  public static IEditorPart getCurrentHtmlEditor() {
    IEditorPart editor = getActiveEditor();
    if (editor == null || editor.getEditorInput() == null)
      return null; 
    if (isHtmlFile(editor.getEditorInput().getName()))
      return editor; 
    return null;
  }
  
  public static boolean isHtmlFile(String fullName) {
    String fileExt = StringUtil.nvl(new String[] { FileUtil.getFileExtension(fullName) });
    return HTML_EXTENSIONS.contains(fileExt.toLowerCase());
  }
  
  public static IEditorReference[] getEditorReferences() {
    IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
    if (window == null)
      return null; 
    IWorkbenchPage page = window.getActivePage();
    if (page == null)
      return null; 
    return page.getEditorReferences();
  }
  
  public static void refreshLocal(IFile file, int depth) throws CoreException {
    if (file == null || !file.exists() || !file.isAccessible())
      return; 
    if (file.isSynchronized(depth))
      return; 
    file.refreshLocal(depth, null);
  }
  
  public static void refreshLocal(IProject project, int depth) throws CoreException {
    if (project == null || !project.exists() || !project.isAccessible())
      return; 
    if (project.isSynchronized(depth))
      return; 
    project.refreshLocal(depth, null);
  }
  
  public static void refreshLocal() throws CoreException {
    IEditorPart editor = getActiveEditor();
    if (editor != null && editor.getEditorInput() != null) {
      IFileEditorInput input = (IFileEditorInput)editor.getEditorInput().getAdapter(IFileEditorInput.class);
      if (input == null)
        return; 
      refreshLocal(input.getFile().getProject(), 2);
    } 
  }
  
  public static String getProjectPath() {
    IEditorPart editor = getActiveEditor();
    if (editor == null || editor.getEditorInput() == null)
      return null; 
    IFileEditorInput input = (IFileEditorInput)editor.getEditorInput().getAdapter(IFileEditorInput.class);
    if (input == null)
      return null; 
    String projectName = input.getFile().getProject().getName();
    IWorkspaceRoot workspaceRoot = IDEWorkbenchPlugin.getPluginWorkspace().getRoot();
    String projectPath = "";
    try {
      projectPath = workspaceRoot.getProject(projectName).getLocation().toOSString();
    } catch (Exception exception) {
      projectPath = workspaceRoot.getLocation().toOSString();
    } 
    return projectPath;
  }
  
  public static void addTableToolTipListener(Table table, TableToolTipListener listener) {
    listener.setTable(table);
    table.addListener(5, listener);
    table.addListener(37, listener);
    table.addListener(12, listener);
    table.addListener(32, listener);
  }
  
  public static void expandTree(Tree tree) {
    if (tree == null)
      return; 
    byte b;
    int i;
    TreeItem[] arrayOfTreeItem;
    for (i = (arrayOfTreeItem = tree.getItems()).length, b = 0; b < i; ) {
      TreeItem treeitem = arrayOfTreeItem[b];
      expandTreeItem(treeitem);
      b++;
    } 
  }
  
  private static void expandTreeItem(TreeItem treeItem) {
    Stack<TreeItem> treeStack = new Stack<>();
    treeStack.add(treeItem);
    TreeItem selectedItem = null;
    do {
      selectedItem = treeStack.pop();
      selectedItem.setExpanded(true);
      if (selectedItem.getItems() == null || selectedItem.getItemCount() == 0)
        continue; 
      byte b;
      int i;
      TreeItem[] arrayOfTreeItem;
      for (i = (arrayOfTreeItem = selectedItem.getItems()).length, b = 0; b < i; ) {
        TreeItem childItem = arrayOfTreeItem[b];
        treeStack.add(childItem);
        b++;
      } 
    } while (!treeStack.isEmpty());
  }
  
  public static void setGridLayout(Composite composite, int numColumns, boolean equalWidth, int heightHint, int widthHint, int horizontalSpan, int verticalSpan, int style) {
    composite.setLayout((Layout)new GridLayout(numColumns, equalWidth));
    setGridLayoutData((Control)composite, heightHint, widthHint, horizontalSpan, verticalSpan, style);
  }
  
  public static void setGridLayoutData(Control control, int heightHint, int widthHint, int horizontalSpan, int verticalSpan, int style) {
    GridData gd = new GridData(style);
    if (heightHint != -1)
      gd.heightHint = heightHint; 
    if (widthHint != -1)
      gd.widthHint = widthHint; 
    if (horizontalSpan != -1)
      gd.horizontalSpan = horizontalSpan; 
    if (verticalSpan != -1)
      gd.verticalSpan = verticalSpan; 
    control.setLayoutData(gd);
  }
  
  public static void errorDialogWithStackTrace(String title, String msg, Throwable t, String pluginId, Shell shell) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    String trace = sw.toString();
    List<Status> childStatuses = new ArrayList<>();
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = trace.split("\n")).length, b = 0; b < i; ) {
      String line = arrayOfString[b];
      childStatuses.add(new Status(4, pluginId, line));
      b++;
    } 
    MultiStatus ms = new MultiStatus(pluginId, 4, (IStatus[])childStatuses.toArray((Object[])new Status[0]), t.getLocalizedMessage(), t);
    ErrorDialog.openError(shell, "Error Dialog", msg, (IStatus)ms);
  }
  
  public static Composite createCompositeEx(Composite parent, int numColumns, int layoutMode) {
    Composite composite = new Composite(parent, 0);
    composite.setFont(parent.getFont());
    composite.setLayout((Layout)new GridLayout(numColumns, false));
    composite.setLayoutData(new GridData(layoutMode));
    return composite;
  }
  
  public static Group createGroup(Composite parent, String label, int numColumns) {
    Group group = new Group(parent, 0);
    group.setFont(parent.getFont());
    group.setText(label);
    GridLayout layout = new GridLayout();
    layout.numColumns = numColumns;
    group.setLayout((Layout)layout);
    group.setLayoutData(new GridData(768));
    return group;
  }
  
  public static void createSpacer(Composite composite, int columnSpan) {
    Label label = new Label(composite, 0);
    GridData gd = new GridData();
    gd.horizontalSpan = columnSpan;
    label.setLayoutData(gd);
  }
  
  public static int getButtonWidthHint(Button button) {
    button.setFont(JFaceResources.getDialogFont());
    PixelConverter converter = new PixelConverter((Control)button);
    int widthHint = converter.convertHorizontalDLUsToPixels(61);
    return Math.max(widthHint, (button.computeSize(-1, -1, true)).x);
  }
  
  public static void makeChildBackgroundTransient(final Composite parent) {
    if (parent != null) {
      final Object lock = new Object();
      final AtomicBoolean finished = new AtomicBoolean(false);
      parent.addPaintListener(new PaintListener() {
            class PairControl {
              Composite parent;
              
              Control child;
            }
            
            public void paintControl(PaintEvent e) {
              if (finished.get())
                return; 
              synchronized (lock) {
                finished.set(true);
                parent.removePaintListener(this);
                if (parent.getBackgroundImage() != null) {
                  Stack<PairControl> childStack = new Stack<>();
                  byte b;
                  int i;
                  Control[] arrayOfControl;
                  for (i = (arrayOfControl = parent.getChildren()).length, b = 0; b < i; ) {
                    Control childControl = arrayOfControl[b];
                    childStack.add(new PairControl(parent, childControl));
                    b++;
                  } 
                  while (!childStack.isEmpty()) {
                    PairControl pair = childStack.pop();
                    Control childControl = pair.child;
                    Composite parentComposite = pair.parent;
                    Image parentImage = parentComposite.getBackgroundImage();
                    Point childLoc = childControl.getLocation();
                    if (childControl instanceof Composite) {
                      Composite childComposite = (Composite)childControl;
                      byte b1;
                      int j;
                      Control[] arrayOfControl1;
                      for (j = (arrayOfControl1 = childComposite.getChildren()).length, b1 = 0; b1 < j; ) {
                        Control grandChildControl = arrayOfControl1[b1];
                        childStack.push(new PairControl(childComposite, grandChildControl));
                        b1++;
                      } 
                    } 
                    Point childSize = childControl.getSize();
                    if (childSize.x > 0 && childSize.y > 0) {
                      Image childImage = new Image((Device)SWTUtil.getDisplay(), childSize.x, childSize.y);
                      GC gc = new GC((Drawable)parentImage);
                      gc.copyArea(childImage, childLoc.x, childLoc.y);
                      gc.dispose();
                      childControl.setBackgroundImage(childImage);
                    } 
                  } 
                } 
              } 
            }
          });
    } 
  }
  
  public static Combo createCombo(Composite parent, String label, String tooltip, String[] contents) {
    return createCombo(parent, label, tooltip, contents, 0);
  }
  
  public static Combo createCombo(Composite parent, String label, String tooltip, String[] contents, int selectedIndex) {
    Label l = new Label(parent, 0);
    l.setText(label);
    l.setToolTipText(tooltip);
    l.setLayoutData(new GridData(1, 2, false, false));
    return createCombo(parent, contents, selectedIndex);
  }
  
  public static Combo createCombo(Composite parent, int style, String label, String tooltip, String[] contents, int selectedIndex) {
    Label l = new Label(parent, 0);
    l.setText(label);
    l.setToolTipText(tooltip);
    l.setLayoutData(new GridData(1, 2, false, false));
    return createCombo(parent, style, contents, selectedIndex);
  }
  
  public static Combo createCombo(Composite parent, String[] contents, int selectedIndex) {
    return createCombo(parent, 8, contents, selectedIndex);
  }
  
  public static Combo createCombo(Composite parent, int style, String[] contents, int selectedIndex) {
    Combo combo = new Combo(parent, style);
    for (int i = 0; i < contents.length; i++)
      combo.add(contents[i]); 
    combo.select(selectedIndex);
    return combo;
  }
  
  public static <E extends Enum<E>> Combo createEnumCombo(Composite parent, String label, String tooltip, Enum[] values, int selectedIndex) {
    String[] contents = StringUtil.enumNameToStringArray(values);
    for (int i = 0; i < contents.length; i++)
      contents[i] = contents[i].toLowerCase(); 
    return createCombo(parent, label, tooltip, contents, selectedIndex);
  }
  
  public static <E extends Enum<E>> Combo createEnumCombo(Composite parent, Enum[] values, int selectedIndex) {
    String[] contents = StringUtil.enumNameToStringArray(values);
    for (int i = 0; i < contents.length; i++)
      contents[i] = contents[i].toLowerCase(); 
    return createCombo(parent, contents, selectedIndex);
  }
  
  public static Spinner createSpinner(Composite parent, String label, String tooltip) {
    Label l = new Label(parent, 0);
    l.setText(label);
    l.setToolTipText(tooltip);
    l.setLayoutData(new GridData(1, 2, false, false));
    Spinner s = new Spinner(parent, 2048);
    return s;
  }
  
  public static Spinner createSpinner(Composite parent, String label, String tooltip, int minimum, int maximum) {
    Spinner s = createSpinner(parent, label, tooltip);
    s.setMaximum(maximum);
    s.setMinimum(minimum);
    return s;
  }
  
  public static Text createText(Composite parent, String label, String tooltip, int horizontalSpan, int verticalSpan) {
    Label l = new Label(parent, 0);
    l.setText(label);
    l.setToolTipText(tooltip);
    l.setLayoutData(new GridData(1, 2, false, false));
    Text t = new Text(parent, 2048);
    t.setLayoutData(new GridData(4, 4, true, false, horizontalSpan, verticalSpan));
    return t;
  }
  
  public static Button createFileBrowserButton(Composite parent, final Text parentText, final String[] extensions) {
    Button button = new Button(parent, 8);
    button.setText(Messages.BrowserButtonLabel);
    button.setLayoutData(new GridData(1, 2, false, false));
    button.addSelectionListener((SelectionListener)new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(parentText.getShell());
            dialog.setFilterExtensions(extensions);
            String projectPath = SWTUtil.getProjectPath();
            dialog.setFilterPath(projectPath);
            dialog.setText(Messages.DialogOpenLabel);
            if (dialog.open() != null) {
              String selectedFile = dialog.getFileName();
              String selectedPath = dialog.getFilterPath();
              if (!selectedPath.equals(projectPath)) {
                StringBuilder relativePath = (new StringBuilder(selectedPath.replace(String.valueOf(projectPath) + File.separator, ""))).append("/").append(selectedFile);
                parentText.setText(relativePath.toString());
              } else {
                parentText.setText(selectedFile);
              } 
            } 
          }
        });
    return button;
  }
  
  public static Button createCheckbox(Composite parent, String label, String tooltip, boolean selected, boolean enabled) {
    Button button = new Button(parent, 32);
    button.setLayoutData(new GridData(4, 4, false, false));
    button.setSelection(selected);
    button.setEnabled(enabled);
    button.setText(label);
    button.setToolTipText(tooltip);
    return button;
  }
  
  public static Button createButton(Composite parent, String label, String tooltip) {
    Button button = new Button(parent, 0);
    button.setLayoutData(new GridData(768));
    button.setText(label);
    button.setToolTipText(tooltip);
    return button;
  }
  
  public static Label createLabel(Composite parent, String text, String tooltip) {
    Label label = new Label(parent, 0);
    if (!StringUtil.isEmpty(text))
      label.setText(text); 
    if (!StringUtil.isEmpty(tooltip))
      label.setToolTipText(tooltip); 
    label.setLayoutData(new GridData(1, 2, false, false));
    return label;
  }
  
  public static RGB convertHexadecimalToRGB(String hexadecimal) {
    try {
      if (!hexadecimal.startsWith("#"))
        hexadecimal = "#" + hexadecimal; 
      int length = hexadecimal.length();
      if (length == 4) {
        int r = Integer.parseInt(hexadecimal.substring(1, 2), 16);
        int g = Integer.parseInt(hexadecimal.substring(2, 3), 16);
        int b = Integer.parseInt(hexadecimal.substring(3, 4), 16);
        return new RGB(r << 4 | r, g << 4 | g, b << 4 | b);
      } 
      if (length == 7)
        return new RGB(Integer.parseInt(hexadecimal.substring(1, 3), 16), Integer.parseInt(hexadecimal.substring(3, 5), 16), Integer.parseInt(hexadecimal.substring(5, 7), 16)); 
    } catch (NumberFormatException numberFormatException) {}
    return null;
  }
  
  public static String convertRGBToHexadecimal(RGB rgb) {
    int red = rgb.red;
    int green = rgb.green;
    int blue = rgb.blue;
    String redHexadecimal = Integer.toHexString(red);
    String greenHexadecimal = Integer.toHexString(green);
    String blueHexadecimal = Integer.toHexString(blue);
    if (redHexadecimal.length() == 1)
      redHexadecimal = "0" + redHexadecimal; 
    if (greenHexadecimal.length() == 1)
      greenHexadecimal = "0" + greenHexadecimal; 
    if (blueHexadecimal.length() == 1)
      blueHexadecimal = "0" + blueHexadecimal; 
    return "#" + redHexadecimal + greenHexadecimal + blueHexadecimal;
  }
  
  public static String getActivePerspectiveId() {
    IPerspectiveDescriptor perspective = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective();
    if (perspective == null)
      return ""; 
    return perspective.getId();
  }
  
  public static String getPerspectiveId(String name) {
    IPerspectiveRegistry perspectives = PlatformUI.getWorkbench().getPerspectiveRegistry();
    IPerspectiveDescriptor perspective = perspectives.findPerspectiveWithLabel(name);
    return (perspective != null) ? perspective.getId() : null;
  }
  
  public static void showPerspective(String perspectiveId) throws WorkbenchException {
    if (perspectiveId != null)
      PlatformUI.getWorkbench().showPerspective(perspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow()); 
  }
  
  public static Font deriveFont(Font font, int style) {
    FontData[] fontData = font.getFontData();
    byte b;
    int i;
    FontData[] arrayOfFontData1;
    for (i = (arrayOfFontData1 = fontData).length, b = 0; b < i; ) {
      FontData fontDatum = arrayOfFontData1[b];
      fontDatum.setStyle(fontDatum.getStyle() | style);
      b++;
    } 
    return new Font((Device)getDisplay(), fontData);
  }
  
  public static Region getTrimmedRegion(Image image, int alpha) {
    if (image == null)
      return null; 
    if (alpha < 0)
      alpha = 0; 
    ImageData imageData = image.getImageData();
    int width = imageData.width;
    int height = imageData.height;
    Region region = new Region();
    region.add(new Rectangle(0, 0, width, height));
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (alpha >= imageData.getAlpha(i, j))
          region.subtract(i, j, 1, 1); 
      } 
    } 
    return region;
  }
}
