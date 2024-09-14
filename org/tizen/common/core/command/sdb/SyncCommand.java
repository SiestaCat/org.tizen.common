package org.tizen.common.core.command.sdb;

import java.io.IOException;
import org.tizen.sdblib.exception.SdbCommandRejectedException;
import org.tizen.sdblib.exception.TimeoutException;
import org.tizen.sdblib.service.ISyncProgressMonitor;
import org.tizen.sdblib.service.NullSyncProgressMonitor;
import org.tizen.sdblib.service.SyncService;

public abstract class SyncCommand extends SdbDevicesHandlingCommand {
  protected final String remotePath;
  
  protected final String localPath;
  
  public SyncCommand(String remotePath, String localPath) {
    this.remotePath = remotePath;
    this.localPath = localPath;
  }
  
  protected SyncService getSyncService() throws TimeoutException, SdbCommandRejectedException, IOException {
    return this.device.getSyncService();
  }
  
  protected ISyncProgressMonitor getProgressMonitor() {
    return (ISyncProgressMonitor)NullSyncProgressMonitor.getInstance();
  }
}
