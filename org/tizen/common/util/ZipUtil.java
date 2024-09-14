package org.tizen.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.tizen.common.file.FileHandler;
import org.tizen.common.file.IResource;
import org.tizen.common.file.LinkedResource;

public class ZipUtil {
  public static String getContent(String path, String name) throws IOException {
    if (StringUtil.isEmpty(path) || StringUtil.isEmpty(name))
      return null; 
    String fullpath = FilenameUtil.getCanonicalPath(path);
    if (StringUtil.isEmpty(fullpath))
      return null; 
    ZipFile zipfile = new ZipFile(fullpath);
    try {
      ZipEntry entry = zipfile.getEntry(name);
      if (entry == null)
        return null; 
      InputStream in = zipfile.getInputStream(entry);
      return IOUtil.getString(in, true);
    } finally {
      if (zipfile != null)
        zipfile.close(); 
    } 
  }
  
  public static void write(ZipArchiveOutputStream zos, IResource resource, String path, int permissions) throws IOException {
    Assert.notNull(zos);
    InputStream is = null;
    try {
      String destPath = path.replace("\\", "/");
      FileHandler fh = resource.getFileHandler();
      String resPath = resource.getPath();
      if (resource instanceof LinkedResource)
        resPath = ((LinkedResource)resource).getRealRelativePath(); 
      if (FileHandler.Type.DIRECTORY.equals(fh.get(resPath, FileHandler.Attribute.TYPE)) && 
        !destPath.endsWith("/"))
        destPath = String.valueOf(destPath) + "/"; 
      ZipArchiveEntry zae = new ZipArchiveEntry(destPath);
      if (permissions > 0)
        zae.setUnixMode(permissions); 
      zos.putArchiveEntry((ArchiveEntry)zae);
      if (FileHandler.Type.FILE.equals(fh.get(resPath, FileHandler.Attribute.TYPE))) {
        is = resource.getContents();
        if (is != null)
          IOUtil.redirect(is, (OutputStream)zos); 
      } 
    } finally {
      IOUtil.tryClose(new Object[] { is });
      zos.closeArchiveEntry();
    } 
  }
  
  public static void write(ZipOutputStream zos, IResource resource, String path) throws IOException {
    Assert.notNull(zos);
    InputStream is = null;
    try {
      String destPath = path.replace("\\", "/");
      FileHandler fh = resource.getFileHandler();
      if (FileHandler.Type.DIRECTORY.equals(fh.get(resource.getPath(), FileHandler.Attribute.TYPE)) && 
        !destPath.endsWith("/"))
        destPath = String.valueOf(destPath) + "/"; 
      ZipEntry zipEntry = new ZipEntry(destPath);
      zos.putNextEntry(zipEntry);
      if (FileHandler.Type.FILE.equals(fh.get(resource.getPath(), FileHandler.Attribute.TYPE))) {
        is = resource.getContents();
        if (is != null)
          IOUtil.redirect(is, zos); 
      } 
    } finally {
      IOUtil.tryClose(new Object[] { is });
      zos.closeEntry();
    } 
  }
}
