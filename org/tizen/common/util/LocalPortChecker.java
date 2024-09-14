package org.tizen.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class LocalPortChecker {
  protected static String LOCAL_ADDRESS_IP = "127.0.0.1";
  
  public static boolean isPortInRange(int port) {
    return (port >= 0 && port < 65536);
  }
  
  public static boolean isPortAvailable(int port) {
    if (!isPortInRange(port))
      return false; 
    ServerSocket socket = null;
    try {
      InetAddress localhost = InetAddress.getByName(LOCAL_ADDRESS_IP);
      socket = new ServerSocket(port, 50, localhost);
      return true;
    } catch (IOException iOException) {
      return false;
    } finally {
      IOUtil.tryClose(new Object[] { socket });
    } 
  }
  
  public static int getAvailableLocalPort(int inc, int portBase) {
    for (int port = portBase;; port += inc) {
      if (!isPortInRange(port))
        return -1; 
      if (isPortAvailable(port))
        return port; 
    } 
  }
}
