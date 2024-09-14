package org.tizen.common.core.command.sdb;

import org.tizen.sdblib.IDevice;

public abstract class SdbDevicesHandlingCommand extends SdbHandlingCommand {
  protected IDevice device;
  
  public void setDevice(IDevice device) {
    this.device = device;
  }
  
  public IDevice getDevice() {
    return this.device;
  }
}
