package org.tizen.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilenameUtil {
  public static final char SEPARATOR_DIRECTORY = '/';
  
  public static final int IS_VALID_NAME = 0;
  
  public static final int HAS_INVALID_CHARACTER = 1;
  
  public static final int HAS_NO_NAME = 2;
  
  public static final int IS_NULL = 3;
  
  protected static final Logger logger = LoggerFactory.getLogger(FilenameUtil.class);
  
  public static String[] getCanonicalFragments(String path) {
    ArrayList<String> ret = new ArrayList<>();
    char[] characters = path.toCharArray();
    StringBuilder buffer = new StringBuilder();
    int status = 0;
    for (int i = 0, n = characters.length; i < n; i++) {
      char ch = characters[i];
      switch (status) {
        case 0:
          if ('/' == ch || File.separatorChar == ch) {
            String fragment = buffer.toString();
            if (!StringUtil.isEmpty(fragment))
              ret.add(fragment); 
            buffer.delete(0, buffer.length());
            break;
          } 
          if ('\'' == ch) {
            status = 3;
            break;
          } 
          buffer.append(ch);
          break;
        case 2:
          if ('\'' != ch) {
            buffer.append('\\');
            buffer.append(ch);
          } 
          status = 3;
          break;
        case 3:
          if ('\'' == ch) {
            status = 0;
            break;
          } 
          if ('\\' == ch) {
            status = 2;
            break;
          } 
          buffer.append(ch);
          break;
      } 
    } 
    if (buffer.length() > 0) {
      String fragment = buffer.toString();
      if (!StringUtil.isEmpty(fragment))
        ret.add(fragment); 
      buffer.delete(0, buffer.length());
    } 
    return ret.<String>toArray(new String[0]);
  }
  
  public static String[] getCanonicalFragmentsWithNoEscape(String path) {
    ArrayList<String> ret = new ArrayList<>();
    char[] characters = path.toCharArray();
    StringBuilder buffer = new StringBuilder();
    for (int i = 0, n = characters.length; i < n; i++) {
      char ch = characters[i];
      if ('/' == ch || File.separatorChar == ch) {
        String fragment = buffer.toString();
        if (!StringUtil.isEmpty(fragment))
          ret.add(fragment); 
        buffer.delete(0, buffer.length());
      } else {
        buffer.append(ch);
      } 
    } 
    if (buffer.length() > 0) {
      String fragment = buffer.toString();
      if (!StringUtil.isEmpty(fragment))
        ret.add(fragment); 
      buffer.delete(0, buffer.length());
    } 
    return ret.<String>toArray(new String[0]);
  }
  
  public static String getTailingPath(String path, int start) {
    String[] fragments = getCanonicalFragments(path);
    StringBuilder buffer = new StringBuilder();
    for (int i = Math.max(0, ArrayUtil.<String>size(fragments) - start), n = ArrayUtil.size(fragments); i < n; i++) {
      buffer.append(fragments[i]);
      if (i < ArrayUtil.size((T[])fragments) - 1)
        buffer.append('/'); 
    } 
    return buffer.toString();
  }
  
  public static String removeTailingPath(String path, int numberOfPath) {
    logger.trace("Path :{}, Index :{}", path, Integer.valueOf(numberOfPath));
    String[] fragments = getCanonicalFragments(path);
    StringBuilder buffer = new StringBuilder();
    for (int i = 0, n = Math.max(0, ArrayUtil.<String>size(fragments) - numberOfPath); i < n; i++) {
      buffer.append('/');
      buffer.append(fragments[i]);
    } 
    String ret = buffer.toString();
    if (ret.length() > 0)
      return buffer.toString().substring((path.startsWith("/") || path.startsWith(File.separator)) ? 0 : 1); 
    return "";
  }
  
  public static String addTailingPath(String path, String pathToAdd) {
    return String.valueOf(StringUtil.trimTrailingCharacter(path, '/')) + '/' + StringUtil.trimLeadingCharacter(pathToAdd, '/');
  }
  
  public static boolean isAncestor(String root, String filePath) {
    String[] rootFragments = getCanonicalFragments(root);
    String[] fileFragments = getCanonicalFragments(filePath);
    if (fileFragments.length < rootFragments.length)
      return false; 
    for (int i = 0, n = rootFragments.length; i < n; i++) {
      if (!ObjectUtil.equals(rootFragments[i], fileFragments[i]))
        return false; 
    } 
    return true;
  }
  
  public static String getRelativePath2(String root, String filePath) {
    String[] rootFragments = getCanonicalFragmentsWithNoEscape(root);
    String[] fileFragments = getCanonicalFragmentsWithNoEscape(filePath);
    int nRoot = rootFragments.length;
    int nFile = fileFragments.length;
    StringBuilder buffer = new StringBuilder();
    int nLoop = (nRoot < nFile) ? 
      nRoot : nFile;
    int nStartDiffer = nLoop;
    int i;
    for (i = 0; i < nLoop; i++) {
      if (!ObjectUtil.equals(rootFragments[i], fileFragments[i])) {
        nStartDiffer = i;
        break;
      } 
    } 
    for (i = 0; i < nRoot - nStartDiffer; i++) {
      if (buffer.length() > 0)
        buffer.append('/'); 
      buffer.append("..");
    } 
    for (i = nStartDiffer; i < fileFragments.length; i++) {
      if (buffer.length() > 0)
        buffer.append('/'); 
      buffer.append(fileFragments[i]);
    } 
    logger.debug("Calculated relative path: {}", buffer.toString());
    return buffer.toString();
  }
  
  public static String getRelativePath(String root, String filePath) {
    String[] rootFragments = getCanonicalFragments(root);
    String[] fileFragments = getCanonicalFragments(filePath);
    int nRoot = rootFragments.length;
    int nFile = fileFragments.length;
    StringBuilder buffer = new StringBuilder();
    int nLoop = (nRoot < nFile) ? 
      nRoot : nFile;
    int nStartDiffer = nLoop;
    int i;
    for (i = 0; i < nLoop; i++) {
      if (!ObjectUtil.equals(rootFragments[i], fileFragments[i])) {
        nStartDiffer = i;
        break;
      } 
    } 
    for (i = 0; i < nRoot - nStartDiffer; i++) {
      if (buffer.length() > 0)
        buffer.append('/'); 
      buffer.append("..");
    } 
    for (i = nStartDiffer; i < fileFragments.length; i++) {
      if (buffer.length() > 0)
        buffer.append('/'); 
      buffer.append(fileFragments[i]);
    } 
    return buffer.toString();
  }
  
  public static String getCanonicalPath(File file) throws IOException {
    String osCanonicalPath = file.getCanonicalPath();
    return getCanonicalForm(osCanonicalPath);
  }
  
  public static String getCanonicalPath(String path) throws IOException {
    if (!FileUtil.isExist(path))
      return null; 
    File file = new File(path);
    return getCanonicalPath(file);
  }
  
  public static String getCanonicalForm(String path) {
    logger.trace("Path :{}", path);
    String[] fragments = getCanonicalFragments(path.replace(File.separatorChar, '/'));
    Stack<String> stack = new Stack<>();
    for (int i = 0, n = ArrayUtil.size(fragments); i < n; i++) {
      String fragment = fragments[i];
      if (!".".equals(fragment))
        if ("..".equals(fragment)) {
          if (!stack.isEmpty())
            stack.pop(); 
        } else {
          stack.push(fragment);
        }  
    } 
    if (stack.isEmpty())
      return "/"; 
    StringBuilder buffer = new StringBuilder();
    for (String f : stack) {
      buffer.append("/");
      buffer.append(f);
    } 
    return buffer.toString();
  }
  
  public static String getFilename(String path) {
    return getTailingPath(path, 1);
  }
  
  public static String getEscapedName(String fileName) {
    Pattern sEscapePattern = Pattern.compile("([\\\\\"$])");
    return sEscapePattern.matcher(fileName).replaceAll("\\\\$1");
  }
  
  public static int isVaildName(String fileName, int os) {
    int result = 0;
    if (fileName == null) {
      result = 3;
    } else if (fileName.trim().length() == 0) {
      result = 2;
    } else {
      Pattern invalidPattern;
      switch (os) {
        case 256:
          invalidPattern = Pattern.compile("([\\\\/:*?\"<>|])");
          break;
        default:
          invalidPattern = Pattern.compile("([/])");
          break;
      } 
      if (invalidPattern.matcher(fileName).find())
        result = 1; 
    } 
    return result;
  }
  
  public static boolean isVaildName(String fileName) {
    return (isVaildName(fileName, OSChecker.getOSID()) == 0);
  }
  
  public static String getInvalidCharacters(int os) {
    switch (os) {
      case 256:
        return "\\\\/:*?\"<>|";
      case 1024:
        return "/";
    } 
    return "/";
  }
  
  public static String getInvalidCharacters() {
    return getInvalidCharacters(OSChecker.getOSID());
  }
  
  public static String addDoubleQuote(String str) {
    return "\"" + str + "\"";
  }
  
  public static String getName(String nameWithExt) {
    Assert.notNull(nameWithExt);
    int index = nameWithExt.lastIndexOf('.');
    if (index < 0)
      return nameWithExt; 
    return nameWithExt.substring(0, index);
  }
  
  public static String getExtension(String nameWithExt) {
    Assert.notNull(nameWithExt);
    int index = nameWithExt.lastIndexOf('.');
    if (index < 0)
      return ""; 
    return nameWithExt.substring(index + 1);
  }
  
  public static boolean equals(File file1, File file2) {
    try {
      return ObjectUtil.equals(
          getCanonicalPath(file1), 
          getCanonicalPath(file2));
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    } 
  }
  
  public static boolean equals(String path1, String path2) {
    return ObjectUtil.equals(path1, path2);
  }
  
  public static boolean hasExtension(String name, String ext) {
    if (StringUtil.isEmpty(name))
      return false; 
    return getExtension(name).equalsIgnoreCase(ext);
  }
}
