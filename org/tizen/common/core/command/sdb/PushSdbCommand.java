package org.tizen.common.core.command.sdb;

import java.io.Closeable;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.Assert;
import org.tizen.common.util.FilenameUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.service.SyncResult;
import org.tizen.sdblib.service.SyncService;
import org.tizen.sdblib.util.IOUtil;

public class PushSdbCommand extends SyncCommand {
  public PushSdbCommand(String localPath, String remotePath) {
    super(remotePath, localPath);
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    IDevice device = getDevice();
    Assert.notNull(device);
    SyncService service = null;
    try {
      service = getSyncService();
      SyncResult syncSuccess = 
        service.push(new String[] { this.localPath }, device.getFileEntry(this.remotePath), getProgressMonitor());
      String name = FilenameUtil.getFilename(this.localPath);
      if (!syncSuccess.isOk()) {
        setResult("Failed");
        context.getPrompter().notify("'" + name + "' file transfer failed.");
        return;
      } 
      setResult("Success");
      context.getPrompter().notify("'" + name + "' file transfer successful.");
    } finally {
      IOUtil.tryClose((Closeable)service);
    } 
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {}
}
