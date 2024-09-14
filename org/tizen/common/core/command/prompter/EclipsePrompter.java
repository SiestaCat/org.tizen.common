package org.tizen.common.core.command.prompter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.Prompter;
import org.tizen.common.core.command.UserField;
import org.tizen.common.util.ObjectUtil;
import org.tizen.common.util.StringUtil;

public class EclipsePrompter implements Prompter {
  protected static final String EP_ID = "org.tizen.common.prompter";
  
  protected static final String ATTR_SCOPE = "scope";
  
  protected static final String ATTR_CLASS = "class";
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final Prompter defaultPrompter;
  
  protected final HashMap<String, Prompter> name2prompter;
  
  public EclipsePrompter(Prompter defaultPrompter) {
    this.defaultPrompter = defaultPrompter;
    this.name2prompter = new HashMap<>();
  }
  
  protected Prompter getPrompter() {
    StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint("org.tizen.common.prompter");
    IExtension[] exts = ep.getExtensions();
    byte b;
    int i;
    StackTraceElement[] arrayOfStackTraceElement1;
    for (i = (arrayOfStackTraceElement1 = stacks).length, b = 0; b < i; ) {
      StackTraceElement stack = arrayOfStackTraceElement1[b];
      String className = stack.getClassName();
      byte b1;
      int j;
      IExtension[] arrayOfIExtension;
      for (j = (arrayOfIExtension = exts).length, b1 = 0; b1 < j; ) {
        IExtension ext = arrayOfIExtension[b1];
        IConfigurationElement[] configs = ext.getConfigurationElements();
        byte b2;
        int k;
        IConfigurationElement[] arrayOfIConfigurationElement1;
        for (k = (arrayOfIConfigurationElement1 = configs).length, b2 = 0; b2 < k; ) {
          IConfigurationElement config = arrayOfIConfigurationElement1[b2];
          String scope = config.getAttribute("scope");
          if (!StringUtil.isEmpty(scope))
            if (ObjectUtil.equals(className, scope))
              try {
                return (Prompter)config.createExecutableExtension("class");
              } catch (CoreException coreException) {
                this.logger.error("Can't create prompter :" + config.getAttribute("class"));
                return this.defaultPrompter;
              }   
          b2++;
        } 
        b1++;
      } 
      b++;
    } 
    return this.defaultPrompter;
  }
  
  public Option interact(String question, Option... options) {
    Prompter prompter = getPrompter();
    return prompter.interact(question, options);
  }
  
  public Object password(String message) {
    Prompter prompter = getPrompter();
    return prompter.password(message);
  }
  
  public void error(String message) {
    Prompter prompter = getPrompter();
    prompter.error(message);
  }
  
  public void notify(String message) {
    Prompter prompter = getPrompter();
    prompter.notify(message);
  }
  
  public void cancel() {
    Prompter prompter = getPrompter();
    prompter.cancel();
  }
  
  public String toString() {
    return String.valueOf(getClass().getSimpleName()) + "@" + Integer.toHexString(hashCode()).substring(0, 4);
  }
  
  public void batch(Collection<UserField> userFields, Map<String, Object> options) {
    Prompter prompter = getPrompter();
    prompter.batch(userFields, options);
  }
}
