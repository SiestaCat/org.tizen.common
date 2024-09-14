package org.tizen.common.core.command;

import java.util.HashMap;
import org.tizen.common.config.Preference;
import org.tizen.common.core.command.policy.PolicyRegistry;
import org.tizen.common.file.FileHandler;

public class ExecutionContext {
  protected static final ThreadLocal<ExecutionContext> context = new ThreadLocal<>();
  
  protected final PolicyRegistry registry;
  
  protected final Prompter prompter;
  
  protected final FileHandler fileHandler;
  
  public static ExecutionContext getCurrentContext() {
    return context.get();
  }
  
  public static void clear() {
    context.remove();
  }
  
  protected HashMap<String, Object> args = new HashMap<>();
  
  public ExecutionContext(PolicyRegistry registry, Prompter prompter, FileHandler fileHandler) {
    this.registry = registry;
    this.prompter = prompter;
    this.fileHandler = fileHandler;
    context.set(this);
  }
  
  public PolicyRegistry getPolicyRegistry() {
    return this.registry;
  }
  
  public Policy getPolicy(String name) {
    return this.registry.getPolicy(name);
  }
  
  public Prompter getPrompter() {
    return this.prompter;
  }
  
  public FileHandler getFileHandler() {
    return this.fileHandler;
  }
  
  public Object getValue(String key) {
    Object val = this.args.get(key);
    if (val != null)
      return val; 
    return Preference.getValue(key, null);
  }
  
  public void setValue(String key, Object value) {
    this.args.put(key, value);
  }
}
