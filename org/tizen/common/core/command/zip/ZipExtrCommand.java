package org.tizen.common.core.command.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.core.command.file.FileHandlingCommand;
import org.tizen.common.file.FileHandler;
import org.tizen.common.util.Assert;
import org.tizen.common.util.IOUtil;

public class ZipExtrCommand extends FileHandlingCommand<Object> {
  protected final String targetDir;
  
  public ZipExtrCommand(String source, String targetDir) {
    this.targetDir = targetDir;
    setPath(source);
  }
  
  public void run(Executor executor, ExecutionContext context) throws IOException {
    FileHandler handler = context.getFileHandler();
    Assert.notNull(handler);
    if (!handler.is(this.targetDir, FileHandler.Attribute.EXISTS))
      throw new IllegalStateException("Could not find a directory : " + this.targetDir); 
    if (!handler.is(this.path, FileHandler.Attribute.EXISTS))
      throw new IllegalStateException("Could not find a file : " + this.path); 
    FileInputStream fileInputStream = new FileInputStream(this.path);
    ZipInputStream zis = new ZipInputStream(fileInputStream);
    try {
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        String entryName = ze.getName();
        if (this.filter.accept("", entryName)) {
          File f = new File(String.valueOf(this.targetDir) + File.separator + entryName);
          if (ze.isDirectory()) {
            f.mkdirs();
          } else {
            File parent = f.getParentFile();
            if (parent != null) {
              parent.mkdirs();
              handler.write(f.toString(), zis);
            } 
          } 
        } else {
          this.logger.debug("Ignore {}", entryName);
        } 
        zis.closeEntry();
        ze = zis.getNextEntry();
      } 
    } finally {
      IOUtil.tryClose(new Object[] { zis, fileInputStream });
    } 
  }
}
