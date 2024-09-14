package org.tizen.common.core.command.sdb;

import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.SmartDevelopmentBridge;

public class SmartDevelopmentBridgeManager {
  private SmartDevelopmentBridge impl = SmartDevelopmentBridge.getBridge();
  
  public boolean equals(Object obj) {
    return this.impl.equals(obj);
  }
  
  public IDevice[] getDevices() {
    return this.impl.getDevices();
  }
  
  public int getRestartAttemptCount() {
    return this.impl.getRestartAttemptCount();
  }
  
  public String getSdbOsLocation() {
    return this.impl.getSdbOsLocation();
  }
  
  public int hashCode() {
    return this.impl.hashCode();
  }
  
  public boolean isConnected() {
    return this.impl.isConnected();
  }
  
  public boolean restart() {
    return this.impl.restart();
  }
  
  public String toString() {
    return this.impl.toString();
  }
  
  public void disconnectBridge() {
    SmartDevelopmentBridge.disconnectBridge();
  }
  
  public void terminate() {
    SmartDevelopmentBridge.terminate();
  }
}
