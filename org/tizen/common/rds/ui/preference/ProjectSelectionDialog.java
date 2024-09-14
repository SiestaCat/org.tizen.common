package org.tizen.common.rds.ui.preference;

import java.util.ArrayList;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.tizen.common.CommonPlugin;

public class ProjectSelectionDialog extends SelectionStatusDialog {
  private TableViewer fTableViewer;
  
  private Set<IProject> fProjectsWithSpecifics;
  
  private static final int SIZING_SELECTION_WIDGET_HEIGHT = 250;
  
  private static final int SIZING_SELECTION_WIDGET_WIDTH = 300;
  
  private static final String DIALOG_SETTINGS_SHOW_ALL = "ProjectSelectionDialog.show_all";
  
  private ViewerFilter fFilter;
  
  public ProjectSelectionDialog(Shell parentShell, Set<IProject> projectsWithSpecifics) {
    super(parentShell);
    setTitle("Project Specific Configuration");
    setMessage("&Select the project to configure:");
    this.fProjectsWithSpecifics = projectsWithSpecifics;
    this.fFilter = new ViewerFilter() {
        public boolean select(Viewer viewer, Object parentElement, Object element) {
          return ProjectSelectionDialog.this.fProjectsWithSpecifics.contains(element);
        }
      };
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite)super.createDialogArea(parent);
    Font font = parent.getFont();
    composite.setFont(font);
    createMessageArea(composite);
    this.fTableViewer = new TableViewer(composite, 2816);
    this.fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
          public void selectionChanged(SelectionChangedEvent event) {
            ProjectSelectionDialog.this.doSelectionChanged(((IStructuredSelection)event.getSelection()).toArray());
          }
        });
    this.fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
          public void doubleClick(DoubleClickEvent event) {
            ProjectSelectionDialog.this.okPressed();
          }
        });
    GridData data = new GridData(4, 4, true, true);
    data.heightHint = 250;
    data.widthHint = 300;
    this.fTableViewer.getTable().setLayoutData(data);
    this.fTableViewer.setLabelProvider((IBaseLabelProvider)WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    this.fTableViewer.setContentProvider((IContentProvider)getContentProvider());
    this.fTableViewer.getControl().setFont(font);
    this.fTableViewer.setSorter(new ViewerSorter());
    Button checkbox = new Button(composite, 32);
    checkbox.setText("Show only &projects with project specific settings");
    checkbox.setLayoutData(new GridData(1, 16777216, true, false));
    checkbox.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            ProjectSelectionDialog.this.updateFilter(((Button)e.widget).getSelection());
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {
            ProjectSelectionDialog.this.updateFilter(((Button)e.widget).getSelection());
          }
        });
    IDialogSettings dialogSettings = CommonPlugin.getDefault().getDialogSettings();
    boolean doFilter = (!dialogSettings.getBoolean("ProjectSelectionDialog.show_all") && !this.fProjectsWithSpecifics.isEmpty());
    checkbox.setSelection(doFilter);
    updateFilter(doFilter);
    this.fTableViewer.setInput(ResourcesPlugin.getWorkspace());
    doSelectionChanged(new Object[0]);
    Dialog.applyDialogFont((Control)composite);
    return (Control)composite;
  }
  
  protected void updateFilter(boolean selected) {
    if (selected) {
      this.fTableViewer.addFilter(this.fFilter);
    } else {
      this.fTableViewer.removeFilter(this.fFilter);
    } 
    CommonPlugin.getDefault().getDialogSettings().put("ProjectSelectionDialog.show_all", !selected);
  }
  
  private void doSelectionChanged(Object[] objects) {
    if (objects.length != 1) {
      updateStatus(new StatusInfo(4, ""));
      setSelectionResult(null);
    } else {
      updateStatus(new StatusInfo());
      setSelectionResult(objects);
    } 
  }
  
  protected void computeResult() {}
  
  protected IStructuredContentProvider getContentProvider() {
    return (IStructuredContentProvider)new WorkbenchContentProvider() {
        public Object[] getChildren(Object element) {
          if (!(element instanceof IWorkspace))
            return new Object[0]; 
          IProject[] projects = ((IWorkspace)element).getRoot().getProjects();
          ArrayList<IProject> projectList = new ArrayList<>();
          byte b;
          int i;
          IProject[] arrayOfIProject1;
          for (i = (arrayOfIProject1 = projects).length, b = 0; b < i; ) {
            IProject project = arrayOfIProject1[b];
            if (project.isOpen())
              projectList.add(project); 
            b++;
          } 
          return projectList.toArray();
        }
      };
  }
}
