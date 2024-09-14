package org.tizen.common.ui.view.console;

import java.io.IOException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.TextConsole;
import org.tizen.common.util.Assert;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.SWTUtil;

public class ConsoleManager2 implements ITizenConsoleManager, IHyperlinkManager {
  protected final MessageConsoleSpy console;
  
  protected final HyperlinkManager hyperlinkManager;
  
  private String consoleName;
  
  protected int documentLength = 0;
  
  public ConsoleManager2(String consoleName, boolean focus) {
    this.consoleName = consoleName;
    this.console = createMessageConsole();
    this.hyperlinkManager = new HyperlinkManager((TextConsole)this.console);
    this.console.getDocument().addDocumentListener(this.hyperlinkManager);
    if (focus)
      getConsoleManager().showConsoleView((IConsole)this.console); 
    setName(consoleName);
  }
  
  protected IConsoleManager getConsoleManager() {
    return ConsolePlugin.getDefault().getConsoleManager();
  }
  
  protected IConsole[] getConsoles() {
    return getConsoleManager().getConsoles();
  }
  
  public void setName(final String newName) {
    Assert.notNull(newName);
    synchronized (this) {
      this.consoleName = newName;
    } 
    SWTUtil.asyncExec(new Runnable() {
          public void run() {
            if (ConsoleManager2.this.console == null)
              return; 
            ConsoleManager2.this.console.setName(newName);
          }
        });
  }
  
  protected synchronized MessageConsoleSpy createMessageConsole() {
    IConsole[] consoles = getConsoles();
    for (int i = 0, n = consoles.length; i < n; i++) {
      if (this.consoleName.equals(consoles[i].getName()))
        getConsoleManager().removeConsoles(new IConsole[] { (IConsole)this.console }); 
    } 
    MessageConsoleSpy console = new MessageConsoleSpy(this.consoleName);
    getConsoleManager().addConsoles(new IConsole[] { (IConsole)console });
    return console;
  }
  
  public IConsole getConsole() {
    return (IConsole)this.console;
  }
  
  public synchronized void println(String line) {
    MessageConsoleStream output = createConsoleStream();
    try {
      output.println(line);
      this.documentLength += line.length() + 1;
    } finally {
      IOUtil.tryClose(new Object[] { output });
    } 
  }
  
  public void printProcessStreams(Process process) {
    clear();
    ConsoleProcessClosure closure = new ConsoleProcessClosure(process, this);
    closure.runBlocking();
  }
  
  protected MessageConsoleStream getConsoleStream(int fontStyle, Color color) {
    MessageConsoleStream stream = createConsoleStream();
    configureConsole(stream, fontStyle, color);
    return stream;
  }
  
  protected MessageConsoleStream createConsoleStream() {
    MessageConsoleStream stream = this.console.newMessageStream();
    return stream;
  }
  
  protected void configureConsole(final MessageConsoleStream stream, final int fontStyle, final Color color) {
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            stream.setFontStyle(fontStyle);
            stream.setColor(color);
          }
        });
  }
  
  public synchronized void print(String message, int fontStyle, Color color) {
    MessageConsoleStream stream = getConsoleStream(fontStyle, color);
    try {
      stream.print(message);
      this.documentLength += message.length();
    } finally {
      IOUtil.tryClose(new Object[] { stream });
    } 
  }
  
  public void println(String message, int fontStyle, Color color) {
    print(String.valueOf(message) + "\n", fontStyle, color);
  }
  
  public synchronized void clear() {
    SWTUtil.syncExec(
        new Runnable() {
          public void run() {
            IDocument document = ConsoleManager2.this.console.getDocument();
            if (document == null)
              return; 
            ConsoleManager2.this.console.clearConsole();
          }
        });
    this.documentLength = 0;
  }
  
  public void show() {
    show(true);
  }
  
  public void show(final boolean focus) {
    SWTUtil.syncExec(
        new Runnable() {
          public void run() {
            IWorkbenchPage page = SWTUtil.getActivePage();
            if (page == null)
              throw new IllegalStateException(); 
            IConsoleView view = (IConsoleView)page.findView("org.eclipse.ui.console.ConsoleView");
            if (view != null) {
              view.display(ConsoleManager2.this.getConsole());
              if (focus)
                view.setFocus(); 
            } 
          }
        });
  }
  
  public void addLinker(LinkInfo link) {
    this.hyperlinkManager.addLinker(link);
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
