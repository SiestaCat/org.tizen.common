package org.tizen.common.util;

import java.io.IOException;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.PlatformCapability;
import org.tizen.sdblib.PlatformInfo;
import org.tizen.sdblib.exception.SdbCommandRejectedException;
import org.tizen.sdblib.exception.TimeoutException;

public class PlatformInformationUtil {
  public static String getPlatformVersion(IDevice device) throws TimeoutException, SdbCommandRejectedException, IOException {
    String version;
    try {
      PlatformCapability capability = device.getPlatformCapability();
      version = capability.getPlatformVersion();
      if (version.equals("unknown")) {
        PlatformInfo info = device.getPlatformInfo();
        version = info.getPlatformVersion();
      } 
    } catch (SdbCommandRejectedException sdbCommandRejectedException) {
      PlatformInfo info = device.getPlatformInfo();
      version = info.getPlatformVersion();
    } 
    return version;
  }
}
