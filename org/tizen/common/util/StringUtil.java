package org.tizen.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {
  private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
  
  public static final String EMPTY_STRING = "";
  
  public static final String NULL_STRING = "<<null>>";
  
  public static final String EMPTY_BYTES_STRING = "<<EMPTY BYTES>>";
  
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  
  public static final String TAB = "\t";
  
  protected static char CONTROL_CHARS_SHOWER = '.';
  
  protected static final char[] HEXA_CHARS = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  protected static final int N_INT_BY_BYTE = 4;
  
  protected static final int WIDTH_PER_LINE = 16;
  
  protected static char TWO_BYTES_CHARS_SHOWER = '?';
  
  public static String ISO_8859_1 = "ISO-8859-1";
  
  public static InputStream toInputStream(String src) {
    if (src == null)
      return null; 
    return new ByteArrayInputStream(src.getBytes());
  }
  
  public static String[] split(String str, String delimiters) {
    List<String> list = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(str, delimiters);
    while (tokenizer.hasMoreTokens())
      list.add(tokenizer.nextToken()); 
    return list.<String>toArray(new String[0]);
  }
  
  public static String getOnlyNumerics(String str) {
    if (str == null)
      return null; 
    final StringBuilder sb = new StringBuilder();
    ArrayUtil.iterate(
        ArrayUtil.convertToWrapper(str.toCharArray()), 
        new IteratingRunner<Character>() {
          public void run(Character arg) {
            if (Character.isDigit(arg.charValue()))
              sb.append(arg); 
          }
        });
    return sb.toString();
  }
  
  public static String asString(Reader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    int c;
    while ((c = reader.read()) != -1)
      sb.append((char)c); 
    return sb.toString();
  }
  
  public static String asString(InputStream is) throws IOException {
    StringBuffer out = new StringBuffer();
    byte[] b = new byte[4096];
    int n;
    while ((n = is.read(b)) != -1)
      out.append(new String(b, 0, n)); 
    return out.toString();
  }
  
  public static String removeEnd(String str, String remove) {
    if (isEmpty(str) || isEmpty(remove))
      return str; 
    if (str.endsWith(remove))
      return str.substring(0, str.length() - remove.length()); 
    return str;
  }
  
  public static String removeStart(String str, String remove) {
    if (isEmpty(str) || isEmpty(remove))
      return str; 
    if (str.startsWith(remove))
      return str.substring(remove.length()); 
    return str;
  }
  
  public static void appendHexa(StringBuilder buffer, int ch) {
    if (ch < 16) {
      buffer.append('0');
      buffer.append(HEXA_CHARS[0xF & ch]);
    } else {
      buffer.append(HEXA_CHARS[0xF & ch >> 4]);
      buffer.append(HEXA_CHARS[0xF & ch]);
    } 
  }
  
  protected static void lineEnd(StringBuilder hexPart, StringBuilder textPart, StringBuilder ret) {
    hexPart.append("     |");
    textPart.append("|\n");
    ret.append(hexPart);
    ret.append(textPart);
    hexPart.delete(0, hexPart.capacity());
    textPart.delete(0, textPart.capacity());
  }
  
  public static String text2hexa(byte[] data) {
    if (data == null)
      return "<<null>>"; 
    return text2hexa(data, 0, data.length);
  }
  
  public static String text2hexa(byte[] data, int offset, int length) {
    if (data == null)
      return "<<null>>"; 
    if (data.length <= 0)
      return "<<EMPTY BYTES>>"; 
    ByteArrayInputStream reader = new ByteArrayInputStream(data, offset, length);
    StringBuilder ret = new StringBuilder();
    StringBuilder hexPart = new StringBuilder();
    StringBuilder textPart = new StringBuilder();
    int address = 0;
    int ch = -1;
    int printByte = 0;
    int cnt = 0;
    hexPart.append("          ");
    int i, n;
    for (i = 0, n = 4; i < n; i++) {
      hexPart.append("+-------");
      textPart.append("+---");
    } 
    lineEnd(hexPart, textPart, ret);
    while ((ch = reader.read()) >= 0) {
      if (cnt == 0) {
        for (i = 3; i >= 0; i--) {
          printByte = 0xFF & address >> 8 * i;
          appendHexa(hexPart, printByte);
        } 
        hexPart.append("  ");
        address += 16;
      } 
      appendHexa(hexPart, ch);
      if ((ch & 0x80) != 0 || ch < 32) {
        textPart.append(CONTROL_CHARS_SHOWER);
      } else {
        textPart.append((char)ch);
      } 
      cnt++;
      if (16 == cnt) {
        lineEnd(hexPart, textPart, ret);
        cnt = 0;
      } 
    } 
    if (cnt != 0) {
      for (; cnt < 16; cnt++) {
        hexPart.append("  ");
        textPart.append(' ');
      } 
      lineEnd(hexPart, textPart, ret);
    } 
    return ret.toString();
  }
  
  public static boolean isEmpty(CharSequence str) {
    if (str == null)
      return true; 
    for (int i = 0, n = str.length(); i < n; ) {
      if (Character.isWhitespace(str.charAt(i))) {
        i++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public static int size(CharSequence str) {
    if (str == null)
      return 0; 
    return str.length();
  }
  
  public static boolean hasLength(CharSequence str) {
    return (size(str) > 0);
  }
  
  public static boolean hasText(CharSequence str) {
    return !isEmpty(str);
  }
  
  public static String nvl(String... strs) {
    String val = ObjectUtil.<String>nvl(strs);
    if (val == null)
      return ""; 
    return val;
  }
  
  public static String trimLeading(String str) {
    if (!hasLength(str))
      return str; 
    char[] chs = str.toCharArray();
    for (int i = 0, n = chs.length; i < n; i++) {
      if (!Character.isWhitespace(chs[i]))
        return new String(chs, i, str.length() - i); 
    } 
    return "";
  }
  
  public static String trimTrailing(String str) {
    if (!hasLength(str))
      return str; 
    char[] chs = str.toCharArray();
    for (int i = chs.length - 1, j = chs.length; j > 0; i--, j--) {
      if (!Character.isWhitespace(chs[i]))
        return new String(chs, 0, j); 
    } 
    return "";
  }
  
  public static String trim(String str) {
    return trimTrailing(trimLeading(str));
  }
  
  public static String getMeaningful(String str) {
    if (str == null)
      return ""; 
    return trim(str).toLowerCase();
  }
  
  public static String mask(String str, String maskingStr) {
    if (str == null)
      return "null"; 
    StringBuilder buffer = new StringBuilder();
    for (int i = 0, n = str.length(); i < n; i++)
      buffer.append(maskingStr); 
    return buffer.toString();
  }
  
  public static String getParamater(String str, char delimeter, int escaper, int targetIndex) throws IOException {
    try {
      if (str == null)
        return null; 
      StringReader reader = new StringReader(str);
      StringWriter writer = null;
      int ch = 0;
      int status = 0;
      int index = 0;
      if (targetIndex == 0)
        writer = new StringWriter(); 
      while ((ch = reader.read()) >= 0) {
        if (1 == status) {
          status = 0;
        } else {
          if (escaper == ch) {
            status = 1;
            continue;
          } 
          if (delimeter == ch) {
            if (index == targetIndex)
              return writer.toString(); 
            index++;
            if (index == targetIndex)
              writer = new StringWriter(); 
            continue;
          } 
        } 
        if (writer != null)
          writer.write(ch); 
      } 
      if (index == targetIndex)
        return writer.toString(); 
      return null;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public static String lastSubstring(String str, int indexFromLast) {
    if (str == null)
      return null; 
    int length = str.length();
    if (length < indexFromLast)
      return str; 
    return str.substring(length - indexFromLast);
  }
  
  public static String getLastSegment(String str, String separator) {
    if (str == null)
      return ""; 
    int index = str.lastIndexOf(separator);
    if (index < 0)
      return str; 
    return str.substring(index + separator.length());
  }
  
  public static String removeLastSegment(String str, String separator) {
    if (str == null)
      return ""; 
    int index = str.lastIndexOf(separator);
    if (index < 0)
      return ""; 
    return str.substring(0, index);
  }
  
  public static String multiply(String symbol, int n) {
    if (symbol == null)
      return ""; 
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < n; i++)
      buffer.append(symbol); 
    return buffer.toString();
  }
  
  public static boolean containsWhitespace(CharSequence str) {
    int nChar = size(str);
    for (int i = 0; i < nChar; i++) {
      if (Character.isWhitespace(str.charAt(i)))
        return true; 
    } 
    return false;
  }
  
  public static String trimLeadingCharacter(String str, char leadingCharacter) {
    if (!hasLength(str))
      return str; 
    char[] chs = str.toCharArray();
    for (int i = 0, n = chs.length; i < n; i++) {
      if (leadingCharacter != chs[i])
        return new String(chs, i, str.length() - i); 
    } 
    return "";
  }
  
  public static String trimTrailingCharacter(String str, char trailingCharacter) {
    if (!hasLength(str))
      return str; 
    char[] chs = str.toCharArray();
    for (int i = chs.length - 1, j = chs.length; j > 0; i--, j--) {
      if (trailingCharacter != chs[i])
        return new String(chs, 0, j); 
    } 
    return "";
  }
  
  public static String trimCharacter(String str, char character) {
    return trimTrailingCharacter(trimLeadingCharacter(str, character), character);
  }
  
  public static String getWord(String doc, int index) {
    StringBuilder buffer = new StringBuilder();
    int position = index;
    if (doc.length() < index)
      return ""; 
    int i;
    for (i = position - 1; i >= 0; i--) {
      int ch = doc.charAt(i);
      if (Character.isWhitespace(ch))
        break; 
      buffer.append((char)ch);
    } 
    buffer.reverse();
    int n;
    for (i = position, n = doc.length(); i < n; i++) {
      int ch = doc.charAt(i);
      if (Character.isWhitespace(ch))
        break; 
      buffer.append((char)ch);
    } 
    return buffer.toString();
  }
  
  public static String getPreviousWord(String doc, int index) {
    StringBuilder buffer = new StringBuilder();
    if (index < 0 || doc.length() < index) {
      logger.debug("Index[{0}] is out of bound :[0:{1}]", Integer.valueOf(index), Integer.valueOf(doc.length()));
      return "";
    } 
    int ch = 0;
    boolean bNeedWord = false;
    if (index < doc.length()) {
      ch = doc.charAt(index);
      bNeedWord = Character.isWhitespace(ch);
    } 
    logger.debug("Character at index :'{0}'", Character.valueOf((char)ch));
    for (int i = index - 1; i >= 0; i--) {
      ch = doc.charAt(i);
      if (Character.isWhitespace(ch)) {
        if (!bNeedWord) {
          logger.debug("Meet space at column {0}", Integer.valueOf(i));
          break;
        } 
      } else {
        bNeedWord = false;
        buffer.append((char)ch);
      } 
    } 
    return buffer.reverse().toString();
  }
  
  public static <T extends Enum<T>> String[] enumNameToStringArray(Enum[] values) {
    int i = 0;
    String[] result = new String[values.length];
    byte b;
    int j;
    Enum[] arrayOfEnum;
    for (j = (arrayOfEnum = values).length, b = 0; b < j; ) {
      Enum enum_ = arrayOfEnum[b];
      result[i++] = enum_.name();
      b++;
    } 
    return result;
  }
  
  public static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException numberFormatException) {
      return false;
    } 
  }
  
  public static String convertFirstLetterUpperCase(String str) {
    if (isEmpty(str))
      return null; 
    char[] str_array = str.toCharArray();
    str_array[0] = Character.toUpperCase(str_array[0]);
    str = new String(str_array);
    return str;
  }
  
  public static String getLastStringAfter(String input, String lastIndexStr) {
    if (input == null)
      return null; 
    int k = input.lastIndexOf(lastIndexStr);
    return (k != -1) ? input.substring(k + 1, input.length()) : null;
  }
  
  public static String[] splitByCamelCase(String str) {
    if (isEmpty(str))
      return new String[0]; 
    char[] c = str.toCharArray();
    List<String> list = new ArrayList<>();
    int tokenStart = 0;
    int currentType = Character.getType(c[tokenStart]);
    for (int pos = tokenStart + 1; pos < c.length; pos++) {
      int type = Character.getType(c[pos]);
      if (type != currentType) {
        if (type == 2 && currentType == 1) {
          int newTokenStart = pos - 1;
          if (newTokenStart != tokenStart) {
            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
            tokenStart = newTokenStart;
          } 
        } else {
          list.add(new String(c, tokenStart, pos - tokenStart));
          tokenStart = pos;
        } 
        currentType = type;
      } 
    } 
    list.add(new String(c, tokenStart, c.length - tokenStart));
    return list.<String>toArray(new String[0]);
  }
  
  private static int minimum(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }
  
  public static int getLevenshteinDistance(CharSequence str1, CharSequence str2) {
    int[][] distance = new int[str1.length() + 1][str2.length() + 1];
    for (int k = 0; k <= str1.length(); k++)
      distance[k][0] = k; 
    for (int j = 1; j <= str2.length(); j++)
      distance[0][j] = j; 
    for (int i = 1; i <= str1.length(); i++) {
      for (int m = 1; m <= str2.length(); m++)
        distance[i][m] = minimum(
            distance[i - 1][m] + 1, 
            distance[i][m - 1] + 1, 
            distance[i - 1][m - 1] + ((str1.charAt(i - 1) == str2.charAt(m - 1)) ? 0 : 1)); 
    } 
    return distance[str1.length()][str2.length()];
  }
  
  public static String[] split(String str, Pattern pattern) {
    List<String> result = null;
    Matcher matcher = pattern.matcher(str);
    if (matcher.matches()) {
      result = new ArrayList<>();
      for (int i = 0; i < matcher.groupCount(); i++)
        result.add(matcher.group(i + 1)); 
    } 
    return (result == null) ? null : result.<String>toArray(new String[0]);
  }
  
  public static String convertToDefaultCharset(String text, String charset) throws UnsupportedEncodingException {
    Assert.notNull(text);
    Assert.notNull(charset);
    byte[] bytes = text.getBytes(charset);
    String defaultCharset = Charset.defaultCharset().name();
    return new String(bytes, defaultCharset);
  }
}
