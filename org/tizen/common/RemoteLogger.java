package org.tizen.common;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.application.InstallPathConfig;

public class RemoteLogger {
  private static final Logger logger = LoggerFactory.getLogger(RemoteLogger.class);
  
  private static final String DEFAULT_VERSION = "Default";
  
  private static final String LOGGING_URL = "https://1lxb5yo2lb.execute-api.ap-northeast-2.amazonaws.com/v1";
  
  private static AnalyticConfig parseConfig(Path filePath) {
    File f = new File(filePath.toString());
    AnalyticConfig config = new AnalyticConfig("", Boolean.valueOf(false));
    if (f.exists()) {
      Gson gson = new Gson();
      try {
        Exception exception2, exception1 = null;
      } catch (IOException iOException) {
        return config;
      } 
    } 
    return config;
  }
  
  public static Boolean isLoggingEnabled() {
    return (getAnalyticsConf()).logging;
  }
  
  public static void writeLoggingInfoToFile(Boolean log) {
    writeAnalyticsConf((getAnalyticsConf()).id, log);
  }
  
  private static AnalyticConfig writeAnalyticsConf(String uuid, Boolean log) {
    Path filePath = Paths.get(InstallPathConfig.getAnalyticsConfig(), new String[0]);
    File f = new File(filePath.toString());
    f.delete();
    AnalyticConfig config = new AnalyticConfig(uuid, log);
    Gson gson = new Gson();
    try {
      Exception exception2, exception1 = null;
    } catch (JsonIOException|IOException e) {
      e.printStackTrace();
      return config;
    } 
    return config;
  }
  
  private static AnalyticConfig getAnalyticsConf() {
    Path filePath = Paths.get(InstallPathConfig.getAnalyticsConfig(), new String[0]);
    File f = new File(filePath.toString());
    AnalyticConfig config = parseConfig(filePath);
    if (config.id.isEmpty()) {
      f.delete();
      String id = UUID.randomUUID().toString().replaceAll("-", "");
      return writeAnalyticsConf(id, Boolean.valueOf(true));
    } 
    return config;
  }
  
  private static void postUsage(UsageLog log) throws IOException {
    URL url = new URL("https://1lxb5yo2lb.execute-api.ap-northeast-2.amazonaws.com/v1/postusage");
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setConnectTimeout(5000);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    Gson gson = new Gson();
    String json = gson.toJson(log);
    OutputStream os = conn.getOutputStream();
    os.write(json.getBytes("UTF-8"));
    os.close();
    InputStream in = new BufferedInputStream(conn.getInputStream());
    String result = IOUtils.toString(in, "UTF-8");
    logger.debug(result);
  }
  
  private static void postAccess(AccessLog log) throws IOException {
    URL url = new URL("https://1lxb5yo2lb.execute-api.ap-northeast-2.amazonaws.com/v1/postaccess");
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setConnectTimeout(5000);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    Gson gson = new Gson();
    String json = gson.toJson(log);
    OutputStream os = conn.getOutputStream();
    os.write(json.getBytes("UTF-8"));
    os.close();
    InputStream in = new BufferedInputStream(conn.getInputStream());
    String result = IOUtils.toString(in, "UTF-8");
    logger.debug(result);
  }
  
  private static void postProfile(ProfileLog log) throws IOException {
    URL url = new URL("https://1lxb5yo2lb.execute-api.ap-northeast-2.amazonaws.com/v1/postprofile");
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setConnectTimeout(5000);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    Gson gson = new Gson();
    String json = gson.toJson(log);
    OutputStream os = conn.getOutputStream();
    os.write(json.getBytes("UTF-8"));
    os.close();
    InputStream in = new BufferedInputStream(conn.getInputStream());
    String result = IOUtils.toString(in, "UTF-8");
    logger.debug(result);
  }
  
  public static void logAccess(String product) {
    AnalyticConfig conf = getAnalyticsConf();
    String uuid = conf.id;
    if (uuid.isEmpty()) {
      logger.info("Failed to generate UUID");
      return;
    } 
    if (!conf.logging.booleanValue()) {
      logger.info("Logging is disabled by user");
      return;
    } 
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String timestamp = fmt.format(now);
    AccessLog log = new AccessLog();
    log.id = uuid;
    log.type = "access";
    log.product = product;
    log.timestamp = timestamp;
    try {
      log.version = getVersion();
    } catch (IOException iOException) {
      logger.info("Failed to get version");
      log.version = "Default";
    } 
    try {
      postAccess(log);
    } catch (IOException iOException) {
      logger.info("Post Request failed");
    } 
  }
  
  public static void deleteAnalytics() {
    try {
      URL url = new URL("https://1lxb5yo2lb.execute-api.ap-northeast-2.amazonaws.com/v1/deleteuser");
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setConnectTimeout(5000);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      Gson gson = new Gson();
      AnalyticConfig conf = getAnalyticsConf();
      DeleteUser user = new DeleteUser();
      user.id = conf.id;
      String json = gson.toJson(user);
      OutputStream os = conn.getOutputStream();
      os.write(json.getBytes("UTF-8"));
      os.close();
      InputStream in = new BufferedInputStream(conn.getInputStream());
      String result = IOUtils.toString(in, "UTF-8");
      logger.debug(result);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void logUsage(String product, long usage) {
    AnalyticConfig conf = getAnalyticsConf();
    String uuid = conf.id;
    if (uuid.isEmpty()) {
      logger.info("Failed to generate UUID");
      return;
    } 
    if (!conf.logging.booleanValue()) {
      logger.info("Logging is disabled by user");
      return;
    } 
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String timestamp = fmt.format(now);
    UsageLog log = new UsageLog();
    log.id = uuid;
    log.type = "usage";
    log.product = product;
    log.timestamp = timestamp;
    log.usage = usage;
    try {
      log.version = getVersion();
    } catch (IOException iOException) {
      logger.info("Failed to get version");
      log.version = "Default";
    } 
    try {
      postUsage(log);
    } catch (IOException iOException) {
      logger.info("Post Request failed");
    } 
  }
  
  public static void logProfile(String hash, String product, String profile, String version, String os, String type) {
    AnalyticConfig conf = getAnalyticsConf();
    String uuid = conf.id;
    if (uuid.isEmpty()) {
      logger.info("Failed to generate UUID");
      return;
    } 
    if (!conf.logging.booleanValue()) {
      logger.info("Logging is disabled by user");
      return;
    } 
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    String timestamp = fmt.format(now);
    ProfileLog log = new ProfileLog();
    log.id = uuid;
    log.type = type;
    log.product = product;
    log.hash = hash;
    log.profile = profile;
    log.pver = version;
    log.os = os;
    log.timestamp = timestamp;
    try {
      log.version = getVersion();
    } catch (IOException iOException) {
      logger.info("Failed to get version");
      log.version = "Default";
    } 
    try {
      postProfile(log);
    } catch (IOException iOException) {
      logger.info("Post Request failed");
    } 
  }
  
  private static String getVersion() throws IOException {
    Path filePath = Paths.get(InstallPathConfig.getSDKPath(), new String[] { "sdk.version" });
    byte[] encoded = Files.readAllBytes(Paths.get(filePath.toString(), new String[0]));
    String content = new String(encoded, StandardCharsets.UTF_8);
    String[] data = content.split("=");
    if (data.length < 2)
      return "Default"; 
    data[1] = data[1].replaceAll("[\\n\\t ]", "");
    return data[1];
  }
}
