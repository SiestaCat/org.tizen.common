package org.tizen.common.sdb.command.message;

public enum ErrorMessageType {
  SUCCESS(0, null),
  SIGNATURE_ERROR(
    -14, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  SIGNATURE_INVALID_P3(-13, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  CERTIFICATE_ERROR(-12, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  AUTHOR_CERTIFICATE_NOT_MATCH(-11, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  AUTHOR_CERTIFICATE_NOT_FOUND(-10, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  PACKAGE_NOT_FOUND(
    1, PkgcmdErrorMessages.ERROR_PACKAGE_NOT_FOUND),
  PACKAGE_INVALID(2, PkgcmdErrorMessages.ERROR_PACKAGE_INVALID),
  PACKAGE_LOWER_VERSION(3, PkgcmdErrorMessages.ERROR_PACKAGE_LOWER_VARSION),
  PACKAGE_EXECUTABLE_NOT_FOUND(4, PkgcmdErrorMessages.ERROR_PACKAGE_EXECUTABLE_NOT_FOUND),
  RPM_INSTALLER_ERR_NOT_SUPPORTED_API_VERSION(5, PkgcmdErrorMessages.ERROR_RPM_INSTALLER_ERR_NOT_SUPPORTED_API_VERSION),
  MANIFEST_NOT_FOUND(11, PkgcmdErrorMessages.ERROR_MANIFEST_NOT_FOUND),
  MANIFEST_INVALID(12, PkgcmdErrorMessages.ERROR_MANIFEST_INVALID),
  CONFIG_NOT_FOUND(13, PkgcmdErrorMessages.ERROR_CONFIG_NOT_FOUND),
  CONFIG_INVALID(14, PkgcmdErrorMessages.ERROR_CONFIG_INVALID),
  SIGNATURE_NOT_FOUND(21, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  SIGNATURE_INVALID(22, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  SIGNATURE_VERIFICATION_FAILED(23, PkgcmdErrorMessages.ERROR_SIGNATURE_VERIFICATION_FAILED),
  ROOT_CERTIFICATE_NOT_FOUND(31, PkgcmdErrorMessages.ERROR_ROOT_CERTIFICATE_NOT_FOUND),
  CERTIFICATE_INVALID(32, PkgcmdErrorMessages.ERROR_SIGNATURE_DEFAULT, new CertificateDefaultErrorHandler()),
  CERTIFICATE_CHAIN_VERIFICATION_FAILED(33, PkgcmdErrorMessages.ERROR_CERTIFICATE_CHAIN_VERIFICATION_FAILED),
  CERTIFICATE_EXPIRED(34, PkgcmdErrorMessages.ERROR_CERTIFICATE_EXPIRED),
  INVALID_PRIVILEGE(41, PkgcmdErrorMessages.ERROR_INVALID_PRIVILEGE),
  PRIVILEGE_LEVEL_VIOLATION(42, PkgcmdErrorMessages.ERROR_PRIVILEGE_LEVEL_VIOLATION),
  PRIVILEGE_UNAUTHORIZED_FAILED(43, PkgcmdErrorMessages.ERROR_UNAUTHORIZED_FAILED),
  PRIVILEGE_UNKNOWN_FAILED(44, PkgcmdErrorMessages.ERROR_PRIVILEGE_UNKNOWN_FAILED),
  PRIVILEGE_USING_LEGACY_FAILED(45, PkgcmdErrorMessages.ERROR_PRIVILEGE_USING_LEGACY_FAILED),
  MENU_ICON_NOT_FOUND(51, PkgcmdErrorMessages.ERROR_MENU_ICON_NOT_FOUND),
  FATAL_ERROR(61, PkgcmdErrorMessages.ERROR_FATAL_ERROR),
  OUT_OF_STORAGE(62, PkgcmdErrorMessages.ERROR_OUT_OF_STORAGE),
  OUT_OF_MEMORY(63, PkgcmdErrorMessages.ERROR_OUT_OF_MEMORY),
  ARGUMENT_INVALID(64, PkgcmdErrorMessages.ERROR_ARGUMENT_INVALID),
  WEB_PACKAGE_ALREADY_INSTALLED(
    121, PkgcmdErrorMessages.ERROR_WEB_PACKAGE_ALREADY_INSTALLED),
  WEB_ACE_CHECK_FAILED(122, PkgcmdErrorMessages.ERROR_WEB_ACE_CHECK_FAILED),
  WEB_MANIFEST_CREATE_FAILED(123, PkgcmdErrorMessages.ERROR_WEB_MANIFEST_CREATE_FAILED),
  WEB_ENCRYPTION_FAILED(124, PkgcmdErrorMessages.ERROR_WEB_ENCRYPTION_FAILED),
  WEB_INSTALL_CORE_SERVICE(125, PkgcmdErrorMessages.ERROR_WEB_INSTALL_CORE_SERVICE),
  WEB_PLUGIN_INSTALLATION_FAILED(
    126, PkgcmdErrorMessages.ERROR_WEB_PLUGIN_INSTALLATION_FAILED),
  WEB_UNINSTALLATION_FAILED(127, PkgcmdErrorMessages.ERROR_WEB_UNINSTALLATION_FAILED),
  WEB_NOT_SUPPORTED_RDS_UPDATE(
    128, PkgcmdErrorMessages.ERROR_WEB_NOT_SUPPORTED_RDS_UPDATE),
  WEB_UNKNOWN_ERROR(140, PkgcmdErrorMessages.ERROR_UNKNOWN);
  
  private int exitCode;
  
  private String management;
  
  private ICommandErrorHandler handler;
  
  ErrorMessageType(int exitCode, String management) {
    this.exitCode = exitCode;
    this.management = management;
  }
  
  ErrorMessageType(int exitCode, String management, ICommandErrorHandler handler) {
    this.exitCode = exitCode;
    this.management = management;
    this.handler = handler;
  }
  
  public String getManagement() {
    return this.management;
  }
  
  public String toString() {
    return name();
  }
  
  public int getExitCode() {
    return this.exitCode;
  }
  
  public ICommandErrorHandler getErrorHandler() {
    return this.handler;
  }
  
  public static ErrorMessageType getErrorType(int exitCode) {
    byte b;
    int i;
    ErrorMessageType[] arrayOfErrorMessageType;
    for (i = (arrayOfErrorMessageType = values()).length, b = 0; b < i; ) {
      ErrorMessageType message = arrayOfErrorMessageType[b];
      if (message.getExitCode() == exitCode)
        return message; 
      b++;
    } 
    return null;
  }
}
