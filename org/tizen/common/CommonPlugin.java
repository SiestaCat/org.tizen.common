package org.tizen.common;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.core.command.EclipseExecutor;
import org.tizen.common.core.command.Executor;
import org.tizen.common.core.command.Prompter;
import org.tizen.common.core.command.prompter.EclipsePrompter;
import org.tizen.common.core.command.prompter.SWTPrompter;
import org.tizen.common.launch.ProjectDeletionListener;
import org.tizen.common.util.DialogUtil;
import org.tizen.common.util.HostUtil;
import org.tizen.common.util.log.TizenLog4jConfigurator;
import org.tizen.sdblib.SmartDevelopmentBridge;
import org.tizen.sdblib.service.CrashReportService;

public class CommonPlugin extends AbstractUIPlugin {
  public static final String EXTENTION_POINT_CRASHREPORTER = "org.tizen.common.crashreporter";
  
  public static final String EXTENTION_POINT_PROMPTER = "org.tizen.common.prompter";
  
  public static final String EXTENTION_POINT_PROJECT_ADAPTER = "org.tizen.common.project.adapter";
  
  public static final String PLUGIN_ID = "org.tizen.common";
  
  public static final String PRODUCT_NAME = "TizenStudio";
  
  private static CommonPlugin plugin;
  
  protected EclipsePrompter prompter = null;
  
  protected Executor executor = null;
  
  protected long startTime;
  
  public void start(BundleContext context) throws Exception {
    super.start(context);
    getPreferenceStore().addPropertyChangeListener(new TizenLog4jConfigurator());
    plugin = this;
    this.startTime = System.currentTimeMillis() / 1000L;
    RemoteLogger.logAccess("TizenStudio");
    initializeExecutor();
    initializeSmartDeviceBridge();
    initDeleteProjectforFastDeploy();
  }
  
  public void stop(BundleContext context) throws Exception {
    long endTime = System.currentTimeMillis() / 1000L;
    long usage = endTime - this.startTime;
    RemoteLogger.logUsage("TizenStudio", usage);
    destroyCrashReportService();
    finalizeSmartDeviceBridge();
    finalizeExecutor();
    plugin = null;
    super.stop(context);
  }
  
  public static void setDefault(CommonPlugin plugin) {
    CommonPlugin.plugin = plugin;
  }
  
  public static CommonPlugin getDefault() {
    return plugin;
  }
  
  protected void initializeDefaultPreferences(IPreferenceStore store) {
    if (InstallPathConfig.getSDKPath() != null)
      store.setDefault("sdkpath", InstallPathConfig.getSDKPath()); 
    store.setDefault("org.tizen.common.option.rds", false);
    store.setDefault("response_timeout", 300000);
    IPreferenceStore debugPrefStore = DebugUIPlugin.getDefault().getPreferenceStore();
    debugPrefStore.setDefault("org.eclipse.debug.ui.switch_to_perspective", "never");
    debugPrefStore.setDefault("org.eclipse.debug.ui.switch_perspective_on_suspend", "always");
  }
  
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }
  
  public Executor getExecutor() {
    return this.executor;
  }
  
  public Prompter getPrompter() {
    return this.prompter;
  }
  
  protected void initializeExecutor() {
    this.prompter = new EclipsePrompter(new SWTPrompter());
    this.executor = new EclipseExecutor(this.prompter);
  }
  
  protected void finalizeExecutor() {}
  
  protected void initializeSmartDeviceBridge() {
    String sdbPath = InstallPathConfig.getSDBPath();
    if (!HostUtil.exists(sdbPath)) {
      DialogUtil.openMessageDialog(String.format("There is no %s.", new Object[] { sdbPath }));
    } else {
      SmartDevelopmentBridge.createBridge(sdbPath);
    } 
  }
  
  protected void finalizeSmartDeviceBridge() {
    SmartDevelopmentBridge.disconnectBridge();
  }
  
  protected void destroyCrashReportService() {
    CrashReportService.getDefault().down();
  }
  
  private void initDeleteProjectforFastDeploy() {
    ProjectDeletionListener projectDeletionListener = new ProjectDeletionListener();
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    workspace.addResourceChangeListener(projectDeletionListener, 4);
  }
}
