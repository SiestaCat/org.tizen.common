package org.tizen.common.ui.page.preference;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.tizen.common.CommonPlugin;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.util.SWTUtil;
import org.tizen.common.util.StringUtil;
import org.tizen.common.util.log.FileAppender;
import org.tizen.common.util.log.Level;

public class LoggingPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  private boolean applyOk = true;
  
  public static final String OPTION_ID_LEVEL = "org.tizen.common.logger.level";
  
  public static final String OPTION_ID_LOCATION = "org.tizen.common.logger.location2";
  
  public static final String OPTION_ID_CP = "org.tizen.common.logger.cp";
  
  private static final int DEFAULT_LEVEL = 1;
  
  private static final Level[] LEVELS = new Level[] { Level.OFF, Level.ERROR, Level.INFO, Level.DEBUG, Level.ALL };
  
  private Text loggerCP;
  
  private Text loggerLocText;
  
  private Scale levelScale;
  
  private Label levelDesLabel;
  
  private static final IPreferenceStore prefStore = (CommonPlugin.getDefault() == null) ? null : CommonPlugin.getDefault().getPreferenceStore();
  
  public LoggingPreferencePage() {
    setPreferenceStore(CommonPlugin.getDefault().getPreferenceStore());
    setDescription(Messages.LOGGING_DESCRIPTION);
  }
  
  public void init(IWorkbench workbench) {}
  
  protected void createFieldEditors() {
    Composite composite = getFieldEditorParent();
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    composite.setLayout((Layout)layout);
    GridData data = new GridData(1808);
    data.verticalAlignment = 4;
    data.horizontalAlignment = 4;
    composite.setLayoutData(data);
    SWTUtil.createSpacer(composite, 1);
    createLoggerPreferences(composite);
  }
  
  private void createLoggerPreferences(Composite parent) {
    Group group = SWTUtil.createGroup(parent, Messages.LOGGING_GROUP, 1);
    SWTUtil.setGridLayout((Composite)group, 2, false, -1, -1, -1, -1, 768);
    createLoggerLocationButton((Composite)group);
    createLoggerConversionPatterhn((Composite)group);
    createLoggerLevelButtons((Composite)group);
  }
  
  private void createLoggerConversionPatterhn(Composite parent) {
    Label loggerLocationLabel = new Label(parent, 0);
    loggerLocationLabel.setText(Messages.LOGGING_CP);
    this.loggerCP = new Text(parent, 2048);
    SWTUtil.setGridLayoutData((Control)this.loggerCP, -1, -1, -1, -1, 768);
    this.loggerCP.setText(getLogConversionPattern());
  }
  
  private void createLoggerLocationButton(Composite parent) {
    Label loggerLocationLabel = new Label(parent, 0);
    loggerLocationLabel.setText(Messages.LOGGING_LOCATION);
    this.loggerLocText = new Text(parent, 2048);
    SWTUtil.setGridLayoutData((Control)this.loggerLocText, -1, -1, -1, -1, 768);
    this.loggerLocText.setText(getLogLocation());
  }
  
  public static String getLogConversionPattern() {
    String loggerCP = prefStore.getString("org.tizen.common.logger.cp");
    if (StringUtil.isEmpty(loggerCP))
      loggerCP = "[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n"; 
    return loggerCP;
  }
  
  public static String getLogLocation() {
    String loggerPath = prefStore.getString("org.tizen.common.logger.location2");
    if (StringUtil.isEmpty(loggerPath))
      loggerPath = "%X{userdata_log}/ide-%d{yyyyMMdd}_%d{HHmmss}.log"; 
    return loggerPath;
  }
  
  private void createLoggerLevelButtons(Composite parent) {
    String selectedLevel = prefStore.getString("org.tizen.common.logger.level");
    if (StringUtil.isEmpty(selectedLevel))
      selectedLevel = LEVELS[1].toString(); 
    this.levelScale = new Scale(parent, 256);
    SWTUtil.setGridLayoutData((Control)this.levelScale, -1, -1, 2, -1, 768);
    this.levelScale.setMinimum(0);
    this.levelScale.setMaximum(LEVELS.length - 1);
    this.levelScale.setIncrement(1);
    this.levelScale.setPageIncrement(1);
    boolean levelDefined = false;
    for (int i = 0; i < LEVELS.length; i++) {
      Level level = LEVELS[i];
      if (level.toString().equals(selectedLevel)) {
        levelDefined = true;
        this.levelScale.setSelection(i);
        break;
      } 
    } 
    if (!levelDefined)
      this.levelScale.setSelection(1); 
    this.levelDesLabel = new Label(parent, 0);
    SWTUtil.setGridLayoutData((Control)this.levelDesLabel, -1, -1, 2, -1, 768);
    this.levelDesLabel.setText(LEVELS[this.levelScale.getSelection()].toString());
    this.levelScale.addMouseListener(new MouseListener() {
          public void mouseUp(MouseEvent e) {
            int selectedLevel = LoggingPreferencePage.this.levelScale.getSelection();
            LoggingPreferencePage.this.levelScale.setSelection(selectedLevel);
          }
          
          public void mouseDown(MouseEvent e) {}
          
          public void mouseDoubleClick(MouseEvent e) {}
        });
    this.levelScale.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            LoggingPreferencePage.this.levelDesLabel.setText(LoggingPreferencePage.LEVELS[LoggingPreferencePage.this.levelScale.getSelection()].toString());
          }
          
          public void widgetDefaultSelected(SelectionEvent e) {}
        });
  }
  
  protected void performDefaults() {
    this.levelScale.setSelection(1);
    this.levelDesLabel.setText(LEVELS[1].toString());
    this.loggerLocText.setText("%X{userdata_log}/ide-%d{yyyyMMdd}_%d{HHmmss}.log");
    this.loggerCP.setText("[%d{yyyy.MM.dd HH:mm:ss}][%-5p] %F(%L) - %m%n");
    super.performDefaults();
  }
  
  protected void performApply() {
    Level loggerLevel = LEVELS[this.levelScale.getSelection()];
    String loggerLoc = this.loggerLocText.getText();
    MDC.put("workspace", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
    MDC.put("tizensdk", InstallPathConfig.getSDKPath());
    MDC.put("userdata_log", InstallPathConfig.getIdeUserDataLogPath());
    Logger rootLogger = Logger.getRootLogger();
    FileAppender fileAppender = (FileAppender)rootLogger.getAppender("TIZEN_FILE_APPENDER");
    try {
      if (fileAppender != null) {
        String result = fileAppender.setFilePath(loggerLoc);
        if (!StringUtil.isEmpty(result)) {
          setErrorMessage(result);
          this.applyOk = false;
          return;
        } 
      } 
    } finally {
      MDC.remove("tizensdk");
      MDC.remove("workspace");
      MDC.remove("userdata_log");
    } 
    prefStore.setValue("org.tizen.common.logger.location2", this.loggerLocText.getText());
    prefStore.setValue("org.tizen.common.logger.level", loggerLevel.toString());
    prefStore.setValue("org.tizen.common.logger.cp", this.loggerCP.getText());
    setErrorMessage(null);
    this.applyOk = true;
  }
  
  public boolean performOk() {
    performApply();
    if (!this.applyOk)
      return false; 
    return super.performOk();
  }
  
  public static Level getLoggerLevel() {
    return getLoggerLevel((String)null);
  }
  
  public static Level getLoggerLevel(String levelString) {
    if (StringUtil.isEmpty(levelString))
      levelString = prefStore.getString("org.tizen.common.logger.level"); 
    if (!StringUtil.isEmpty(levelString)) {
      byte b;
      int i;
      Level[] arrayOfLevel;
      for (i = (arrayOfLevel = LEVELS).length, b = 0; b < i; ) {
        Level level = arrayOfLevel[b];
        if (level.toString().equals(levelString))
          return level; 
        b++;
      } 
    } 
    return LEVELS[1];
  }
}
