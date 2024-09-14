package org.tizen.common.util;

import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredTaskManager {
  protected static final long DEFAULT_IDLE_TIME = 3000L;
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected long idleTime;
  
  protected long scheduledTime = 3000L;
  
  protected Timer timer = null;
  
  protected final Runnable task;
  
  public DeferredTaskManager(Runnable task) {
    this(task, 3000L);
  }
  
  public DeferredTaskManager(Runnable task, long idleTime) {
    Assert.notNull(task);
    this.task = task;
    this.idleTime = idleTime;
    this.logger.debug("Idle time: {}", Long.valueOf(idleTime));
  }
  
  public synchronized long getIdleTime() {
    return this.idleTime;
  }
  
  public synchronized void setIdleTime(long idleTime) {
    this.idleTime = idleTime;
    if (this.timer != null)
      schedule(this.idleTime + this.scheduledTime - System.currentTimeMillis()); 
  }
  
  public synchronized void schedule(long delay) {
    cancel();
    if (0L < delay) {
      this.timer = new Timer();
      this.scheduledTime = System.currentTimeMillis();
      this.timer.schedule(new TimerTask() {
            public void run() {
              DeferredTaskManager.this.task.run();
              DeferredTaskManager.this.logger.debug("{} was run", DeferredTaskManager.this.task);
            }
          }delay);
      this.logger.debug("{} will be run after {}", this.task, Long.valueOf(delay));
    } else {
      this.task.run();
      this.logger.debug("{} was run", this.task);
    } 
  }
  
  public synchronized void tick() {
    schedule(this.idleTime);
  }
  
  public synchronized void cancel() {
    if (this.timer != null) {
      this.timer.cancel();
      this.timer = null;
      this.logger.debug("{} canceled", this.task);
    } 
  }
}
