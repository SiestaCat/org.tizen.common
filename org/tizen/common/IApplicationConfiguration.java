package org.tizen.common;

public interface IApplicationConfiguration extends ITizenNativeProject {
  String getBinaryName();
  
  String generateAppId();
  
  String getBuildArchitecture();
}
