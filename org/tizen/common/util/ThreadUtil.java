package org.tizen.common.util;

public class ThreadUtil {
  public static void trySleep(long milliseconds) {
    if (milliseconds <= 0L)
      return; 
    long start = System.nanoTime();
    try {
      long now = 0L;
      while ((now = System.nanoTime()) - start < 1000000L * milliseconds)
        Thread.sleep(milliseconds - (now - start) / 1000000L); 
    } catch (InterruptedException interruptedException) {}
  }
}
