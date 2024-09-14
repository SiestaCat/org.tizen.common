package org.tizen.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
  private static final int BUFFER_SIZE = 8192;
  
  protected static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
  
  public static boolean createDirectory(File dir) {
    return dir.mkdirs();
  }
  
  public static boolean createDirectory(String fullPath) {
    return createDirectory(new File(fullPath));
  }
  
  public static boolean createDirectory(String dirName, String destDir) {
    Assert.notNull(dirName);
    if (destDir == null)
      destDir = System.getProperty("user.dir"); 
    String fullPath = FilenameUtil.addTailingPath(destDir, dirName);
    return createDirectory(new File(fullPath));
  }
  
  public static boolean createTextFile(File file, String text, String encoding) throws IOException {
    if (!file.createNewFile())
      return false; 
    return writeTextFile(file, text, encoding);
  }
  
  public static IFile getIFile(IProject project, String path, String text, String encoding) throws IOException {
    File file = new File(path);
    if (!file.createNewFile())
      return null; 
    writeTextFile(file, text, encoding, true);
    IFile iFile = project.getFile(path.replace(project.getLocation().toString(), ""));
    return iFile;
  }
  
  public static boolean createTextFile(String fullPath, String text, String encoding) throws IOException {
    return createTextFile(new File(fullPath), text, encoding);
  }
  
  public static boolean createTextFile(String fileName, String text, String destDir, String encoding) throws IOException {
    Assert.notNull(fileName);
    if (destDir == null)
      destDir = System.getProperty("user.dir"); 
    String fullPath = FilenameUtil.addTailingPath(destDir, fileName);
    return createTextFile(fullPath, text, encoding);
  }
  
  public static boolean writeTextFile(File file, String text, String encoding) throws IOException {
    return writeTextFile(file, text, encoding, false);
  }
  
  public static boolean appendTextFile(File file, String text, String encoding) throws IOException {
    return writeTextFile(file, text, encoding, true);
  }
  
  private static boolean writeTextFile(File file, String text, String encoding, boolean doAppend) throws IOException {
    if (encoding == null)
      encoding = System.getProperty("file.encoding"); 
    File parent = file.getAbsoluteFile().getParentFile();
    if (parent != null && !parent.exists() && 
      !parent.mkdirs())
      throw new IOException("File write error. Cannot create directory - " + parent.getAbsolutePath()); 
    BufferedWriter out = null;
    FileOutputStream fileOut = new FileOutputStream(file, doAppend);
    try {
      out = new BufferedWriter(new OutputStreamWriter(fileOut, encoding), 8192);
      out.write(text.toCharArray(), 0, text.length());
      out.flush();
    } finally {
      IOUtil.tryClose(new Object[] { out, fileOut });
    } 
    return true;
  }
  
  public static String readTextFile(File file, String encoding) throws IOException {
    FileInputStream fileInputStream = null;
    String text = null;
    try {
      fileInputStream = new FileInputStream(file);
      text = readTextStream(fileInputStream, encoding);
      return text;
    } finally {
      IOUtil.tryClose(new Object[] { fileInputStream });
    } 
  }
  
  public static String readTextStream(InputStream input, String encoding) throws IOException {
    Assert.notNull(input);
    Reader in = null;
    encoding = StringUtil.nvl(new String[] { encoding, System.getProperty("file.encoding") });
    try {
      return IOUtil.getString(new BufferedReader(
            in = new InputStreamReader(input, encoding), 
            8192));
    } finally {
      IOUtil.tryClose(new Object[] { in });
    } 
  }
  
  public static String getFileExtension(String fullName) {
    return StringUtil.getLastStringAfter(fullName, ".");
  }
  
  public static String getFileNameWithoutExtension(String fullName) {
    if (fullName == null)
      return null; 
    int k = fullName.lastIndexOf(".");
    return (k != -1) ? fullName.substring(0, k) : fullName;
  }
  
  public static String getFileNameFromPath(String path) {
    String result = StringUtil.getLastStringAfter(path, "/");
    if (result == null)
      result = StringUtil.getLastStringAfter(path, "\\"); 
    if (result == null)
      result = path; 
    return result;
  }
  
  public static boolean recursiveDelete(File file) {
    boolean result = true;
    if (file == null)
      return false; 
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        byte b;
        int i;
        File[] arrayOfFile;
        for (i = (arrayOfFile = children).length, b = 0; b < i; ) {
          File child = arrayOfFile[b];
          result &= recursiveDelete(child);
          b++;
        } 
      } 
    } 
    result &= file.delete();
    return result;
  }
  
  public static boolean recursiveDelete(String path) {
    if (!isExist(path))
      return false; 
    File file = new File(path);
    return recursiveDelete(file);
  }
  
  public static void redirectStream(InputStream is, OutputStream os) throws IOException {
    byte[] buffer = new byte[8192];
    BufferedInputStream bin = new BufferedInputStream(is, 8192);
    BufferedOutputStream bout = new BufferedOutputStream(os, 8192);
    int n = 0;
    while ((n = bin.read(buffer, 0, buffer.length)) >= 0)
      bout.write(buffer, 0, n); 
    bout.flush();
  }
  
  public static void copyTo(String from, String to) throws IOException {
    copyTo(from, to, false);
  }
  
  public static void copyTo(String from, String to, boolean append) throws IOException {
    File fromFile = new File(from);
    File toFile = new File(to);
    copyTo(fromFile, toFile, append);
  }
  
  public static void copyTo(File fromFile, File toFile, boolean append) throws IOException {
    File parent = toFile.getParentFile();
    if (parent != null && !parent.exists() && 
      !parent.mkdirs())
      throw new IOException("File copy error. Cannot create directory - " + parent.getAbsolutePath()); 
    if (FilenameUtil.equals(fromFile, toFile))
      throw new IOException("Unable to write file " + fromFile + " on itself."); 
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(fromFile);
      in = new BufferedInputStream(in, 8192);
      out = new FileOutputStream(toFile, append);
      out = new BufferedOutputStream(out, 8192);
      IOUtil.redirect(in, out);
    } finally {
      IOUtil.tryClose(new Object[] { in, out });
    } 
  }
  
  public static void copyRecursively(String from, String to) throws IOException {
    copyRecursively(from, to, true);
  }
  
  public static void copyRecursively(String from, String to, boolean overwrite) throws IOException {
    copyRecursively(from, to, overwrite, new File[0]);
  }
  
  public static void copyRecursively(String from, String to, boolean overwrite, File... filters) throws IOException {
    File fromDir = new File(from);
    File toDir = new File(to);
    if (!fromDir.exists()) {
      logger.warn("Directory {} does not exist.", fromDir.getCanonicalPath());
      return;
    } 
    checkDirectory(fromDir, toDir);
    copyRecursivelyWithoutDirChecking(fromDir, toDir, overwrite, filters);
  }
  
  private static void checkDirectory(File fromDir, File toDir) throws IOException {
    if (!toDir.exists()) {
      toDir.mkdirs();
    } else if (toDir.isFile()) {
      throw new IOException("destination directory " + toDir.toString() + " is a file.");
    } 
    if (!fromDir.exists())
      throw new IOException("source directory " + fromDir.toString() + " does not exist."); 
    if (fromDir.isFile())
      throw new IOException("source directory " + fromDir.toString() + " is a file."); 
  }
  
  private static void copyRecursivelyWithoutDirChecking(File fromDir, File toDir, boolean overwrite, File... filters) throws IOException {
    HashSet<String> filterSet = new HashSet<>();
    if (filters != null) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = filters).length, b = 0; b < i; ) {
        File filter = arrayOfFile[b];
        filterSet.add(filter.getCanonicalPath());
        b++;
      } 
    } 
    Stack<File> fromFileStack = new Stack<>();
    Stack<File> toFileStack = new Stack<>();
    fromFileStack.add(fromDir);
    toFileStack.add(toDir);
    while (!fromFileStack.isEmpty()) {
      File fromFile = fromFileStack.pop();
      File toFile = toFileStack.pop();
      if (fromFile.isDirectory()) {
        toFile.mkdirs();
        File[] newFromFiles = fromFile.listFiles();
        if (newFromFiles != null) {
          byte b;
          int i;
          File[] arrayOfFile;
          for (i = (arrayOfFile = newFromFiles).length, b = 0; b < i; ) {
            File newFromFile = arrayOfFile[b];
            fromFileStack.push(newFromFile);
            toFileStack.push(new File(toFile, newFromFile.getName()));
            b++;
          } 
        } 
        continue;
      } 
      if (fromFile.isFile() && (
        !toFile.exists() || !toFile.isFile() || overwrite) && (
        filterSet.size() == 0 || !filterSet.contains(fromFile.getCanonicalPath())))
        copyTo(fromFile, toFile, false); 
    } 
  }
  
  public static boolean equals(File file1, File file2) throws IOException {
    if (file1 == null || file2 == null)
      return false; 
    String strFile1 = file1.getCanonicalPath();
    String strFile2 = file2.getCanonicalPath();
    if (strFile1 == null || strFile2 == null)
      return false; 
    return strFile1.equals(strFile2);
  }
  
  public static boolean checkParentDirectory(File file) {
    Assert.notNull(file);
    File parent = file.getParentFile();
    if (parent == null)
      return false; 
    if (!parent.exists())
      parent.mkdirs(); 
    return true;
  }
  
  public static boolean checkParentDirectory(String filename) {
    Assert.notNull(filename);
    return checkParentDirectory(new File(filename));
  }
  
  public static List<File> findFiles(File rootPath, String pattern, boolean recursive) throws FileNotFoundException {
    Assert.notNull(rootPath);
    validateDirectory(rootPath);
    List<File> result = getFileListing(rootPath, pattern, recursive);
    if (result != null)
      Collections.sort(result); 
    return result;
  }
  
  private static List<File> getFileListing(File rootPath, String pattern, boolean recursive) throws FileNotFoundException {
    List<File> result = new ArrayList<>();
    File[] filesAndDirs = rootPath.listFiles();
    if (filesAndDirs == null)
      return result; 
    byte b;
    int i;
    File[] arrayOfFile1;
    for (i = (arrayOfFile1 = filesAndDirs).length, b = 0; b < i; ) {
      File file = arrayOfFile1[b];
      String name = file.getName();
      if (name.matches(pattern) && file.isFile()) {
        result.add(file);
      } else if (recursive) {
        List<File> deeperList = getFileListing(file, pattern, recursive);
        if (deeperList != null)
          result.addAll(deeperList); 
      } 
      b++;
    } 
    return result;
  }
  
  private static void validateDirectory(File aDirectory) throws FileNotFoundException {
    Assert.notNull(aDirectory, "Directory should not be null.");
    if (!aDirectory.exists())
      throw new FileNotFoundException("Directory does not exist: " + 
          aDirectory); 
    Assert.isTrue(aDirectory.isDirectory(), "Is not a directory: " + aDirectory);
    Assert.isTrue(aDirectory.canRead(), "Directory cannot be read: " + aDirectory);
  }
  
  public static String readFromFile(URL source) throws IOException {
    if (!(new File(source.getFile())).exists())
      throw new FileNotFoundException(); 
    return readFromFile(source.openStream());
  }
  
  public static String readFromFile(InputStream in) throws IOException {
    char[] chars = new char[4092];
    InputStreamReader contentsReader = null;
    StringBuffer buffer = new StringBuffer();
    try {
      int c;
      contentsReader = new InputStreamReader(in);
      do {
        c = contentsReader.read(chars);
        if (c == -1)
          break; 
        buffer.append(chars, 0, c);
      } while (c != -1);
    } finally {
      IOUtil.tryClose(new Object[] { contentsReader });
    } 
    return buffer.toString();
  }
  
  public static String appendPath(String originalPath, String appendPath) {
    if (OSChecker.isWindows())
      return appendPath(originalPath, appendPath, true); 
    return appendPath(originalPath, appendPath, false);
  }
  
  public static String appendPath(String originalPath, String appendPath, boolean isWindows) {
    char separatorChar = isWindows ? '\\' : '/';
    char targetChar = isWindows ? '/' : '\\';
    originalPath = originalPath.replace(targetChar, separatorChar);
    appendPath = appendPath.replace(targetChar, separatorChar);
    return trimLastPath(originalPath, separatorChar).concat(trimFirstPath(appendPath, separatorChar));
  }
  
  public static String convertToOSPath(String input, boolean isWindows) {
    return appendPath(input, "", isWindows);
  }
  
  public static String convertToOSPath(String input) {
    if (OSChecker.isWindows())
      return convertToOSPath(input, true); 
    return convertToOSPath(input, false);
  }
  
  private static String trimLastPath(String originalPath, char targetChar) {
    if (StringUtil.isEmpty(originalPath))
      return ""; 
    char lastChar = originalPath.charAt(originalPath.length() - 1);
    if (lastChar == targetChar)
      return originalPath.substring(0, originalPath.length() - 1); 
    return originalPath;
  }
  
  private static String trimFirstPath(String originalPath, char targetChar) {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic isEmpty : (Ljava/lang/CharSequence;)Z
    //   4: ifeq -> 10
    //   7: ldc ''
    //   9: areturn
    //   10: aload_0
    //   11: iconst_0
    //   12: invokevirtual charAt : (I)C
    //   15: istore_2
    //   16: iload_2
    //   17: iload_1
    //   18: if_icmpeq -> 40
    //   21: new java/lang/StringBuilder
    //   24: dup
    //   25: invokespecial <init> : ()V
    //   28: iload_1
    //   29: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   32: invokevirtual toString : ()Ljava/lang/String;
    //   35: aload_0
    //   36: invokevirtual concat : (Ljava/lang/String;)Ljava/lang/String;
    //   39: areturn
    //   40: aload_0
    //   41: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #731	-> 0
    //   #732	-> 7
    //   #735	-> 10
    //   #736	-> 16
    //   #737	-> 21
    //   #739	-> 40
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	42	0	originalPath	Ljava/lang/String;
    //   0	42	1	targetChar	C
    //   16	26	2	firstChar	C
  }
  
  public static boolean isExist(String path) {
    if (StringUtil.isEmpty(path))
      return false; 
    File file = new File(path);
    return file.exists();
  }
  
  public static long getFolderSize(File directory) {
    long length = 0L;
    if (!directory.exists())
      return length; 
    File[] files = directory.listFiles();
    if (files != null) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = files).length, b = 0; b < i; ) {
        File file = arrayOfFile[b];
        if (file.isFile()) {
          length += file.length();
        } else {
          length += getFolderSize(file);
        } 
        b++;
      } 
    } 
    return length;
  }
}
