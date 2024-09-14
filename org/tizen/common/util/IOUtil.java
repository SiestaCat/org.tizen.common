package org.tizen.common.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtil {
  private static final int BUFFER_SIZE = 8192;
  
  protected static final Logger logger = LoggerFactory.getLogger(IOUtil.class);
  
  public static void tryClose(Object... closeables) {
    if (closeables == null)
      return; 
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = closeables).length, b = 0; b < i; ) {
      Object obj = arrayOfObject[b];
      if (obj != null)
        try {
          if (obj instanceof Closeable) {
            ((Closeable)obj).close();
          } else if (obj instanceof Selector) {
            ((Selector)obj).close();
          } else if (obj instanceof Socket) {
            ((Socket)obj).close();
          } else if (obj instanceof ServerSocket) {
            ((ServerSocket)obj).close();
          } 
        } catch (IOException e) {
          logger.warn("Fail to close " + obj, e);
        }  
      b++;
    } 
  }
  
  public static void tryFlush(Flushable... flushables) {
    if (flushables == null)
      return; 
    byte b;
    int i;
    Flushable[] arrayOfFlushable;
    for (i = (arrayOfFlushable = flushables).length, b = 0; b < i; ) {
      Flushable flushable = arrayOfFlushable[b];
      if (flushable != null)
        try {
          flushable.flush();
        } catch (IOException e) {
          logger.warn("Fail to flush " + flushable, e);
        }  
      b++;
    } 
  }
  
  public static void redirect(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int nRead = 0;
    int totalBytes = 0;
    while ((nRead = in.read(buffer)) > 0) {
      out.write(buffer, 0, nRead);
      totalBytes += nRead;
    } 
    logger.trace("{} byte(s) wrote", Integer.valueOf(totalBytes));
    tryFlush(new Flushable[] { out });
  }
  
  public static void redirect(Reader reader, Writer writer) throws IOException {
    char[] buffer = new char[8192];
    int nRead = 0;
    while ((nRead = reader.read(buffer)) > 0)
      writer.write(buffer, 0, nRead); 
    tryFlush(new Flushable[] { writer });
  }
  
  public static void redirect(Reader reader, StringBuffer writer) throws IOException {
    char[] buffer = new char[8192];
    int nRead = 0;
    while ((nRead = reader.read(buffer)) > 0)
      writer.append(buffer, 0, nRead); 
  }
  
  public static void redirect(Reader reader, StringBuilder writer) throws IOException {
    if (reader == null)
      return; 
    char[] buffer = new char[8192];
    int nRead = 0;
    while ((nRead = reader.read(buffer)) > 0)
      writer.append(buffer, 0, nRead); 
  }
  
  public static byte[] getBytes(InputStream in) throws IOException {
    return getBytes(in, false);
  }
  
  public static byte[] getBytes(InputStream in, boolean bClose) throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    try {
      redirect(in, byteOut);
      return byteOut.toByteArray();
    } finally {
      tryClose(new Object[] { byteOut });
      if (bClose)
        tryClose(new Object[] { in }); 
    } 
  }
  
  public static String getString(Reader reader) throws IOException {
    return getString(reader, false);
  }
  
  public static String getString(Reader reader, boolean bClose) throws IOException {
    StringBuilder writer = new StringBuilder();
    try {
      redirect(reader, writer);
      return writer.toString();
    } finally {
      tryClose(new Object[] { writer });
      if (bClose)
        tryClose(new Object[] { reader }); 
    } 
  }
  
  public static String getString(InputStream in) throws IOException {
    return getString(new InputStreamReader(in));
  }
  
  public static String getString(InputStream in, boolean bClose) throws IOException {
    return getString(new InputStreamReader(in), bClose);
  }
}
