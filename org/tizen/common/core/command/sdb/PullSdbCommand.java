package org.tizen.common.core.command.sdb;

import java.io.Closeable;
import java.io.IOException;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.Assert;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.service.FileEntry;
import org.tizen.sdblib.service.SyncResult;
import org.tizen.sdblib.service.SyncService;
import org.tizen.sdblib.util.IOUtil;

public class PullSdbCommand extends SyncCommand {
  public PullSdbCommand(String remotePath, String localPath) {
    super(remotePath, localPath);
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    IDevice device = getDevice();
    Assert.notNull(device);
    SyncService service = null;
    try {
      service = getSyncService();
      SyncResult syncSuccess = 
        service.pull(new FileEntry[] { device.getFileEntry(this.remotePath) }, this.localPath, getProgressMonitor());
      if (!syncSuccess.isOk())
        context.getPrompter().notify("Error: Failed to Transfer."); 
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      IOUtil.tryClose((Closeable)service);
    } 
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {}
}
