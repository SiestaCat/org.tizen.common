package org.tizen.common.rds.ui.preference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;

public abstract class PropertyAndPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {
  private IStatus fBlockStatus = new StatusInfo();
  
  private ControlEnableState fBlockEnableState = null;
  
  private IProject fProject = null;
  
  private Map<String, Object> fData = null;
  
  private Control fConfigurationBlockControl;
  
  private Link fChangeWorkspaceSettings;
  
  private SelectionButtonDialogField fUseProjectSettings;
  
  private Composite fParentComposite;
  
  public static final String DATA_NO_LINK = "PropertyAndPreferencePage.nolink";
  
  protected abstract Control createPreferenceContent(Composite paramComposite);
  
  protected abstract boolean hasProjectSpecificOptions(IProject paramIProject);
  
  protected abstract String getPreferencePageID();
  
  protected abstract String getPropertyPageID();
  
  protected boolean supportsProjectSpecificOptions() {
    return (getPropertyPageID() != null);
  }
  
  protected boolean offerLink() {
    return !(this.fData != null && Boolean.TRUE.equals(this.fData.get("PropertyAndPreferencePage.nolink")));
  }
  
  protected Label createDescriptionLabel(Composite parent) {
    this.fParentComposite = parent;
    if (isProjectPreferencePage()) {
      Composite composite = new Composite(parent, 0);
      composite.setFont(parent.getFont());
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.numColumns = 2;
      composite.setLayout((Layout)layout);
      composite.setLayoutData(new GridData(4, 16777216, true, false));
      IDialogFieldListener listener = new IDialogFieldListener() {
          public void dialogFieldChanged(DialogField field) {
            if (field instanceof SelectionButtonDialogField) {
              boolean enabled = ((SelectionButtonDialogField)field).isSelected();
              PropertyAndPreferencePage.this.enableProjectSpecificSettings(enabled);
              if (enabled && PropertyAndPreferencePage.this.getData() != null)
                PropertyAndPreferencePage.this.applyData(PropertyAndPreferencePage.this.getData()); 
            } 
          }
        };
      this.fUseProjectSettings = new SelectionButtonDialogField(32);
      this.fUseProjectSettings.setDialogFieldListener(listener);
      this.fUseProjectSettings.setLabelText("Enable Pr&oject specific settings.");
      this.fUseProjectSettings.doFillIntoGrid(composite, 1);
      LayoutUtil.setHorizontalGrabbing((Control)this.fUseProjectSettings.getSelectionButton((Composite)null));
      if (offerLink()) {
        this.fChangeWorkspaceSettings = createLink(composite, "Configure Workspace Settings...");
        this.fChangeWorkspaceSettings.setLayoutData(new GridData(16777224, 16777216, false, false));
      } else {
        LayoutUtil.setHorizontalSpan((Control)this.fUseProjectSettings.getSelectionButton((Composite)null), 2);
      } 
      Label horizontalLine = new Label(composite, 258);
      horizontalLine.setLayoutData(new GridData(4, 4, true, false, 2, 1));
      horizontalLine.setFont(composite.getFont());
    } else if (supportsProjectSpecificOptions() && offerLink()) {
      this.fChangeWorkspaceSettings = createLink(parent, "Configure Project Specific Settings...");
      this.fChangeWorkspaceSettings.setLayoutData(new GridData(16777224, 16777216, true, false));
    } 
    return super.createDescriptionLabel(parent);
  }
  
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, 0);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    composite.setLayout((Layout)layout);
    composite.setFont(parent.getFont());
    GridData data = new GridData(4, 4, true, true);
    this.fConfigurationBlockControl = createPreferenceContent(composite);
    this.fConfigurationBlockControl.setLayoutData(data);
    if (isProjectPreferencePage()) {
      boolean useProjectSettings = hasProjectSpecificOptions(getProject());
      enableProjectSpecificSettings(useProjectSettings);
    } 
    Dialog.applyDialogFont((Control)composite);
    return (Control)composite;
  }
  
  private Link createLink(Composite composite, String text) {
    Link link = new Link(composite, 0);
    link.setFont(composite.getFont());
    link.setText("<A>" + text + "</A>");
    link.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            PropertyAndPreferencePage.this.doLinkActivated((Link)e.widget);
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {
            PropertyAndPreferencePage.this.doLinkActivated((Link)e.widget);
          }
        });
    return link;
  }
  
  protected boolean useProjectSettings() {
    return (isProjectPreferencePage() && this.fUseProjectSettings != null && this.fUseProjectSettings.isSelected());
  }
  
  protected boolean isProjectPreferencePage() {
    return (this.fProject != null);
  }
  
  protected IProject getProject() {
    return this.fProject;
  }
  
  final void doLinkActivated(Link link) {
    Map<String, Object> data = getData();
    if (data == null)
      data = new HashMap<>(); 
    data.put("PropertyAndPreferencePage.nolink", Boolean.TRUE);
    if (isProjectPreferencePage()) {
      openWorkspacePreferences(data);
    } else {
      HashSet<IProject> projectsWithSpecifics = new HashSet<>();
      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      for (int i = 0; i < projects.length; i++) {
        IProject curr = projects[i];
        if (hasProjectSpecificOptions(curr.getProject()))
          projectsWithSpecifics.add(curr); 
      } 
      ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics);
      if (dialog.open() == 0) {
        IProject res = (IProject)dialog.getFirstResult();
        if (res != null)
          openProjectProperties(res.getProject(), data); 
      } 
    } 
  }
  
  protected final void openWorkspacePreferences(Object data) {
    String id = getPreferencePageID();
    PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, data);
    if (dialog != null)
      dialog.open(); 
  }
  
  protected final void openProjectProperties(IProject project, Object data) {
    String id = getPropertyPageID();
    if (id != null) {
      PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(getShell(), (IAdaptable)project, id, new String[] { id }, data);
      if (dialog != null)
        dialog.open(); 
    } 
  }
  
  protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
    this.fUseProjectSettings.setSelection(useProjectSpecificSettings);
    enablePreferenceContent(useProjectSpecificSettings);
    updateLinkVisibility();
    doStatusChanged();
  }
  
  private void updateLinkVisibility() {
    if (this.fChangeWorkspaceSettings == null || this.fChangeWorkspaceSettings.isDisposed())
      return; 
    if (isProjectPreferencePage())
      this.fChangeWorkspaceSettings.setEnabled(!useProjectSettings()); 
  }
  
  protected void setPreferenceContentStatus(IStatus status) {
    this.fBlockStatus = status;
    doStatusChanged();
  }
  
  protected IStatus getPreferenceContentStatus() {
    return this.fBlockStatus;
  }
  
  protected void doStatusChanged() {
    if (!isProjectPreferencePage() || useProjectSettings()) {
      updateStatus(this.fBlockStatus);
    } else {
      updateStatus(new StatusInfo());
    } 
  }
  
  protected void enablePreferenceContent(boolean enable) {
    if (enable) {
      if (this.fBlockEnableState != null) {
        this.fBlockEnableState.restore();
        this.fBlockEnableState = null;
      } 
    } else if (this.fBlockEnableState == null) {
      this.fBlockEnableState = ControlEnableState.disable(this.fConfigurationBlockControl);
    } 
  }
  
  protected void performDefaults() {
    if (useProjectSettings())
      enableProjectSpecificSettings(false); 
    super.performDefaults();
  }
  
  private void updateStatus(IStatus status) {
    setValid(!status.matches(4));
    StatusUtil.applyToStatusLine((DialogPage)this, status);
  }
  
  public IAdaptable getElement() {
    return (IAdaptable)this.fProject;
  }
  
  public void setElement(IAdaptable element) {
    if (element instanceof IResource)
      if (element instanceof IProject) {
        this.fProject = (IProject)element.getAdapter(IResource.class);
      } else if (element instanceof IFile) {
        IFile file = (IFile)element.getAdapter(IFile.class);
        this.fProject = file.getProject();
      } else if (element instanceof IFolder) {
        IFolder folder = (IFolder)element.getAdapter(IFolder.class);
        this.fProject = folder.getProject();
      }  
  }
  
  public void applyData(Object data) {
    if (data instanceof Map)
      this.fData = (Map<String, Object>)data; 
    if (this.fChangeWorkspaceSettings != null && 
      !offerLink()) {
      this.fChangeWorkspaceSettings.dispose();
      this.fParentComposite.layout(true, true);
    } 
  }
  
  protected Map<String, Object> getData() {
    return this.fData;
  }
}
