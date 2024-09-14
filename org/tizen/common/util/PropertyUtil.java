package org.tizen.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PropertyUtil {
  protected static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
  
  public static Properties loadProperties(String fileName) {
    Properties props = new Properties();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(fileName));
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (StringUtil.isEmpty(line))
          continue; 
        int index = line.indexOf("=");
        String key = line.substring(0, index);
        String value = line.substring(index + 1);
        props.setProperty(key, value);
      } 
    } catch (IOException e) {
      logger.error(e.getMessage());
    } finally {
      IOUtil.tryClose(new Object[] { reader });
    } 
    return props;
  }
  
  public static Properties loadProperties(InputStream inputStream) {
    Properties props = new Properties();
    try {
      props.load(inputStream);
    } catch (IOException iOException) {
      logger.info("Ignore exception");
    } 
    return props;
  }
  
  public static boolean storeProperties(OutputStream outputStream, Properties props) {
    try {
      props.store(outputStream, (String)null);
      return true;
    } catch (IOException iOException) {
      logger.info("Ignore exception");
      return false;
    } finally {
      IOUtil.tryClose(new Object[] { outputStream });
    } 
  }
  
  public static boolean storeProperties(String fileName, Properties props) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileName));
      props.store(writer, (String)null);
      return true;
    } catch (IOException iOException) {
      logger.info("Ignore exception");
      return false;
    } finally {
      IOUtil.tryClose(new Object[] { writer });
    } 
  }
}
