package org.tizen.common.util;

import org.eclipse.core.runtime.IProgressMonitor;

public class ProcessMonitorThread extends Thread {
  private IProgressMonitor monitor = null;
  
  private Process proc = null;
  
  private boolean hasMoreWork;
  
  public ProcessMonitorThread() {}
  
  public ProcessMonitorThread(Process proc, IProgressMonitor monitor) {
    this();
    this.monitor = monitor;
    this.proc = proc;
  }
  
  public void run() {
    this.hasMoreWork = true;
    while (this.hasMoreWork) {
      if (this.monitor.isCanceled()) {
        if (this.proc != null)
          this.proc.destroy(); 
        this.monitor.setCanceled(false);
        this.monitor.done();
        this.hasMoreWork = false;
        break;
      } 
      try {
        Thread.sleep(100L);
        int exit = this.proc.exitValue();
        if (exit <= 0 || exit >= 255) {
          this.monitor.done();
          this.hasMoreWork = false;
        } 
      } catch (IllegalThreadStateException illegalThreadStateException) {
        this.hasMoreWork = true;
      } catch (InterruptedException interruptedException) {
        this.hasMoreWork = false;
      } 
    } 
  }
}
