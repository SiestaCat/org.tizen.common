package org.tizen.common.util.io;

import java.io.IOException;
import java.io.InputStream;
import org.tizen.common.util.Assert;

public class BufferInputStream extends InputStream {
  protected final Buffer buffer;
  
  public BufferInputStream(Buffer buffer) {
    Assert.notNull(buffer);
    this.buffer = buffer;
  }
  
  public int read() throws IOException {
    return this.buffer.read();
  }
}
