package org.tizen.common.rds.ui.preference;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.CommonPlugin;
import org.tizen.common.ui.page.preference.Messages;
import org.tizen.common.util.ProjectUtil;

public class RdsPreferencePage extends PropertyAndPreferencePage implements IWorkbenchPreferencePage {
  private static Logger logger = LoggerFactory.getLogger(RdsPreferencePage.class);
  
  public static final String RDS_OPTION_ID = "org.tizen.common.option.rds";
  
  public static final String RDS_PREFERENCE_PAGE_ID = "org.tizen.common.preferences.rds";
  
  public static final String RDS_PROPERTIES_PAGE_ID = "org.tizen.common.properties.rds";
  
  public static final QualifiedName RDS_PROPERTIES_SPECIFIC_OPTION_NAME = new QualifiedName("org.tizen.common.properties.rds", "org.tizen.common.preferences.rds");
  
  public static final QualifiedName RDS_PROPERTIES_RDS_OPTION_NAME = new QualifiedName("org.tizen.common.properties.rds", "org.tizen.common.option.rds");
  
  public static final String OPTION_ENABLE = "true";
  
  public static final String OPTION_DISABLE = "false";
  
  public static final boolean RDS_MODE_DEFAULT = false;
  
  private IPreferenceStore prefStore = CommonPlugin.getDefault().getPreferenceStore();
  
  Button btnRdsCheck;
  
  public void init(IWorkbench workbench) {}
  
  protected Control createPreferenceContent(Composite composite) {
    createRdsCheck(composite);
    return (Control)composite;
  }
  
  private void createRdsCheck(Composite parent) {
    Composite rdsComposite = new Composite(parent, 0);
    GridData gridData = new GridData(768);
    gridData.horizontalSpan = 1;
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 5;
    gridLayout.marginWidth = 5;
    rdsComposite.setLayoutData(gridData);
    rdsComposite.setLayout((Layout)gridLayout);
    this.btnRdsCheck = new Button(rdsComposite, 32);
    this.btnRdsCheck.setText(Messages.RDS_MODE_PREFERENCE);
    initRdsCheck(false);
    createNoteComposite(JFaceResources.getDialogFont(), rdsComposite, "Note:", Messages.RDS_NOTE);
  }
  
  private void initRdsCheck(boolean isDefault) {
    if (this.btnRdsCheck == null)
      return; 
    boolean isProperty = isProjectPreferencePage();
    if (!isProperty) {
      if (isDefault) {
        this.btnRdsCheck.setSelection(this.prefStore.getDefaultBoolean("org.tizen.common.option.rds"));
      } else {
        this.btnRdsCheck.setSelection(this.prefStore.getBoolean("org.tizen.common.option.rds"));
      } 
    } else {
      boolean isEnabled = this.btnRdsCheck.getEnabled();
      if (!isEnabled)
        this.btnRdsCheck.setEnabled(true); 
      if (isDefault) {
        this.btnRdsCheck.setSelection(this.prefStore.getDefaultBoolean("org.tizen.common.option.rds"));
      } else {
        boolean isEnable = false;
        try {
          if ("true".equals(getProject().getPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME))) {
            isEnable = true;
          } else {
            isEnable = false;
          } 
        } catch (CoreException coreException) {
          isEnable = true;
        } 
        this.btnRdsCheck.setSelection(isEnable);
      } 
      if (!isEnabled)
        this.btnRdsCheck.setEnabled(false); 
    } 
  }
  
  protected boolean hasProjectSpecificOptions(IProject project) {
    boolean isSpecificOption = false;
    String specificOption = "";
    try {
      specificOption = project.getPersistentProperty(RDS_PROPERTIES_SPECIFIC_OPTION_NAME);
    } catch (CoreException coreException) {
      isSpecificOption = false;
    } 
    if ("true".equals(specificOption)) {
      isSpecificOption = true;
    } else {
      isSpecificOption = false;
    } 
    return isSpecificOption;
  }
  
  protected String getPreferencePageID() {
    return "org.tizen.common.preferences.rds";
  }
  
  protected String getPropertyPageID() {
    return "org.tizen.common.properties.rds";
  }
  
  public void performApply() {
    if (isProjectPreferencePage()) {
      IProject project = getProject();
      try {
        if (useProjectSettings()) {
          project.setPersistentProperty(RDS_PROPERTIES_SPECIFIC_OPTION_NAME, "true");
        } else {
          project.setPersistentProperty(RDS_PROPERTIES_SPECIFIC_OPTION_NAME, "false");
        } 
        if (this.btnRdsCheck.getSelection()) {
          project.setPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME, "true");
        } else {
          project.setPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME, "false");
        } 
      } catch (CoreException e) {
        logger.error(Messages.RDS_OPTION_SAVE_FAIL, (Throwable)e);
      } 
    } else {
      this.prefStore.setValue("org.tizen.common.option.rds", this.btnRdsCheck.getSelection());
    } 
  }
  
  public boolean getRdsOption() {
    boolean isRdsOption = false;
    IProject project = getProject();
    if (isProjectPreferencePage()) {
      String rdsOption = null;
      try {
        rdsOption = project.getPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME);
      } catch (CoreException coreException) {
        isRdsOption = false;
      } 
      isRdsOption = "true".equals(rdsOption);
    } else {
      isRdsOption = this.prefStore.getBoolean("org.tizen.common.option.rds");
    } 
    return isRdsOption;
  }
  
  public void performDefaults() {
    initRdsCheck(true);
    super.performDefaults();
  }
  
  public boolean performOk() {
    performApply();
    return super.performOk();
  }
  
  public static boolean isRdsMode(IProject project) {
    String projectSpecificOption = "";
    String rdsOption = "false";
    if (project != null) {
      List<IProject> refProject = ProjectUtil.getReferencedProjects(project);
      if (!refProject.isEmpty())
        return false; 
    } 
    if (project != null)
      try {
        projectSpecificOption = project.getPersistentProperty(RDS_PROPERTIES_SPECIFIC_OPTION_NAME);
      } catch (CoreException e) {
        logger.error("Failed to read project specific option", (Throwable)e);
      }  
    if ("true".equals(projectSpecificOption)) {
      try {
        if (project != null)
          rdsOption = project.getPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME); 
      } catch (CoreException e) {
        logger.error("Failed to read RDS option", (Throwable)e);
      } 
      return "true".equals(rdsOption);
    } 
    IPreferenceStore prefStore = CommonPlugin.getDefault().getPreferenceStore();
    return prefStore.getBoolean("org.tizen.common.option.rds");
  }
  
  public static boolean isWebRdsMode(IProject project) {
    String projectSpecificOption = "";
    String rdsOption = "false";
    try {
      projectSpecificOption = project.getPersistentProperty(RDS_PROPERTIES_SPECIFIC_OPTION_NAME);
    } catch (CoreException e) {
      logger.error("Failed to read project specific option.", (Throwable)e);
    } 
    if ("true".equals(projectSpecificOption)) {
      try {
        rdsOption = project.getPersistentProperty(RDS_PROPERTIES_RDS_OPTION_NAME);
      } catch (CoreException e) {
        logger.error("Failed to read RDS option.", (Throwable)e);
      } 
      return "true".equals(rdsOption);
    } 
    IPreferenceStore prefStore = CommonPlugin.getDefault().getPreferenceStore();
    return prefStore.getBoolean("org.tizen.common.option.rds");
  }
}
