package org.tizen.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamGobbler extends Thread {
  InputStream is;
  
  OutputStream os;
  
  String result;
  
  private static Object synchronizer = new Object();
  
  public StreamGobbler(InputStream is) {
    this.is = is;
    this.os = null;
  }
  
  public StreamGobbler(InputStream is, OutputStream os) {
    this.is = is;
    this.os = os;
  }
  
  public void run() {
    StringBuffer buffer = new StringBuffer();
    BufferedReader br = null;
    try {
      synchronized (synchronizer) {
        br = new BufferedReader(new InputStreamReader(this.is));
        String line;
        while ((line = br.readLine()) != null) {
          buffer.append(line);
          buffer.append("\n");
          if (this.os != null)
            this.os.write((String.valueOf(line) + "\n").getBytes()); 
        } 
      } 
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      this.result = buffer.toString();
      IOUtil.tryClose(new Object[] { br });
    } 
  }
  
  public String getResult() {
    return this.result;
  }
}
