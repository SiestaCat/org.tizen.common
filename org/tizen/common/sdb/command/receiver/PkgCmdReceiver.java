package org.tizen.common.sdb.command.receiver;

import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.common.util.StringUtil;

public class PkgCmdReceiver extends CommandOutputReceiver {
  private String PKGCMD_CB_KEY_ABOUT_KEY = "key";
  
  private String PKGCMD_CB_KEY_ABOUT_VALUE = "val";
  
  private String oldOutput = "";
  
  private static String RETURN_CB_PREFIX = "__return_cb";
  
  private static String RETURN_CB_START = "start";
  
  private static String RETURN_CB_END = "end";
  
  private static String RETURN_CB_INSTALL_PERCENT = "install_percent";
  
  private static String RETURN_CB_UNINSTALL_PERCENT = "uninstall_percent";
  
  private static String RETURN_CB_ERROR = "error";
  
  private static String OUTPUT_ING = ".";
  
  public PkgCmdReceiver(ITizenConsoleManager console) {
    super(console);
  }
  
  public void processNewLines(String[] lines) {
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = lines).length, b = 0; b < i; ) {
      String line = arrayOfString[b];
      String output = getMeaningfulOutput(line, this.oldOutput);
      if (this.console != null && 
        output != null && 
        !output.isEmpty()) {
        this.console.print(output);
        this.oldOutput = output;
      } 
      this.commandOutput.append(line);
      this.commandOutput.append(System.getProperty("line.separator"));
      b++;
    } 
    this.endLine = lines[lines.length - 1];
  }
  
  private String getMeaningfulOutput(String output, String lastMeaningfulOutput) {
    String meaningfulOutput = null;
    String[] elements = StringUtil.split(output, " ");
    if (output.startsWith(RETURN_CB_PREFIX)) {
      String key = getValue(elements, this.PKGCMD_CB_KEY_ABOUT_KEY);
      String value = getValue(elements, this.PKGCMD_CB_KEY_ABOUT_VALUE);
      if (RETURN_CB_START.equals(key)) {
        meaningfulOutput = String.valueOf(key) + " process (" + 
          getValue(elements, this.PKGCMD_CB_KEY_ABOUT_VALUE) + 
          ")\n";
      } else if (RETURN_CB_END.equals(key)) {
        meaningfulOutput = "\n" + key + " process (" + 
          getValue(elements, this.PKGCMD_CB_KEY_ABOUT_VALUE) + 
          ")\n";
      } else if (RETURN_CB_INSTALL_PERCENT.equals(key)) {
        meaningfulOutput = OUTPUT_ING;
      } else if (RETURN_CB_UNINSTALL_PERCENT.equals(key)) {
        meaningfulOutput = OUTPUT_ING;
      } else if (RETURN_CB_ERROR.equals(key)) {
        meaningfulOutput = "\n" + key + " : " + value + "\n" + getErrorMessage(elements);
      } else {
        meaningfulOutput = "\n" + key + 
          " : " + 
          value + 
          "\n";
      } 
    } else {
      meaningfulOutput = String.valueOf(output) + "\n";
    } 
    return meaningfulOutput;
  }
  
  private String getErrorMessage(String[] elements) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < elements.length; i++) {
      if (elements[i].equals(RETURN_CB_ERROR))
        for (int j = i; j < elements.length; j++)
          buffer.append(elements[j]).append(" ");  
    } 
    return buffer.toString();
  }
  
  private String getValue(String[] elements, String key) {
    String result = null;
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = elements).length, b = 0; b < i; ) {
      String element = arrayOfString[b];
      if (element.startsWith(key)) {
        String[] list = StringUtil.split(element, "[");
        result = StringUtil.trimCharacter(list[1], ']');
        break;
      } 
      b++;
    } 
    return result;
  }
}
