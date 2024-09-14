package org.tizen.common.core.command.sdb;

import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.Assert;
import org.tizen.sdblib.IDevice;

public class DevicesSdbCommand extends SdbHandlingCommand {
  protected SmartDevelopmentBridgeManager createBridge() {
    return new SmartDevelopmentBridgeManager();
  }
  
  public void run(Executor executor, ExecutionContext context) {
    SmartDevelopmentBridgeManager bridge = createBridge();
    Assert.notNull(bridge);
    IDevice[] devices = bridge.getDevices();
    StringBuilder result = new StringBuilder();
    byte b;
    int i;
    IDevice[] arrayOfIDevice1;
    for (i = (arrayOfIDevice1 = devices).length, b = 0; b < i; ) {
      IDevice device = arrayOfIDevice1[b];
      result.append(device.toString()).append(System.getProperty("line.separator"));
      b++;
    } 
    setResult(result.toString());
    context.getPrompter().notify(getResult());
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {}
}
