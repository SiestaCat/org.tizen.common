package org.tizen.common.ui.view.console;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.tizen.common.util.IOUtil;

public class ConsoleProcessClosure {
  protected static class ReaderThread extends Thread implements Closeable {
    private InputStream fInputStream;
    
    private IConsolePrinter fConsole;
    
    private boolean fFinished = false;
    
    private String lineSeparator;
    
    public ReaderThread(ThreadGroup group, String name, InputStream in, IConsolePrinter out) {
      super(group, name);
      this.fConsole = out;
      this.fInputStream = in;
      setDaemon(true);
      this.lineSeparator = System.getProperty("line.separator");
    }
    
    public void run() {
      try {
        try {
          BufferedReader reader = new BufferedReader(new InputStreamReader(this.fInputStream));
          String line;
          while ((line = reader.readLine()) != null) {
            List<TextStyle> ts = AnsicodeAdapter.getStringStyles(line);
            if (ts != null) {
              for (int i = 0; i < ts.size(); i++)
                this.fConsole.print(((TextStyle)ts.get(i)).getStripString(), 0, ((TextStyle)ts.get(i)).getForeground()); 
            } else {
              this.fConsole.print(line, 0, AnsicodeAdapter.BLACK);
            } 
            this.fConsole.print(this.lineSeparator, 0, AnsicodeAdapter.BLACK);
          } 
        } catch (IOException x) {
          x.printStackTrace();
        } finally {
          IOUtil.tryClose(new Object[] { this.fInputStream });
        } 
      } finally {
        complete();
      } 
    }
    
    public synchronized boolean finished() {
      return this.fFinished;
    }
    
    public synchronized void waitFor() {
      while (!this.fFinished) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
    }
    
    public synchronized void complete() {
      this.fFinished = true;
      notify();
    }
    
    public void close() {}
  }
  
  protected static int fCounter = 0;
  
  protected Process fProcess;
  
  protected IConsolePrinter fConsole;
  
  protected ReaderThread fOutputReader;
  
  protected ReaderThread fErrorReader;
  
  public ConsoleProcessClosure(Process process, IConsolePrinter console) {
    this.fProcess = process;
    this.fConsole = console;
  }
  
  public void runNonBlocking() {
    ThreadGroup group = new ThreadGroup("SRuncher" + fCounter++);
    InputStream stdin = this.fProcess.getInputStream();
    InputStream stderr = this.fProcess.getErrorStream();
    this.fOutputReader = new ReaderThread(group, "OutputReader", stdin, this.fConsole);
    this.fErrorReader = new ReaderThread(group, "ErrorReader", stderr, this.fConsole);
    this.fOutputReader.start();
    this.fErrorReader.start();
  }
  
  public void runBlocking() {
    runNonBlocking();
    boolean finished = false;
    while (!finished) {
      try {
        this.fProcess.waitFor();
      } catch (InterruptedException interruptedException) {}
      try {
        this.fProcess.exitValue();
        finished = true;
      } catch (IllegalThreadStateException e) {
        e.printStackTrace();
      } 
    } 
    if (!this.fOutputReader.finished())
      this.fOutputReader.waitFor(); 
    if (!this.fErrorReader.finished())
      this.fErrorReader.waitFor(); 
    IOUtil.tryClose(new Object[] { this.fOutputReader, this.fErrorReader });
    this.fProcess = null;
    this.fOutputReader = null;
    this.fErrorReader = null;
  }
  
  public boolean isAlive() {
    if (this.fProcess != null) {
      if (this.fOutputReader.isAlive() || this.fErrorReader.isAlive())
        return true; 
      this.fProcess = null;
      IOUtil.tryClose(new Object[] { this.fOutputReader, this.fErrorReader });
    } 
    return false;
  }
  
  public boolean isRunning() {
    if (this.fProcess != null) {
      if (this.fOutputReader.isAlive() || this.fErrorReader.isAlive())
        return true; 
      this.fProcess = null;
    } 
    return false;
  }
  
  public void terminate() {
    if (this.fProcess != null) {
      this.fProcess.destroy();
      this.fProcess = null;
    } 
    if (!this.fOutputReader.finished())
      this.fOutputReader.waitFor(); 
    if (!this.fErrorReader.finished())
      this.fErrorReader.waitFor(); 
    IOUtil.tryClose(new Object[] { this.fOutputReader, this.fErrorReader });
  }
}
