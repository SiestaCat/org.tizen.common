package org.tizen.common.core.command.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.core.command.Policy;
import org.tizen.common.core.command.file.FileHandlingCommand;
import org.tizen.common.core.command.policy.FilePolicy;
import org.tizen.common.core.command.policy.MessagePolicy;
import org.tizen.common.file.FileHandler;
import org.tizen.common.file.IResource;
import org.tizen.common.util.Assert;
import org.tizen.common.util.FilenameUtil;
import org.tizen.common.util.IOUtil;

public class ZipCommand extends FileHandlingCommand<Object> {
  protected final String baseDir;
  
  private IResource[] resources = null;
  
  public ZipCommand(String baseDir, String target) {
    this(baseDir, (IResource[])null, target);
  }
  
  public ZipCommand(String baseDir, IResource[] resources, String target) {
    this.baseDir = baseDir;
    this.resources = resources;
    setPath(target);
  }
  
  public void run(Executor executor, ExecutionContext context) throws IOException {
    FileHandler handler = context.getFileHandler();
    Assert.notNull(handler);
    if (!handler.is(this.baseDir, FileHandler.Attribute.EXISTS)) {
      Policy policy = context.getPolicy("nonexist.dir.in");
      if (policy == null)
        throw new IllegalStateException("Could not find a policy : nonexist.dir.in"); 
      MessagePolicy messagePolicy = policy.<MessagePolicy>adapt(MessagePolicy.class);
      if (messagePolicy != null)
        messagePolicy.print(context.getPrompter(), "{0} doesn't exist.", new Object[] { this.baseDir }); 
      return;
    } 
    if (handler.is(this.path, FileHandler.Attribute.EXISTS)) {
      Policy policy = context.getPolicy("exist.file.out.wgt");
      if (policy == null)
        throw new IllegalStateException("Could not find a policy : exist.file.out.wgt"); 
      MessagePolicy messagePolicy = policy.<MessagePolicy>adapt(MessagePolicy.class);
      if (messagePolicy != null)
        messagePolicy.print(context.getPrompter(), "Package already exist.", new Object[0]); 
      FilePolicy filePolicy = policy.<FilePolicy>adapt(FilePolicy.class);
      if (FilePolicy.OVERWRITE.equals(filePolicy)) {
        this.logger.debug("No operation");
      } else {
        return;
      } 
    } 
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ZipOutputStream zipOut = createZipOutputStream(byteOut);
    try {
      if (this.resources != null) {
        byte b;
        int i;
        IResource[] arrayOfIResource;
        for (i = (arrayOfIResource = this.resources).length, b = 0; b < i; ) {
          IResource resource = arrayOfIResource[b];
          addEntry(zipOut, resource.getFileHandler(), resource.getPath());
          b++;
        } 
      } else {
        addEntry(zipOut, handler, this.baseDir);
      } 
    } finally {
      IOUtil.tryClose(new Object[] { zipOut });
    } 
    InputStream is = new ByteArrayInputStream(byteOut.toByteArray());
    try {
      handler.write(this.path, is);
      context.getPrompter().notify(MessageFormat.format("Package( {0} ) is created successfully.", new Object[] { this.path }));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
  }
  
  protected void addEntry(ZipOutputStream zipOut, FileHandler handler, String filePath) {
    try {
      Object type = handler.get(filePath, FileHandler.Attribute.TYPE);
      this.logger.trace("File path :{}", filePath);
      if (!FilenameUtil.equals(filePath, this.baseDir) && 
        !this.filter.accept(this.baseDir, FilenameUtil.getRelativePath(this.baseDir, filePath))) {
        this.logger.debug("Ignore {}", filePath);
        return;
      } 
      this.logger.trace("file [{}]'s type :{}", filePath, type);
      if (FileHandler.Type.DIRECTORY.equals(type)) {
        Collection<String> children = handler.list(filePath);
        TreeSet<String> safe = new TreeSet<>(new Comparator<String>() {
              public int compare(String o1, String o2) {
                if (o1.equals(o2))
                  return 0; 
                if (o1.equals("META-INF"))
                  return -1; 
                if (o2.equals("META-INF"))
                  return 1; 
                return o1.compareTo(o2);
              }
            });
        safe.addAll(children);
        children = safe;
        this.logger.debug("Files :{}", children);
        if (!FilenameUtil.equals(this.baseDir, filePath)) {
          String relative = FilenameUtil.getRelativePath(this.baseDir, filePath);
          this.logger.trace("Relative path :{}", relative);
          ZipEntry entry = createEntry(String.valueOf(relative) + "/");
          zipOut.putNextEntry(entry);
          zipOut.closeEntry();
        } 
        for (String child : children)
          addEntry(zipOut, handler, child); 
      } else if (FileHandler.Type.FILE.equals(type)) {
        String relative = FilenameUtil.getRelativePath(this.baseDir, filePath);
        this.logger.trace("Relative path :{}", relative);
        InputStream fileIn = handler.read(filePath);
        try {
          ZipEntry entry = createEntry(relative);
          zipOut.putNextEntry(entry);
          IOUtil.redirect(fileIn, zipOut);
        } finally {
          IOUtil.tryClose(new Object[] { fileIn });
          zipOut.closeEntry();
        } 
        this.logger.debug("Zip {}", relative);
      } 
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  protected ZipOutputStream createZipOutputStream(OutputStream out) throws IOException {
    ZipOutputStream zipOut = new ZipOutputStream(out);
    zipOut.setMethod(8);
    zipOut.setLevel(9);
    return zipOut;
  }
  
  protected ZipEntry createEntry(String name) {
    return new ZipEntry(name);
  }
}
