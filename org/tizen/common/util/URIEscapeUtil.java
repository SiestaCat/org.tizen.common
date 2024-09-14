package org.tizen.common.util;

import java.io.IOException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

public class URIEscapeUtil {
  public static String encode(String input) throws IOException {
    StringBuilder resultStr = new StringBuilder();
    byte b;
    int i;
    char[] arrayOfChar;
    for (i = (arrayOfChar = input.toCharArray()).length, b = 0; b < i; ) {
      char ch = arrayOfChar[b];
      if (isNonASCII(ch)) {
        String result;
        try {
          result = URIUtil.encodePathQuery(String.valueOf(ch));
        } catch (URIException e) {
          throw new IOException(e);
        } 
        resultStr.append(result);
      } else if (isUnsafe(ch)) {
        resultStr.append('%');
        resultStr.append(toHex(ch / 16));
        resultStr.append(toHex(ch % 16));
      } else {
        resultStr.append(ch);
      } 
      b++;
    } 
    return resultStr.toString();
  }
  
  public static String decode(String input) throws IOException {
    try {
      return URIUtil.decode(input);
    } catch (URIException e) {
      throw new IOException(e);
    } 
  }
  
  private static char toHex(int ch) {
    return (char)((ch < 10) ? (48 + ch) : (65 + ch - 10));
  }
  
  private static boolean isUnsafe(char ch) {
    return ("% <>#{}|\\^~[]`;/?@=&$!*:+".indexOf(ch) >= 0);
  }
  
  private static boolean isNonASCII(char ch) {
    if (ch > '' || ch < '\000')
      return true; 
    return false;
  }
}
