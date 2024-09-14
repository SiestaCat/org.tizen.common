package org.tizen.common.launch;

import org.tizen.sdblib.IDevice;

public interface ITizenNativeLaunchConfiguration extends ITizenLaunchConfiguration {
  IDevice getDeviceFromLaunchConfiguration(IDevice[] paramArrayOfIDevice);
}
