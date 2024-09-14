package org.tizen.common.util;

public class OSChecker {
  public static final int WINDOWS = 256;
  
  public static final int WINDOWSXP = 257;
  
  public static final int WINDOWS7 = 258;
  
  public static final int MAC = 512;
  
  public static final int LINUX = 1024;
  
  public static final int UNIX = 2048;
  
  public static final int AIX = 4096;
  
  public static final int SOLARIS = 8192;
  
  public static final int SUN = 1;
  
  public static final int MICROSOFT = 2;
  
  public static final int APPLE = 4;
  
  public static final int IBM = 8;
  
  static int osID = 0;
  
  static int vendorID = 0;
  
  static {
    osID = 0;
    String osName = System.getProperty("os.name").toUpperCase();
    if (osName.indexOf("WINDOWS") >= 0) {
      osID |= 0x100;
      if (ObjectUtil.equals(osName, "WINDOWS XP")) {
        osID |= 0x101;
      } else if (ObjectUtil.equals(osName, "WINDOWS 7")) {
        osID |= 0x102;
      } 
    } else if (osName.indexOf("MAC") >= 0) {
      osID |= 0x200;
    } else if (osName.indexOf("LINUX") >= 0) {
      osID |= 0x400;
    } else if (osName.indexOf("AIX") >= 0) {
      osID |= 0x1000;
      osID |= 0x800;
    } else if (osName.indexOf("SOLARIS") >= 0) {
      osID |= 0x2000;
      osID |= 0x800;
    } 
    vendorID = 0;
    String vendorName = System.getProperty("java.vendor").toUpperCase();
    if (vendorName.indexOf("IBM") >= 0) {
      vendorID |= 0x8;
    } else if (vendorName.indexOf("MICROSOFT") >= 0) {
      vendorID |= 0x2;
    } else if (vendorName.indexOf("APPLE") >= 0) {
      vendorID |= 0x4;
    } else if (vendorName.indexOf("SUN") >= 0) {
      vendorID |= 0x1;
    } 
  }
  
  public static int getOSID() {
    return osID;
  }
  
  public static boolean isWindows() {
    return ((osID & 0x100) > 0);
  }
  
  public static boolean isWindowsXP() {
    return (osID == 257);
  }
  
  public static boolean isWindows7() {
    return (osID == 258);
  }
  
  public static boolean isMAC() {
    return ((osID & 0x200) > 0);
  }
  
  public static boolean isLinux() {
    return ((osID & 0x400) > 0);
  }
  
  public static boolean isAIX() {
    return ((osID & 0x1000) > 0);
  }
  
  public static boolean isSolaris() {
    return ((osID & 0x2000) > 0);
  }
  
  public static boolean isUnix() {
    return ((osID & 0x800) > 0);
  }
  
  public static boolean isUnknownOS() {
    return (osID == 0);
  }
  
  public static int getVendorID() {
    return vendorID;
  }
  
  public static boolean byMicrosoft() {
    return ((vendorID & 0x2) > 0);
  }
  
  public static boolean byIBM() {
    return ((vendorID & 0x8) > 0);
  }
  
  public static boolean byApple() {
    return ((vendorID & 0x4) > 0);
  }
  
  public static boolean bySun() {
    return ((vendorID & 0x1) > 0);
  }
  
  public static boolean byUnknownVendor() {
    return (vendorID == 0);
  }
  
  public static boolean is64bit() {
    boolean is64bit = false;
    if (isWindows()) {
      is64bit = (System.getenv("ProgramFiles(x86)") != null);
    } else {
      is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
    } 
    return is64bit;
  }
}
