package org.tizen.common;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppIdGenerator implements Factory<String> {
  final Logger logger = LoggerFactory.getLogger(AppIdGenerator.class);
  
  protected static AppIdGenerator instance = new AppIdGenerator();
  
  public static AppIdGenerator getInstance() {
    return instance;
  }
  
  public String create() {
    try {
      return generate();
    } catch (Exception e) {
      this.logger.error("Exception occurred:", e);
      return "";
    } 
  }
  
  final char[] PSEUDO_CHARS = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 
      'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 
      'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 
      'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z' };
  
  final BigInteger PSEUDO_CHAR_SIZE = new BigInteger("62");
  
  static final String[] RESERVED_APPID = new String[] { "TIZEN", 
      "PRIVT", 
      "WIDGT" };
  
  private String convertBytesToAlphaNumeric(byte[] in, int size) {
    BigInteger randomBigInt = (new BigInteger(in)).abs();
    StringBuffer out = new StringBuffer(size + 1);
    for (int i = 0; i < size; i++) {
      BigInteger[] dividedAndremainder = randomBigInt.divideAndRemainder(this.PSEUDO_CHAR_SIZE);
      randomBigInt = dividedAndremainder[0];
      out.append(this.PSEUDO_CHARS[dividedAndremainder[1].intValue()]);
    } 
    String ret = new String(out);
    return ret;
  }
  
  private byte[] generateRandomBytes(int size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    SecureRandom random = new SecureRandom();
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128);
    byte[] bytes = new byte[size];
    random.nextBytes(bytes);
    SecretKey secretkey = keyGen.generateKey();
    byte[] key = secretkey.getEncoded();
    SecretKeySpec secretkeySpec = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(1, secretkeySpec);
    return cipher.doFinal(bytes);
  }
  
  private boolean check(String appId) {
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = RESERVED_APPID).length, b = 0; b < i; ) {
      String prefix = arrayOfString[b];
      if (appId.startsWith(prefix))
        return false; 
      b++;
    } 
    return true;
  }
  
  public String generate(int size) throws Exception {
    String appId = "";
    do {
      appId = convertBytesToAlphaNumeric(generateRandomBytes(size), size);
    } while (!check(appId));
    return appId;
  }
  
  public String generate() throws Exception {
    return generate(10);
  }
}
