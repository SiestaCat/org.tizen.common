package org.tizen.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.Assert;
import org.tizen.common.util.ParsingUtil;

public class Preference {
  protected static final Logger logger = LoggerFactory.getLogger(Preference.class);
  
  protected static final ArrayList<PreferenceProvider> providers = new ArrayList<>();
  
  protected static final HashMap<String, PreferenceProvider> name2provider = new HashMap<>();
  
  public static void clear() {
    providers.clear();
    name2provider.clear();
  }
  
  protected static final boolean checkName(String name) {
    char[] chars = name.toCharArray();
    for (int i = 0, n = chars.length; i < n; i++) {
      if (!Character.isJavaIdentifierPart(chars[i]))
        if ('.' != chars[i])
          return false;  
    } 
    return true;
  }
  
  public static void register(String name, PreferenceProvider provider) {
    if (name != null && !checkName(name))
      return; 
    PreferenceProvider old = name2provider.put(name, provider);
    if (old != null) {
      providers.set(providers.indexOf(old), provider);
    } else {
      providers.add(0, provider);
    } 
  }
  
  public static String get(String key) {
    logger.info("Key :{}", key);
    for (PreferenceProvider provider : providers) {
      String exp = provider.get(key);
      logger.debug("Provider :{}, Value :{}", provider, exp);
      if (exp == null)
        continue; 
      return exp;
    } 
    return null;
  }
  
  public static String get(String namespace, String key) {
    Assert.notNull(namespace);
    logger.info("Namespace :{}, Key :{}", namespace, key);
    PreferenceProvider provider = name2provider.get(namespace);
    if (provider != null) {
      String exp = provider.get(key);
      logger.debug("Provider :{}, Value :{}", provider, exp);
      return exp;
    } 
    return null;
  }
  
  public static Collection<String> list(String name) {
    Set<String> keys = new HashSet<>();
    return addAllKeys(name, keys);
  }
  
  public static Collection<String> sortedList(String name) {
    Set<String> keys = new TreeSet<>();
    return addAllKeys(name, keys);
  }
  
  private static Collection<String> addAllKeys(String name, Set<String> keys) {
    PreferenceProvider preferenceProvider = name2provider.get(name);
    if (preferenceProvider != null)
      keys.addAll(preferenceProvider.keys()); 
    return Collections.unmodifiableCollection(keys);
  }
  
  public static String getValue(String key, String defaultValue) {
    String exp = get(key);
    if (exp == null) {
      if (defaultValue == null)
        return null; 
      return ExpressionParser.parse(defaultValue);
    } 
    return ExpressionParser.parse(exp);
  }
  
  public static int getInteger(String key, int defaultValue) {
    String value = getValue(key, null);
    return ParsingUtil.parseInt(value, defaultValue);
  }
  
  public static boolean getBoolean(String key, boolean defaultValue) {
    String value = getValue(key, null);
    return ParsingUtil.parseBoolean(value, defaultValue);
  }
}
