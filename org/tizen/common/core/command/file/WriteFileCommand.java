package org.tizen.common.core.command.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.ObjectUtil;

public class WriteFileCommand extends FileHandlingCommand<Boolean> {
  protected final byte[] contents;
  
  public WriteFileCommand(String path, byte[] contents) {
    setPath(path);
    this.contents = contents;
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    byte[] safeBytes = ObjectUtil.<byte[]>nvl(new byte[][] { this.contents, {} });
    this.logger.trace("Trying write {} in {} bytes", this.path, Integer.valueOf(safeBytes.length));
    InputStream is = null;
    try {
      is = new ByteArrayInputStream(safeBytes);
      context.getFileHandler().write(
          this.path, 
          is);
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
  }
}
