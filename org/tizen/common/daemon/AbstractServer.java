package org.tizen.common.daemon;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServer implements Server, Runnable {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected Thread thread = null;
  
  protected Lock lock = new ReentrantLock();
  
  protected ServerState state = ServerState.Terminated;
  
  protected final String name;
  
  public AbstractServer() {
    this(null);
  }
  
  public AbstractServer(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public ServerState getStatus() {
    this.lock.lock();
    try {
      return this.state;
    } finally {
      this.lock.unlock();
    } 
  }
  
  protected void setStatus(ServerState state) {
    this.lock.lock();
    try {
      this.state = state;
    } finally {
      this.lock.unlock();
      synchronized (this) {
        notifyAll();
      } 
    } 
  }
  
  public boolean isState(ServerState... states) {
    this.lock.lock();
    try {
      byte b;
      int i;
      ServerState[] arrayOfServerState;
      for (i = (arrayOfServerState = states).length, b = 0; b < i; ) {
        ServerState state = arrayOfServerState[b];
        if (this.state.equals(state))
          return true; 
        b++;
      } 
      return false;
    } finally {
      this.lock.unlock();
    } 
  }
  
  public void waitState(ServerState... states) {
    while (!isState(states)) {
      try {
        synchronized (this) {
          wait();
        } 
      } catch (InterruptedException e) {
        this.logger.error(e.getMessage(), e);
      } 
    } 
  }
  
  public void run() {
    if (isState(new ServerState[] { ServerState.Halting })) {
      setStatus(ServerState.Terminated);
      return;
    } 
    try {
      initialize();
      this.lock.lock();
      try {
        if (isState(new ServerState[] { ServerState.Halting }))
          return; 
        setStatus(ServerState.Running);
      } finally {
        this.lock.unlock();
      } 
      while (!isState(new ServerState[] { ServerState.Halting }))
        process(); 
    } catch (Exception e) {
      this.logger.error("Error occurred:", e);
    } finally {
      terminate();
      setStatus(ServerState.Terminated);
    } 
  }
  
  public void boot() throws ServerException {
    this.lock.lock();
    try {
      if (!isState(new ServerState[] { ServerState.Terminated }))
        throw new IllegalStateException(); 
      String name = getName();
      if (name == null) {
        this.thread = new Thread(this);
      } else {
        this.thread = new Thread(this, name);
      } 
      this.thread.setDaemon(true);
      this.thread.start();
      setStatus(ServerState.Initializing);
    } finally {
      this.lock.unlock();
    } 
  }
  
  public void down() throws ServerException {
    this.lock.lock();
    try {
      if (isState(new ServerState[] { ServerState.Terminated, ServerState.Halting }))
        throw new IllegalStateException(); 
      setStatus(ServerState.Halting);
    } finally {
      this.lock.unlock();
    } 
  }
  
  protected abstract void initialize() throws ServerException;
  
  protected abstract void process() throws Exception;
  
  protected abstract void terminate();
}
