package org.tizen.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

public class ObjectUtil {
  public static <T> T nvl(Object... args) {
    if (args == null)
      return null; 
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = args).length, b = 0; b < i; ) {
      T arg = (T)arrayOfObject[b];
      if (arg != null)
        return arg; 
      b++;
    } 
    return null;
  }
  
  private static String hexServerIP = null;
  
  private static final SecureRandom SEEDER = new SecureRandom();
  
  public static boolean equals(Object obj1, Object obj2) {
    if (obj1 == obj2)
      return true; 
    if (obj1 == null || obj2 == null)
      return false; 
    if (!obj1.getClass().isArray() || !obj2.getClass().isArray())
      return obj1.equals(obj2); 
    if (obj1 instanceof boolean[] && obj2 instanceof boolean[])
      return Arrays.equals((boolean[])obj1, (boolean[])obj2); 
    if (obj1 instanceof byte[] && obj2 instanceof byte[])
      return Arrays.equals((byte[])obj1, (byte[])obj2); 
    if (obj1 instanceof char[] && obj2 instanceof char[])
      return Arrays.equals((char[])obj1, (char[])obj2); 
    if (obj1 instanceof double[] && obj2 instanceof double[])
      return Arrays.equals((double[])obj1, (double[])obj2); 
    if (obj1 instanceof float[] && obj2 instanceof float[])
      return Arrays.equals((float[])obj1, (float[])obj2); 
    if (obj1 instanceof int[] && obj2 instanceof int[])
      return Arrays.equals((int[])obj1, (int[])obj2); 
    if (obj1 instanceof long[] && obj2 instanceof long[])
      return Arrays.equals((long[])obj1, (long[])obj2); 
    if (obj1 instanceof short[] && obj2 instanceof short[])
      return Arrays.equals((short[])obj1, (short[])obj2); 
    if (obj1 instanceof Collection && obj2 instanceof Collection)
      return CollectionUtil.equals((Collection<?>[])new Collection[] { (Collection)obj1, (Collection)obj2 }); 
    if (obj1 instanceof Object[] && obj2 instanceof Object[])
      return CollectionUtil.equals(new Object[][] { (Object[])obj1, (Object[])obj2 }); 
    return false;
  }
  
  public static int getInt(byte[] bytes) {
    int i = 0;
    int j = 24;
    for (int k = 0; j >= 0; k++) {
      int l = bytes[k] & 0xFF;
      i += l << j;
      j -= 8;
    } 
    return i;
  }
  
  private static String padHex(String str, int length) {
    StringBuilder buffer = new StringBuilder();
    for (int j = 0, n = length - str.length(); j < n; j++)
      buffer.append('0'); 
    buffer.append(str.subSequence(str.length() - Math.min(str.length(), length), str.length()));
    return buffer.toString();
  }
  
  public static String hexFormat(int i, int j) {
    String s = Integer.toHexString(i);
    return padHex(s, j);
  }
  
  public static final String generateGUID(Object obj) {
    StringBuilder guid = new StringBuilder(32);
    long timeNow = System.currentTimeMillis();
    int timeLow = (int)timeNow & 0xFFFFFFFF;
    guid.append(hexFormat(timeLow, 8));
    if (hexServerIP == null) {
      InetAddress localInetAddress = null;
      try {
        localInetAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
        try {
          localInetAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
          e.printStackTrace();
          return null;
        } 
      } 
      byte[] serverIP = localInetAddress.getAddress();
      hexServerIP = hexFormat(getInt(serverIP), 8);
    } 
    guid.append(hexServerIP);
    guid.append(hexFormat(System.identityHashCode(obj), 8));
    int node = -1;
    synchronized (SEEDER) {
      node = SEEDER.nextInt();
    } 
    guid.append(hexFormat(node, 8));
    return guid.toString();
  }
  
  public static byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
    objOut.writeObject(obj);
    return byteOut.toByteArray();
  }
  
  public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    Assert.notNull(bytes, "bytes must not be null");
    ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
    ObjectInputStream objIn = new ObjectInputStream(byteIn);
    return objIn.readObject();
  }
  
  public static String toString(Object obj) {
    if (obj == null)
      return "<<null>>"; 
    StringBuilder builder = new StringBuilder();
    builder.append(obj.getClass().getSimpleName());
    builder.append('@');
    int hash = obj.hashCode();
    for (int i = 8; i >= 0; i -= 8)
      StringUtil.appendHexa(builder, 0xFF & hash >> i); 
    return builder.toString();
  }
}
