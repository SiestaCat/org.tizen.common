package org.tizen.common.util;

public interface ISdbCommandHelper {
  void runCommand(String paramString1, boolean paramBoolean, String paramString2) throws Exception;
  
  String[] getResultLineStrings();
}
