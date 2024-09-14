package org.tizen.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
  private static final String REGEX_IRI = "([a-z]([a-z]|\\d|\\+|-|\\.)*):(\\/\\/(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?((\\[(|(v[\\da-f]{1,}\\.(([a-z]|\\d|-|\\.|_|~)|[!\\$&'\\(\\)\\*\\+,;=]|:)+))\\])|((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=])*)(:\\d*)?)(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*|(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)|((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)|((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)){0})(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?";
  
  private static final String REGEX_EMAIL = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])??";
  
  private static final String REGEX_URL = "\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
  
  private static final String REGEX_FILEURI = "([a-zA-Z0-9\\-_\\.!\\~\\*'\\(\\);\\?\\@\\&=\\+$,]|(%[a-fA-F0-9]{2}))*";
  
  private static final String REGEX_VERSION = "[0-9]{1,3}\\.[0-9]{1,3}(\\.[0-9]{1,5})?";
  
  private static final String REGEX_REQUIRED_VERSION = "[0-9]{1,}\\.[0-9]{1,}(\\.[0-9]{1,})?(\\.[0-9]{1,})?";
  
  private static final String REGEX_REQUIRED_VERSION_2DIGIT = "[0-9]{1,}\\.[0-9]{1,}";
  
  private static final String REGEX_APPWIDGETID = "[0-9a-zA-Z]{10}\\.[0-9a-zA-Z]{1,52}\\.[0-9a-zA-Z]{1,}";
  
  private static final String REGEX_IPADDRESS = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
  
  private static final String REGEX_PORT = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$";
  
  private static final String REGEX_APPLICATIONID = "[0-9a-zA-Z]{10}\\.[0-9a-zA-Z]{1,52}";
  
  public static final String REGEX_PRIVILEGE = "([a-z]|[A-Z]|[0-9]|/|:|\\.|-|_)*";
  
  private static final String REGEX_UBUNTU_FILE_PATH = "([/])?(/[a-zA-Z0-9-\\s_.-]+)+";
  
  private static final String REGEX_WINDOW_FILE_PATH = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9-\\s_.-]+)+";
  
  public static final String[] WIDGET_CONTENT_FILE_EXTENSIONS = new String[] { ".html", ".htm", ".xhtml", ".xht", ".svg" };
  
  public static final String[] WIDGET_ICON_FILE_EXTENSIONS = new String[] { ".png", ".gif", ".jpg", ".ico" };
  
  public static final String[] WIDGET_ICON_FILTER_EXTENSIONS = new String[] { "*.png;*.gif;*.jpg;*.ico;" };
  
  public static final String[] SERVICE_CONTENT_FILE_EXTENSION = new String[] { ".js" };
  
  public static final String[] WARNING_SCREENSIZE_FEATURES = new String[] { "http://tizen.org/feature/screen.size.all", 
      "http://tizen.org/feature/screen.size.normal" };
  
  public static final String ASTERISK = "*";
  
  public static boolean checkForEmail(String value) {
    Pattern pattern = Pattern.compile("[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])??");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForIRI(String value) {
    Pattern pattern = Pattern.compile("([a-z]([a-z]|\\d|\\+|-|\\.)*):(\\/\\/(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?((\\[(|(v[\\da-f]{1,}\\.(([a-z]|\\d|-|\\.|_|~)|[!\\$&'\\(\\)\\*\\+,;=]|:)+))\\])|((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=])*)(:\\d*)?)(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*|(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)|((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)|((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)){0})(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?", 2);
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForURL(String value) {
    Pattern pattern = Pattern.compile("\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForFileURI(String value) {
    Pattern pattern = Pattern.compile("([a-zA-Z0-9\\-_\\.!\\~\\*'\\(\\);\\?\\@\\&=\\+$,]|(%[a-fA-F0-9]{2}))*");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForFilePathURI(String value) {
    Pattern pattern = null;
    Matcher matcher = null;
    if (OSChecker.isWindows()) {
      pattern = Pattern.compile("([a-zA-Z]:)?(\\\\[a-zA-Z0-9-\\s_.-]+)+");
    } else {
      pattern = Pattern.compile("([/])?(/[a-zA-Z0-9-\\s_.-]+)+");
    } 
    matcher = pattern.matcher(value);
    if (matcher.matches())
      return true; 
    pattern = Pattern.compile("\\b(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForIconSrc(String value) {
    if (value.contains(":"))
      return true; 
    return false;
  }
  
  public static boolean checkForServiceId(String value) {
    Pattern pattern = Pattern.compile("[0-9a-zA-Z]{10}\\.[0-9a-zA-Z]{1,52}");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
  
  public static boolean checkForFileExtension(String value, String[] extensions) {
    if (StringUtil.isEmpty(value) || extensions == null)
      return false; 
    if (value.startsWith("."))
      return false; 
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = extensions).length, b = 0; b < i; ) {
      String ext = arrayOfString[b];
      if (value.toLowerCase().endsWith(ext))
        return true; 
      b++;
    } 
    return false;
  }
  
  public static boolean checkForVersion(String version) {
    if (version == null || StringUtil.isEmpty(version))
      return false; 
    Pattern pattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}(\\.[0-9]{1,5})?");
    Matcher matcher = pattern.matcher(version);
    if (matcher.matches()) {
      String[] versions = version.split("\\.");
      if (versions.length < 3)
        return false; 
      for (int index = 0; index < 2; index++) {
        int xy = Integer.parseInt(versions[index]);
        if (xy < 0 || 255 < xy)
          return false; 
      } 
      int z = Integer.parseInt(versions[2]);
      if (z < 0 || 65535 < z)
        return false; 
      return true;
    } 
    return false;
  }
  
  public static boolean checkForRequiredVersion2Digit(String version) {
    if (version == null || StringUtil.isEmpty(version))
      return false; 
    Pattern pattern = Pattern.compile("[0-9]{1,}\\.[0-9]{1,}");
    Matcher matcher = pattern.matcher(version);
    if (matcher.matches())
      return true; 
    return false;
  }
  
  public static boolean checkForRequiredVersion(String version) {
    if (version == null || StringUtil.isEmpty(version))
      return false; 
    Pattern pattern = Pattern.compile("[0-9]{1,}\\.[0-9]{1,}(\\.[0-9]{1,})?(\\.[0-9]{1,})?");
    Matcher matcher = pattern.matcher(version);
    if (matcher.matches())
      return true; 
    return false;
  }
  
  public static boolean checkForDynamicBoxId(String id) {
    Pattern pattern = Pattern.compile("[0-9a-zA-Z]{10}\\.[0-9a-zA-Z]{1,52}\\.[0-9a-zA-Z]{1,}");
    Matcher matcher = pattern.matcher(id);
    return matcher.matches();
  }
  
  public static boolean checkForIP(String ip) {
    Pattern pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    Matcher matcher = pattern.matcher(ip);
    return matcher.matches();
  }
  
  public static boolean checkForPort(String port) {
    Pattern pattern = Pattern.compile("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$");
    Matcher matcher = pattern.matcher(port);
    return matcher.matches();
  }
  
  public static boolean checkForWidgetIconFileExtension(String iconFileName) {
    return checkForFileExtension(iconFileName, WIDGET_ICON_FILE_EXTENSIONS);
  }
  
  public static boolean checkForWidgetContentFileExtension(String contentFileName) {
    return checkForFileExtension(contentFileName, WIDGET_CONTENT_FILE_EXTENSIONS);
  }
  
  public static boolean checkForServiceContentFileExtension(String contentFileName) {
    return checkForFileExtension(contentFileName, SERVICE_CONTENT_FILE_EXTENSION);
  }
  
  public static boolean checkForWarningFeature(String featureName) {
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = WARNING_SCREENSIZE_FEATURES).length, b = 0; b < i; ) {
      String feature = arrayOfString[b];
      if (feature.equalsIgnoreCase(featureName))
        return true; 
      b++;
    } 
    return false;
  }
  
  public static boolean checkForAccessOrigin(String origin) {
    if (!"*".equals(origin) && !checkForURL(origin))
      return false; 
    return true;
  }
  
  public static boolean checkForPrivilege(String privilege) {
    Pattern pattern = Pattern.compile("([a-z]|[A-Z]|[0-9]|/|:|\\.|-|_)*");
    Matcher matcher = pattern.matcher(privilege);
    return matcher.matches();
  }
}
