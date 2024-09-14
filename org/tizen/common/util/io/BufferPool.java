package org.tizen.common.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferPool implements Closeable {
  protected static final int N_CREATION = 1024;
  
  protected static final int BUFFER_SIZE = 256;
  
  protected final Logger logger;
  
  protected int nCreation;
  
  protected int bufferSize;
  
  protected final ObjectPool<ByteBuffer> bufferPool;
  
  public BufferPool() {
    this.logger = LoggerFactory.getLogger(getClass());
    this.nCreation = 1024;
    this.bufferSize = 256;
    this




















































      
      .bufferPool = (new GenericObjectPoolFactory((PoolableObjectFactory)new BasePoolableObjectFactory<ByteBuffer>() {
          protected final LinkedList<ByteBuffer> buffers = new LinkedList<>();
          
          public ByteBuffer makeObject() throws Exception {
            BufferPool.this.logger.trace("{} buffer(s) exist", Integer.valueOf(this.buffers.size()));
            if (!this.buffers.isEmpty())
              return this.buffers.removeFirst(); 
            long startTime = System.currentTimeMillis();
            int totalSize = BufferPool.this.nCreation * BufferPool.this.bufferSize;
            ByteBuffer totalBuffer = ByteBuffer.allocateDirect(totalSize);
            for (int limit = BufferPool.this.bufferSize; limit <= totalSize; limit += BufferPool.this.bufferSize) {
              totalBuffer.limit(limit);
              this.buffers.add(totalBuffer.slice());
              totalBuffer.position(limit);
            } 
            long endTime = System.currentTimeMillis();
            BufferPool.this.logger.trace("Buffer creation time :{} ms", Long.valueOf(endTime - startTime));
            return this.buffers.removeFirst();
          }
          
          public void activateObject(ByteBuffer buffer) throws Exception {
            super.activateObject(buffer);
            buffer.clear();
          }
        }-1)).createPool();
  }
  
  public void setNumberOfFragments(int nCreation) {
    this.nCreation = nCreation;
  }
  
  public void setSizeOfFragment(int bufferSize) {
    this.bufferSize = bufferSize;
  }
  
  public ByteBuffer borrow() throws IOException {
    try {
      return (ByteBuffer)this.bufferPool.borrowObject();
    } catch (Exception e) {
      throw new IOException(e);
    } 
  }
  
  public void release(ByteBuffer buffer) throws IOException {
    try {
      this.bufferPool.returnObject(buffer);
    } catch (Exception e) {
      throw new IOException(e);
    } 
  }
  
  public void close() throws IOException {
    try {
      this.bufferPool.close();
    } catch (Exception e) {
      throw new IOException(e);
    } 
  }
  
  public String toString() {
    return ObjectUtils.toString(this);
  }
}
