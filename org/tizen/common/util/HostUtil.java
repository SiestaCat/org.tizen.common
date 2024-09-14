package org.tizen.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.ui.view.console.ConsoleManager;

public abstract class HostUtil {
  protected static Logger logger = LoggerFactory.getLogger(HostUtil.class);
  
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");
  
  private static final String SHELL_COMMAND_LINUX = "/bin/sh";
  
  private static final String SHELL_COMMAND_WINDOW = "cmd";
  
  public static String getContents(String filePath) {
    FileReader input = null;
    StringBuilder contents = new StringBuilder();
    try {
      input = new FileReader(filePath);
      IOUtil.redirect(input, contents);
      return StringUtil.trim(contents.toString());
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return null;
    } finally {
      IOUtil.tryClose(new Object[] { input, contents });
    } 
  }
  
  public static boolean exists(String path) {
    if (StringUtil.isEmpty(path))
      return false; 
    File file = new File(path);
    return file.exists();
  }
  
  public static boolean execute(String command) {
    if (command == null)
      return false; 
    Process proc = null;
    Runtime runtime = Runtime.getRuntime();
    String[] fullCommand = getCommand(command);
    int i = 0;
    try {
      proc = runtime.exec(fullCommand);
      i = proc.waitFor();
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return false;
    } catch (InterruptedException e) {
      logger.info("Exception occurred:", e);
      return false;
    } finally {
      logger.debug("HostUtil execute - exit value : {}", Integer.valueOf(i));
      if (proc != null)
        proc.destroy(); 
    } 
    return (i == 0);
  }
  
  public static boolean execute(String command, String workingDir) {
    if (command == null)
      return false; 
    BufferedReader input = null;
    Process proc = null;
    String[] fullCommand = getCommand(command);
    int result = 0;
    try {
      ProcessBuilder pb = new ProcessBuilder(new String[0]);
      pb.command(fullCommand);
      if (workingDir != null)
        pb.directory(new File(workingDir)); 
      proc = pb.start();
      result = proc.waitFor();
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return false;
    } catch (InterruptedException e) {
      logger.info("Exception occurred:", e);
      return false;
    } finally {
      IOUtil.tryClose(new Object[] { input });
      if (proc != null)
        proc.destroy(); 
    } 
    return (result == 0);
  }
  
  public static boolean batchExecute(String command) {
    if (command == null)
      return false; 
    String[] fullCommand = getCommand(command);
    Runtime run = Runtime.getRuntime();
    Process p = null;
    int i = 0;
    try {
      p = run.exec(fullCommand);
      i = p.waitFor();
      StreamGobbler gb1 = new StreamGobbler(p.getInputStream());
      StreamGobbler gb2 = new StreamGobbler(p.getErrorStream());
      gb1.start();
      gb2.start();
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return false;
    } catch (InterruptedException e) {
      logger.info("Exception occurred:", e);
      return false;
    } 
    return (i == 0);
  }
  
  public static boolean batchExecute(String command, String[] envp, File dir) {
    String[] fullCommand = getCommand(command);
    Runtime run = Runtime.getRuntime();
    Process p = null;
    StreamGobbler gb1 = null;
    StreamGobbler gb2 = null;
    try {
      p = run.exec(fullCommand, envp, dir);
      gb1 = new StreamGobbler(p.getInputStream());
      gb2 = new StreamGobbler(p.getErrorStream());
      gb1.start();
      gb2.start();
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return false;
    } 
    return true;
  }
  
  public static String returnExecute(String command, String workingDir) {
    return returnExecute(command, workingDir, false);
  }
  
  public static String returnExecute(String command, String workingDir, boolean withError) {
    if (command == null)
      return null; 
    BufferedReader input = null;
    StringBuilder contents = new StringBuilder();
    String line = null;
    Process proc = null;
    String[] fullCommand = getCommand(command);
    try {
      ProcessBuilder pb = new ProcessBuilder(new String[0]);
      pb.redirectErrorStream(withError);
      pb.command(fullCommand);
      if (workingDir != null)
        pb.directory(new File(workingDir)); 
      proc = pb.start();
      input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      while ((line = input.readLine()) != null) {
        contents.append(line);
        contents.append(LINE_SEPARATOR);
      } 
    } catch (IOException e) {
      logger.info("Exception occurred:", e);
      return null;
    } finally {
      IOUtil.tryClose(new Object[] { input });
      if (proc != null)
        proc.destroy(); 
    } 
    return contents.toString().trim();
  }
  
  public static void executeWithConsole(String command, String viewName) throws IOException, InterruptedException {
    BufferedReader input = null;
    String line = null;
    Process proc = null;
    String[] fullCommand = getCommand(command);
    ConsoleManager cm = new ConsoleManager(viewName, true);
    cm.clear();
    try {
      ProcessBuilder pb = new ProcessBuilder(new String[0]);
      pb.redirectErrorStream(true);
      pb.command(fullCommand);
      proc = pb.start();
      input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      while ((line = input.readLine()) != null)
        cm.println(line); 
      proc.waitFor();
      if (proc.exitValue() != 0)
        throw new IllegalStateException("Failed to execute command: " + command); 
    } finally {
      IOUtil.tryClose(new Object[] { input });
      if (proc != null)
        proc.destroy(); 
    } 
  }
  
  public static void executeWithLog(String command, Logger logger) {
    BufferedReader input = null;
    String line = null;
    Process proc = null;
    String[] fullCommand = getCommand(command);
    try {
      ProcessBuilder pb = new ProcessBuilder(new String[0]);
      pb.command(fullCommand);
      proc = pb.start();
      input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      while ((line = input.readLine()) != null)
        logger.info(line); 
    } catch (IOException e) {
      HostUtil.logger.info("Exception occurred:", e);
    } finally {
      IOUtil.tryClose(new Object[] { input });
      if (proc != null)
        proc.destroy(); 
    } 
  }
  
  public static String returnExecute(String command) {
    return returnExecute(command, null);
  }
  
  public static String[] getCommand(String command) {
    if (OSChecker.isWindows())
      return new String[] { "cmd", "/c", command }; 
    return new String[] { "/bin/sh", "-c", command };
  }
}
