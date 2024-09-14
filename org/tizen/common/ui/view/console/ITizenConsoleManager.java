package org.tizen.common.ui.view.console;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.IConsole;

public interface ITizenConsoleManager extends IConsolePrinter {
  IConsole getConsole();
  
  void print(String paramString);
  
  void println(String paramString);
  
  void println(String paramString, int paramInt, Color paramColor);
  
  void printProcessStreams(Process paramProcess);
  
  void show();
}
