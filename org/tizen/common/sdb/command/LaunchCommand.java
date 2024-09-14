package org.tizen.common.sdb.command;

import java.util.ArrayList;
import java.util.List;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.common.util.OSChecker;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.service.ApplicationCmdService;

public class LaunchCommand extends ApplicationCommand {
  protected String widgetId = null;
  
  protected String args = "";
  
  protected boolean isUseWidgetViewer = false;
  
  protected String WIDGET_ID_FORMAT_WIN = "\\\"%s\\\"";
  
  protected String WIDGET_ID_FORMAT_OTHERS = "\"%s\"";
  
  public LaunchCommand(IDevice device, String widgetId, String args, ITizenConsoleManager console) {
    super(device, console);
    this.widgetId = widgetId;
    this.args = (args != null) ? args : "";
  }
  
  public LaunchCommand(IDevice device, String widgetId, String args, boolean isUseWidgetViewerSdk, ITizenConsoleManager console) {
    super(device, console);
    this.widgetId = widgetId;
    this.args = args;
    this.isUseWidgetViewer = isUseWidgetViewerSdk;
  }
  
  protected LaunchCommand(IDevice device, ITizenConsoleManager console) {
    super(device, console);
  }
  
  protected List<String> getCommand() {
    List<String> command = new ArrayList<>();
    command.add(getSdbPath());
    command.add("-s");
    command.add(this.device.getSerialNumber());
    command.add("launch");
    command.add("-a");
    command.addAll(splitParams(getWidgetId()));
    command.add("-p");
    command.add("-e");
    command.add("-m");
    command.add("run");
    command.addAll(splitParams(getArgs()));
    return command;
  }
  
  protected List<String> splitParams(String param) {
    ArrayList<String> results = new ArrayList<>();
    if (!param.isEmpty()) {
      String[] splitArgs = param.split(" ");
      for (int i = 0; i < splitArgs.length; i++)
        results.add(splitArgs[i]); 
    } 
    return results;
  }
  
  protected void executeCommand() throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      if (!appcmd.runApplication(getWidgetId(), (IShellOutputReceiver)this.receiver))
        newCoreException("Launch command failed", null); 
    } else {
      this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
      this.helper.runHostCommand(getCommand());
    } 
  }
  
  protected String getWidgetId() {
    if (isUseWidgetViewer())
      this.widgetId = "org.tizen.widget_viewer_sdk widget_id " + this.widgetId; 
    String format = this.WIDGET_ID_FORMAT_OTHERS;
    if (OSChecker.isWindows() && !ApplicationCommand.isAppCmdSupported(getDevice()))
      format = this.WIDGET_ID_FORMAT_WIN; 
    return String.format(format, new Object[] { this.widgetId });
  }
  
  protected String getArgs() {
    return this.args;
  }
  
  public String getCommandOutput() {
    if (this.helper != null)
      return this.helper.getCommandOutput(); 
    if (this.receiver != null)
      return this.receiver.getCommandOutput(); 
    return null;
  }
  
  protected boolean isUseWidgetViewer() {
    return this.isUseWidgetViewer;
  }
}
