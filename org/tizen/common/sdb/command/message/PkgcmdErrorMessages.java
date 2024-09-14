package org.tizen.common.sdb.command.message;

import org.eclipse.osgi.util.NLS;

public class PkgcmdErrorMessages {
  public static String MESSAGE_FORMAT;
  
  public static String ERROR_PACKAGE_NOT_FOUND;
  
  public static String ERROR_PACKAGE_INVALID;
  
  public static String ERROR_PACKAGE_LOWER_VARSION;
  
  public static String ERROR_PACKAGE_EXECUTABLE_NOT_FOUND;
  
  public static String ERROR_RPM_INSTALLER_ERR_NOT_SUPPORTED_API_VERSION;
  
  public static String ERROR_MANIFEST_NOT_FOUND;
  
  public static String ERROR_MANIFEST_INVALID;
  
  public static String ERROR_CONFIG_NOT_FOUND;
  
  public static String ERROR_CONFIG_INVALID;
  
  public static String ERROR_INVALID_PRIVILEGE;
  
  public static String ERROR_PRIVILEGE_LEVEL_VIOLATION;
  
  public static String ERROR_UNAUTHORIZED_FAILED;
  
  public static String ERROR_PRIVILEGE_UNKNOWN_FAILED;
  
  public static String ERROR_PRIVILEGE_USING_LEGACY_FAILED;
  
  public static String ERROR_MENU_ICON_NOT_FOUND;
  
  public static String ERROR_FATAL_ERROR;
  
  public static String ERROR_OUT_OF_STORAGE;
  
  public static String ERROR_OUT_OF_MEMORY;
  
  public static String ERROR_ARGUMENT_INVALID;
  
  public static String ERROR_SIGNATURE_DEFAULT;
  
  public static String ERROR_SIGNATURE_NOT_FOUND;
  
  public static String ERROR_SIGNATURE_INVALID;
  
  public static String ERROR_SIGNATURE_VERIFICATION_FAILED;
  
  public static String ERROR_ROOT_CERTIFICATE_NOT_FOUND;
  
  public static String ERROR_CERTIFICATE_INVALID;
  
  public static String ERROR_CERTIFICATE_CHAIN_VERIFICATION_FAILED;
  
  public static String ERROR_CERTIFICATE_EXPIRED;
  
  public static String ERROR_WEB_PACKAGE_ALREADY_INSTALLED;
  
  public static String ERROR_WEB_ACE_CHECK_FAILED;
  
  public static String ERROR_WEB_MANIFEST_CREATE_FAILED;
  
  public static String ERROR_WEB_ENCRYPTION_FAILED;
  
  public static String ERROR_WEB_INSTALL_CORE_SERVICE;
  
  public static String ERROR_WEB_NOT_DEVELOPER_MODE;
  
  public static String ERROR_WEB_UNINSTALLATION_FAILED;
  
  public static String ERROR_WEB_PLUGIN_INSTALLATION_FAILED;
  
  public static String ERROR_WEB_NOT_SUPPORTED_RDS_UPDATE;
  
  public static String ERROR_UNKNOWN;
  
  static {
    NLS.initializeMessages(PkgcmdErrorMessages.class.getName(), PkgcmdErrorMessages.class);
  }
}
