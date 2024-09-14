package org.tizen.common.util.io;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Buffer implements Closeable {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  
  protected BufferPool pool;
  
  protected LinkedList<ByteBuffer> useds = new LinkedList<>();
  
  protected ByteBuffer readBuffer;
  
  protected ByteBuffer writeBuffer;
  
  protected LinkedList<ByteBuffer> usings = new LinkedList<>();
  
  protected int nReaded = 0;
  
  protected int size = 0;
  
  protected int index;
  
  public Buffer(BufferPool pool) {
    this.pool = pool;
  }
  
  public int size() {
    return this.size;
  }
  
  public int getBufferSize() {
    return this.useds.size() + this.usings.size() + ((this.readBuffer == null) ? 0 : 1) + ((this.writeBuffer == null) ? 0 : 1);
  }
  
  public ByteBuffer getBufferForWrite() throws IOException {
    if (this.writeBuffer == null) {
      this.writeBuffer = this.pool.borrow();
    } else if (this.writeBuffer.remaining() <= 0) {
      this.writeBuffer.flip();
      this.usings.addLast(this.writeBuffer);
      this.writeBuffer = this.pool.borrow();
    } 
    return this.writeBuffer;
  }
  
  public ByteBuffer getBufferForRead() throws IOException {
    if (this.readBuffer != null && this.readBuffer.remaining() == 0) {
      this.readBuffer.position(0);
      this.useds.add(this.readBuffer);
      this.readBuffer = null;
    } 
    if (this.readBuffer == null)
      if (this.usings.isEmpty()) {
        if (this.writeBuffer != null) {
          this.readBuffer = this.writeBuffer;
          this.readBuffer.flip();
          this.writeBuffer = null;
        } else {
          return null;
        } 
      } else {
        this.readBuffer = this.usings.removeFirst();
      }  
    return this.readBuffer;
  }
  
  public int readFrom(SocketChannel channel) throws IOException {
    int sum = 0;
    int nCnt = 0;
    while (true) {
      ByteBuffer buffer = getBufferForWrite();
      int nRead = channel.read(buffer);
      if (nRead < 0)
        throw new EOFException(); 
      if (nRead == 0) {
        this.size += sum;
        if (sum > 0)
          this.logger.info("{} bytes[{}] read from {}", new Object[] { Integer.valueOf(sum), Integer.valueOf(nCnt), channel }); 
        return sum;
      } 
      sum += nRead;
      nCnt++;
    } 
  }
  
  public int writeTo(SocketChannel channel) throws IOException {
    int sum = 0;
    while (this.size > 0) {
      ByteBuffer buffer = getBufferForRead();
      if (buffer != null) {
        int nWrite = channel.write(buffer);
        if (nWrite > 0) {
          this.size -= nWrite;
          sum += nWrite;
        } 
      } 
    } 
    this.nReaded += sum;
    this.logger.info("Send {} bytes to {}", Integer.valueOf(sum), channel);
    return sum;
  }
  
  public int read() throws IOException {
    ByteBuffer buffer = getBufferForRead();
    if (buffer == null)
      return -1; 
    int ch = 0xFF & buffer.get();
    this.size--;
    this.nReaded++;
    return ch;
  }
  
  public int readInt() throws IOException {
    int i1 = read();
    int i2 = read();
    int i3 = read();
    int i4 = read();
    return (0xFF & i1) << 24 | (0xFF & i2) << 16 | (0xFF & i3) << 8 | 0xFF & i4;
  }
  
  public void write(int value) throws IOException {
    ByteBuffer buffer = getBufferForWrite();
    buffer.put((byte)value);
    this.size++;
  }
  
  public void write(byte[] bytes) throws IOException {
    for (int i = 0, n = bytes.length; i < n; ) {
      ByteBuffer buffer = getBufferForWrite();
      int remaingSize = buffer.remaining();
      int writeSize = Math.min(remaingSize, bytes.length - i);
      buffer.put(bytes, i, writeSize);
      i += writeSize;
    } 
  }
  
  public void writeInt(int value) throws IOException {
    write(0xFF & value >> 24);
    write(0xFF & value >> 16);
    write(0xFF & value >> 8);
    write(0xFF & value);
  }
  
  public void reset() {
    if (this.readBuffer != null) {
      this.readBuffer.position();
      this.useds.add(this.readBuffer);
    } 
    this.useds.addAll(this.usings);
    if (this.writeBuffer != null) {
      this.writeBuffer.flip();
      this.useds.add(this.writeBuffer);
      this.writeBuffer = null;
    } 
    this.usings = this.useds;
    this.useds = new LinkedList<>();
    this.size += this.nReaded;
    this.nReaded = 0;
  }
  
  public synchronized void pack() throws IOException {
    for (ByteBuffer buffer : this.useds) {
      this.nReaded -= buffer.remaining();
      this.pool.release(buffer);
    } 
    this.useds.clear();
  }
  
  public synchronized void close() throws IOException {
    if (this.readBuffer != null) {
      this.pool.release(this.readBuffer);
      this.readBuffer = null;
    } 
    for (ByteBuffer buffer : this.usings)
      this.pool.release(buffer); 
    this.usings.clear();
    if (this.writeBuffer != null) {
      this.pool.release(this.writeBuffer);
      this.writeBuffer = null;
    } 
    for (ByteBuffer buffer : this.useds)
      this.pool.release(buffer); 
    this.useds.clear();
  }
  
  public static Buffer concatenate(Buffer... buffers) {
    Buffer ret = new Buffer(null);
    boolean bInit = false;
    byte b;
    int i;
    Buffer[] arrayOfBuffer;
    for (i = (arrayOfBuffer = buffers).length, b = 0; b < i; ) {
      Buffer buffer = arrayOfBuffer[b];
      if (bInit && ret.pool != buffer.pool)
        throw new IllegalArgumentException("Unmatching buffer pool"); 
      ret.pool = buffer.pool;
      bInit = true;
      buffer.reset();
      ret.usings.addAll(buffer.usings);
      ret.size += buffer.size;
      b++;
    } 
    return ret;
  }
  
  public String toString() {
    return "Buffer[" + this.size + "]";
  }
}
