package org.tizen.common.util.io;

import java.io.IOException;
import java.io.OutputStream;
import org.tizen.common.util.Assert;

public class BufferOutputStream extends OutputStream {
  protected Buffer buffer;
  
  public BufferOutputStream(Buffer buffer) {
    Assert.notNull(buffer);
    this.buffer = buffer;
  }
  
  public Buffer getBuffer() {
    return this.buffer;
  }
  
  public void write(int b) throws IOException {
    this.buffer.write(b);
  }
}
