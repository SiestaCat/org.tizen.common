package org.tizen.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.URIUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.Surrogate;

public class PluginUtil {
  protected static final Logger logger = LoggerFactory.getLogger(PluginUtil.class);
  
  protected static Surrogate<IExtensionRegistry> platformSurrogate = new Surrogate<IExtensionRegistry>() {
      public IExtensionRegistry getAdapter() {
        return Platform.getExtensionRegistry();
      }
    };
  
  public static Object[] loadClasses(String extPointId) throws CoreException {
    IExtensionRegistry registry = platformSurrogate.getAdapter();
    IExtensionPoint ep = registry.getExtensionPoint(extPointId);
    IExtension[] exts = ep.getExtensions();
    List<Object> classList = new ArrayList();
    byte b;
    int i;
    IExtension[] arrayOfIExtension1;
    for (i = (arrayOfIExtension1 = exts).length, b = 0; b < i; ) {
      IExtension ext = arrayOfIExtension1[b];
      IConfigurationElement[] configs = ext.getConfigurationElements();
      byte b1;
      int j;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (j = (arrayOfIConfigurationElement1 = configs).length, b1 = 0; b1 < j; ) {
        IConfigurationElement config = arrayOfIConfigurationElement1[b1];
        Object cls = config.createExecutableExtension("class");
        classList.add(cls);
        b1++;
      } 
      b++;
    } 
    return classList.toArray();
  }
  
  public static Object loadClass(String extPointId, String classId) throws CoreException {
    IExtensionRegistry registry = platformSurrogate.getAdapter();
    IExtensionPoint ep = registry.getExtensionPoint(extPointId);
    IExtension[] exts = ep.getExtensions();
    byte b;
    int i;
    IExtension[] arrayOfIExtension1;
    for (i = (arrayOfIExtension1 = exts).length, b = 0; b < i; ) {
      IExtension ext = arrayOfIExtension1[b];
      IConfigurationElement[] configs = ext.getConfigurationElements();
      byte b1;
      int j;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (j = (arrayOfIConfigurationElement1 = configs).length, b1 = 0; b1 < j; ) {
        IConfigurationElement config = arrayOfIConfigurationElement1[b1];
        String id = config.getAttribute("id");
        if (id != null && id.equals(classId))
          return config.createExecutableExtension("class"); 
        b1++;
      } 
      b++;
    } 
    return null;
  }
  
  public static Object[] getExtensionAttribute(String extPointId, String attribute) throws CoreException {
    IExtensionRegistry registry = platformSurrogate.getAdapter();
    IExtensionPoint ep = registry.getExtensionPoint(extPointId);
    IExtension[] exts = ep.getExtensions();
    List<String> values = new ArrayList<>();
    byte b;
    int i;
    IExtension[] arrayOfIExtension1;
    for (i = (arrayOfIExtension1 = exts).length, b = 0; b < i; ) {
      IExtension ext = arrayOfIExtension1[b];
      IConfigurationElement[] configs = ext.getConfigurationElements();
      byte b1;
      int j;
      IConfigurationElement[] arrayOfIConfigurationElement1;
      for (j = (arrayOfIConfigurationElement1 = configs).length, b1 = 0; b1 < j; ) {
        IConfigurationElement config = arrayOfIConfigurationElement1[b1];
        String value = config.getAttribute(attribute);
        if (value != null && !value.isEmpty())
          values.add(value); 
        b1++;
      } 
      b++;
    } 
    return values.toArray();
  }
  
  public static String getBuiltInPath(String symbolicName, String entryPath) throws Exception {
    URI sourceURI = null;
    URL sourceURL = FileLocator.toFileURL(Platform.getBundle(symbolicName).getEntry(entryPath));
    sourceURI = URIUtil.toURI(sourceURL);
    return (sourceURI == null) ? null : sourceURI.getPath();
  }
  
  public static URL getBuiltInURL(Plugin plugin, String entryPath) throws IOException {
    Assert.notNull(plugin);
    Assert.notNull(entryPath);
    URL entry = plugin.getBundle().getEntry(entryPath);
    return (entry != null) ? FileLocator.toFileURL(entry) : null;
  }
  
  public static IConfigurationElement[] getExtensionConfigurationElements(String extPointId) {
    IExtensionRegistry registry = platformSurrogate.getAdapter();
    return registry.getConfigurationElementsFor(extPointId);
  }
  
  public static Bundle getBundle(String symbolicName) {
    return Platform.getBundle(symbolicName);
  }
  
  public static URL getFileURLinBundle(String symbolicName, String filePath) {
    Bundle bundle = getBundle(symbolicName);
    if (bundle == null)
      return null; 
    return getFileURLinBundle(bundle, filePath);
  }
  
  public static URL getFileURLinBundle(Bundle bundle, String filePath) {
    if (bundle == null || filePath == null)
      return null; 
    URL url = bundle.getResource(filePath);
    if (url == null)
      return null; 
    URL resolvedURL = null;
    try {
      int separatorIndex = filePath.lastIndexOf("/");
      if (separatorIndex > 0) {
        String rootFilePath = filePath.substring(0, separatorIndex);
        URL rootUrl = bundle.getResource(rootFilePath);
        FileLocator.toFileURL(rootUrl);
      } 
      resolvedURL = FileLocator.toFileURL(url);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } 
    return resolvedURL;
  }
}
