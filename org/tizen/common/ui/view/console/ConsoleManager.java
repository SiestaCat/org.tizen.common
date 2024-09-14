package org.tizen.common.ui.view.console;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleManager implements ITizenConsoleManager {
  private IConsoleManager consoleManager;
  
  private String consoleName;
  
  private boolean consoleFocus;
  
  private MessageConsole console;
  
  private Display display = Display.getDefault();
  
  private MessageConsoleStream colorConsoleStream;
  
  public ConsoleManager(String consoleName, boolean focus) {
    this.consoleName = consoleName;
    this.consoleFocus = focus;
    this.consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    this.console = getMessageConsole();
  }
  
  public void changeConsoleName(String newName) throws NoSuchFieldException, IllegalAccessException {
    this.consoleName = newName;
    Runnable runnable = new Runnable() {
        public void run() {
          Class<?> cls = null;
          Method method = null;
          try {
            cls = Class.forName("org.eclipse.ui.console.AbstractConsole");
            method = cls.getDeclaredMethod("setName", new Class[] { String.class });
            method.setAccessible(true);
            method.invoke(ConsoleManager.this.getConsole(), new Object[] { ConsoleManager.access$1(this.this$0) });
          } catch (SecurityException e) {
            ConsolePlugin.log(e);
          } catch (IllegalArgumentException e) {
            ConsolePlugin.log(e);
          } catch (ClassNotFoundException e) {
            ConsolePlugin.log(e);
          } catch (NoSuchMethodException e) {
            ConsolePlugin.log(e);
          } catch (IllegalAccessException e) {
            ConsolePlugin.log(e);
          } catch (InvocationTargetException e) {
            ConsolePlugin.log(e);
          } 
        }
      };
    Display.getDefault().syncExec(runnable);
  }
  
  private MessageConsole getMessageConsole() {
    boolean found = false;
    IConsole[] consoles = this.consoleManager.getConsoles();
    for (int i = 0; i < consoles.length; i++) {
      if (this.consoleName.equals(consoles[i].getName())) {
        this.console = (MessageConsole)consoles[i];
        this.consoleManager.removeConsoles(new IConsole[] { (IConsole)this.console });
        break;
      } 
    } 
    if (!found) {
      this.console = new MessageConsole(this.consoleName, null);
      this.consoleManager.addConsoles(new IConsole[] { (IConsole)this.console });
    } 
    if (this.consoleFocus)
      showConsoleView(); 
    return this.console;
  }
  
  public IConsole getConsole() {
    return (IConsole)this.console;
  }
  
  public void showConsoleView() {
    this.consoleManager.showConsoleView((IConsole)this.console);
  }
  
  public void removeConsole() {
    MessageConsole console = null;
    IConsole[] consoles = this.consoleManager.getConsoles();
    for (int i = 0; i < consoles.length; i++) {
      if (this.consoleName.equals(consoles[i].getName())) {
        console = (MessageConsole)consoles[i];
        this.consoleManager.removeConsoles(new IConsole[] { (IConsole)console });
        break;
      } 
    } 
  }
  
  @Deprecated
  public MessageConsoleStream getMessageConsoleStream(boolean isError) {
    final int colorId;
    final MessageConsoleStream output = this.console.newMessageStream();
    output.setActivateOnWrite(false);
    if (!isError) {
      colorId = 2;
    } else {
      colorId = 3;
    } 
    Runnable runnable = new Runnable() {
        public void run() {
          Color color = Display.getCurrent().getSystemColor(colorId);
          output.setColor(color);
        }
      };
    Display.getDefault().syncExec(runnable);
    return output;
  }
  
  public MessageConsoleStream getMessageConsoleStream() {
    return this.console.newMessageStream();
  }
  
  public void println(String line) {
    MessageConsoleStream output = this.console.newMessageStream();
    output.println(line);
    try {
      output.close();
    } catch (IOException e) {
      ConsolePlugin.log(e);
    } 
  }
  
  public void printProcessStreams(Process process) {
    clear();
    ConsoleProcessClosure closure = new ConsoleProcessClosure(process, this);
    closure.runBlocking();
  }
  
  private void setStringStyle(int fontStyle, Color color) {
    this.colorConsoleStream.setFontStyle(fontStyle);
    this.colorConsoleStream.setColor(color);
  }
  
  public void print(String line, int fontStyle, Color color) {
    this.colorConsoleStream = this.console.newMessageStream();
    this.display.syncExec(new ConsoleSettingThread(fontStyle, color));
    this.colorConsoleStream.print(line);
    try {
      this.colorConsoleStream.close();
    } catch (IOException e) {
      ConsolePlugin.log(e);
    } 
  }
  
  public void println(String line, int fontStyle, Color color) {
    this.colorConsoleStream = this.console.newMessageStream();
    this.display.syncExec(new ConsoleSettingThread(fontStyle, color));
    this.colorConsoleStream.println(line);
    try {
      this.colorConsoleStream.close();
    } catch (IOException e) {
      ConsolePlugin.log(e);
    } 
  }
  
  public void clear() {
    Runnable runnable = new Runnable() {
        public void run() {
          IDocument document = ConsoleManager.this.console.getDocument();
          if (document != null)
            document.set(""); 
        }
      };
    Display.getDefault().syncExec(runnable);
  }
  
  public void show() {
    Runnable runnable = new Runnable() {
        public void run() {
          IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
          String id = "org.eclipse.ui.console.ConsoleView";
          IConsoleView view = null;
          try {
            view = (IConsoleView)page.showView(id);
            view.display(ConsoleManager.this.getConsole());
          } catch (PartInitException e) {
            ConsolePlugin.log((Throwable)e);
          } 
        }
      };
    Display.getDefault().syncExec(runnable);
  }
  
  class ConsoleSettingThread extends Thread {
    private int fontStyle;
    
    private Color color;
    
    ConsoleSettingThread(int fontStyle, Color color) {
      this.fontStyle = fontStyle;
      this.color = color;
    }
    
    public void run() {
      ConsoleManager.this.setStringStyle(this.fontStyle, this.color);
    }
  }
  
  public void print(String line) {
    MessageConsoleStream output = this.console.newMessageStream();
    output.print(line);
    try {
      output.close();
    } catch (IOException e) {
      ConsolePlugin.log(e);
    } 
  }
}
