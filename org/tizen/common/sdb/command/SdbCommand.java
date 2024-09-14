package org.tizen.common.sdb.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.CommonPlugin;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.common.sdb.command.message.CommandErrorException;
import org.tizen.common.sdb.command.message.CommandErrorType;
import org.tizen.common.sdb.command.receiver.CommandOutputReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.common.util.HostUtil;
import org.tizen.common.util.StringUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.exception.SdbCommandRejectedException;
import org.tizen.sdblib.exception.ShellCommandUnresponsiveException;
import org.tizen.sdblib.exception.TimeoutException;

public class SdbCommand {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Deprecated
  public static final int DEFAULT_TIMEOUT = 60000;
  
  private final String SEPARATOR = System.getProperty("line.separator");
  
  private IDevice device = null;
  
  private ITizenConsoleManager console = null;
  
  private String endLine = "";
  
  private String commandOutput = "";
  
  private CommandOutputReceiver receiver = null;
  
  private String sdbPath;
  
  private boolean showCommand = false;
  
  private int defaultTimeout;
  
  public SdbCommand(IDevice device) {
    this(device, null);
  }
  
  public SdbCommand(IDevice device, ITizenConsoleManager console) {
    this(device, console, new CommandOutputReceiver(console));
  }
  
  public SdbCommand(IDevice device, ITizenConsoleManager console, CommandOutputReceiver receiver) {
    this(device, console, receiver, CommonPlugin.getDefault().getPreferenceStore().getInt("response_timeout"));
  }
  
  public SdbCommand(IDevice device, ITizenConsoleManager console, CommandOutputReceiver receiver, int timeout) {
    this.device = device;
    this.console = console;
    this.receiver = receiver;
    this.sdbPath = InstallPathConfig.getSDBPath();
    this.defaultTimeout = timeout;
  }
  
  public void runCommand(String command) throws Exception {
    print("$ " + command);
    this.device.executeShellCommand(command, (IShellOutputReceiver)this.receiver);
    this.endLine = this.receiver.getEndLine();
    this.commandOutput = this.receiver.getCommandOutput();
  }
  
  public String runLaunchCommand(String command) throws IOException {
    runLaunchCommand(command, true);
    return this.commandOutput;
  }
  
  private Process runHostCommandImpl(String command, String sdbCommand, boolean isBlock, IShellOutputReceiver userReceiver, boolean isLaunch) throws IOException {
    print("$ " + sdbCommand);
    Process process = null;
    IShellOutputReceiver r = (userReceiver != null) ? userReceiver : (IShellOutputReceiver)this.receiver;
    if (isBlock) {
      if (isLaunch) {
        this.device.executeLaunchCommand(command, r);
      } else {
        this.device.executeHostCommand(command, r);
      } 
      if (userReceiver == null) {
        this.endLine = this.receiver.getEndLine();
        this.commandOutput = this.receiver.getCommandOutput();
      } 
    } else {
      process = Runtime.getRuntime().exec(sdbCommand);
    } 
    return process;
  }
  
  public Process runDebugLaunchCommand(String command) throws IOException {
    List<String> commandArr = new ArrayList<>();
    commandArr.add(this.sdbPath);
    commandArr.add("-s");
    commandArr.add(this.device.getSerialNumber());
    commandArr.add("launch");
    String[] cmds = command.split(" ");
    for (int i = 0; i < cmds.length; i++)
      commandArr.add(cmds[i]); 
    ProcessBuilder pb = new ProcessBuilder(commandArr);
    return pb.start();
  }
  
  public Process runLaunchCommand(String command, boolean isBlock) throws IOException {
    String sdbCommand = String.format("%s -s %s launch %s", new Object[] { this.sdbPath, this.device.getSerialNumber(), command });
    return runHostCommandImpl(command, sdbCommand, isBlock, null, true);
  }
  
  public Process runHostCommand(String command, boolean isBlock) throws IOException {
    return runHostCommand(command, isBlock, null);
  }
  
  public Process runHostCommand(String command, boolean isBlock, IShellOutputReceiver receiver) throws IOException {
    String sdbCommand = String.format("%s -s %s %s", new Object[] { this.sdbPath, this.device.getSerialNumber(), command });
    return runHostCommandImpl(command, sdbCommand, isBlock, receiver, false);
  }
  
  public String returnExecuteCommand(String command) {
    String sdbCommand = String.format("%s -s %s shell %s", new Object[] { this.sdbPath, this.device.getSerialNumber(), command });
    return HostUtil.returnExecute(sdbCommand);
  }
  
  public CommandErrorType runCommand(String command, CommandErrorType messages) throws CommandErrorException, TimeoutException, SdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
    return runCommand(command, messages, this.defaultTimeout);
  }
  
  public CommandErrorType runCommand(String command, CommandErrorType errorMessages, int timeout) throws CommandErrorException, TimeoutException, SdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
    print("$ " + command);
    this.device.executeShellCommand(makeCommandWithExitcode(command), (IShellOutputReceiver)this.receiver, timeout);
    this.endLine = this.receiver.getEndLine();
    this.commandOutput = this.receiver.getCommandOutput();
    String endLine = this.receiver.getEndLine();
    int exitcode = parseExitcode(endLine);
    errorMessages.setCommandOutput(this.receiver.getCommandOutput());
    errorMessages.findErrorType(exitcode, command);
    errorMessages.makeException();
    return errorMessages;
  }
  
  public String getEndLine() {
    return this.endLine;
  }
  
  public String getCommandOutput() {
    return this.commandOutput;
  }
  
  public int getDefaultTimeout() {
    return this.defaultTimeout;
  }
  
  public String[] getResultLineStrings() {
    String strs = this.commandOutput.toString();
    if (StringUtil.isEmpty(strs))
      return new String[0]; 
    String[] str = StringUtil.split(strs, this.SEPARATOR);
    return str;
  }
  
  private int parseExitcode(String line) {
    int exitcode = -1;
    if (line.startsWith("cmd_ret:"))
      exitcode = Integer.parseInt(StringUtil.getOnlyNumerics(line)); 
    return exitcode;
  }
  
  private String makeCommandWithExitcode(String command) {
    return String.valueOf(command) + "; echo cmd_ret:$?;";
  }
  
  public boolean isShowCommand() {
    return this.showCommand;
  }
  
  public void setShowCommand(boolean showCommand) {
    this.showCommand = showCommand;
  }
  
  private void print(String message) {
    this.logger.debug(message);
    if (isShowCommand() && this.console != null)
      this.console.println(message); 
  }
}
