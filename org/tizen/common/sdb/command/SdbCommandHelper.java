package org.tizen.common.sdb.command;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.sdb.command.message.CommandErrorException;
import org.tizen.common.sdb.command.message.CommandErrorType;
import org.tizen.common.sdb.command.receiver.CommandOutputReceiver;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.common.util.StringUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.IShellOutputReceiver;
import org.tizen.sdblib.daemon.ServerState;
import org.tizen.sdblib.exception.SdbCommandRejectedException;
import org.tizen.sdblib.exception.ServerException;
import org.tizen.sdblib.exception.ShellCommandUnresponsiveException;
import org.tizen.sdblib.exception.TimeoutException;
import org.tizen.sdblib.util.StreamGobbler;

public class SdbCommandHelper {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  private final String SEPARATOR = System.getProperty("line.separator");
  
  private boolean showCommand = false;
  
  private ITizenConsoleManager console = null;
  
  private CommandOutputReceiver receiver = null;
  
  private int defaultTimeout = 300000;
  
  private IDevice device = null;
  
  public SdbCommandHelper(IDevice device, ITizenConsoleManager console, CommandOutputReceiver receiver) {
    this.device = device;
    this.console = console;
    if (receiver == null) {
      this.receiver = new CommandOutputReceiver(console);
    } else {
      this.receiver = receiver;
    } 
  }
  
  public Process runHostCommand(List<String> command) throws IOException {
    return runHostCommand(command, true);
  }
  
  public Process runHostCommand(List<String> command, boolean isBlock) throws IOException {
    print("$ " + command);
    ProcessBuilder pb = new ProcessBuilder(command);
    Process process = pb.start();
    if (!isBlock)
      return process; 
    try {
      StreamGobbler inputGobbler = new StreamGobbler(process.getInputStream());
      inputGobbler.boot();
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
      errorGobbler.boot();
      inputGobbler.waitState(new ServerState[] { ServerState.Terminated });
      errorGobbler.waitState(new ServerState[] { ServerState.Terminated });
      this.receiver.append(inputGobbler.getResult());
      this.receiver.append(errorGobbler.getResult());
      this.receiver.close();
    } catch (ServerException e) {
      throw new IOException(e);
    } 
    return process;
  }
  
  public CommandErrorType runPkgCmd(String command, CommandErrorType errorMessages, int timeout) throws CommandErrorException, TimeoutException, SdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
    command = String.valueOf(command) + "; echo cmd_ret:$?;";
    print("$ " + command);
    this.device.executeShellCommand(command, (IShellOutputReceiver)this.receiver, timeout);
    int exitcode = parseExitcode(this.receiver.getEndLine());
    errorMessages.setCommandOutput(this.receiver.getCommandOutput());
    errorMessages.findErrorType(exitcode, command);
    errorMessages.makeException();
    return errorMessages;
  }
  
  private int parseExitcode(String line) {
    int exitcode = -1;
    if (line.startsWith("cmd_ret:"))
      exitcode = Integer.parseInt(StringUtil.getOnlyNumerics(line)); 
    return exitcode;
  }
  
  public void runCommand(String command) throws Exception {
    runCommand(command, false);
  }
  
  public void runCommand(String command, boolean isBlock) throws Exception {
    print("$ " + command);
    if (isBlock) {
      this.device.executeShellCommand(command, false);
      return;
    } 
    this.device.executeShellCommand(command, (IShellOutputReceiver)this.receiver, this.defaultTimeout);
  }
  
  private void print(String message) {
    this.logger.debug(message);
    if (isShowCommand() && this.console != null)
      this.console.println(message); 
  }
  
  public boolean isShowCommand() {
    return this.showCommand;
  }
  
  public void setShowCommand(boolean showCommand) {
    this.showCommand = showCommand;
  }
  
  public CommandOutputReceiver getReceiver() {
    return this.receiver;
  }
  
  public String getCommandOutput() {
    return this.receiver.getCommandOutput();
  }
  
  public String getEndLine() {
    return this.receiver.getEndLine();
  }
  
  public String[] getResultLineStrings() {
    String strs = getCommandOutput().toString();
    if (StringUtil.isEmpty(strs))
      return new String[0]; 
    String[] str = StringUtil.split(strs, this.SEPARATOR);
    return str;
  }
}
