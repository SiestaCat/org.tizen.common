package org.tizen.common.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CipherUtil {
  protected static final Logger logger = LoggerFactory.getLogger(CipherUtil.class);
  
  private static final String password = "KYANINYLhijklmnopqrstuvwx";
  
  private static SecretKey SECRETE_KEY;
  
  private static Cipher DES_CIPHER;
  
  private static final String ALGORITHM = "DESede";
  
  static {
    try {
      byte[] key = "KYANINYLhijklmnopqrstuvwx".getBytes();
      DESedeKeySpec desKeySpec = new DESedeKeySpec(key);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
      SECRETE_KEY = keyFactory.generateSecret(desKeySpec);
      DES_CIPHER = Cipher.getInstance("DESede/ECB/PKCS5Padding");
    } catch (Throwable t) {
      logger.error("Exception occurred while creating secret key", t);
    } 
  }
  
  private static byte[] decryptByDES(byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    DES_CIPHER.init(2, SECRETE_KEY);
    return DES_CIPHER.doFinal(bytes);
  }
  
  private static byte[] encryptByDES(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    DES_CIPHER.init(1, SECRETE_KEY);
    return DES_CIPHER.doFinal(s.getBytes());
  }
  
  public static String getDecryptedString(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    byte[] decodeBytes = Base64.decode(s);
    if (decodeBytes == null)
      return s; 
    return new String(decryptByDES(decodeBytes));
  }
  
  public static String getEncryptedString(String s) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    String encoded = Base64.encode(encryptByDES(s));
    if (encoded == null)
      return s; 
    return encoded;
  }
}
