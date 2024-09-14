package org.tizen.common.core.command.sdb;

import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.Assert;
import org.tizen.sdblib.IDevice;

public class ForwardSdbCommand extends SdbDevicesHandlingCommand {
  private int localPort;
  
  private int remotePort;
  
  public ForwardSdbCommand(int localPort, int remotePort) {
    this.localPort = localPort;
    this.remotePort = remotePort;
  }
  
  public void run(Executor executor, ExecutionContext context) throws InterruptedException {
    IDevice device = getDevice();
    Assert.notNull(device);
    try {
      device.createForward(this.localPort, this.remotePort);
    } catch (Exception exception) {
      context.getPrompter().notify("Forward socket connections error.");
    } 
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {
    IDevice device = getDevice();
    Assert.notNull(device);
    try {
      device.removeForward(this.localPort, this.remotePort);
    } catch (Exception exception) {
      context.getPrompter().notify("Remove forward socket connections error.");
    } 
  }
}
