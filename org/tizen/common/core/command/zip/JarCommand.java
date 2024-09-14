package org.tizen.common.core.command.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarCommand extends ZipCommand {
  public JarCommand(String baseDir, String target) {
    super(baseDir, target);
  }
  
  protected ZipOutputStream createZipOutputStream(OutputStream out) throws IOException {
    JarOutputStream zipOut = new JarOutputStream(out);
    zipOut.setMethod(8);
    zipOut.setLevel(9);
    return zipOut;
  }
  
  protected JarEntry createEntry(String name) {
    return new JarEntry(name);
  }
}
