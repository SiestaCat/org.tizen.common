package org.tizen.common.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class UserField {
  protected final String id;
  
  protected final String message;
  
  protected final Class<?> type;
  
  protected boolean bModify = true;
  
  protected Object value;
  
  protected InputValidator validator;
  
  protected Collection<UserField> children;
  
  protected Collection<Object> supports;
  
  public UserField(String id, String message, Class<?> type) {
    this.id = id;
    this.message = message;
    this.type = type;
  }
  
  public String getId() {
    return this.id;
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public Class<?> getType() {
    return this.type;
  }
  
  public boolean canModify() {
    return this.bModify;
  }
  
  public void setModify(boolean bModify) {
    this.bModify = bModify;
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public void setValue(Object value) {
    this.value = value;
  }
  
  public InputValidator getValidator() {
    return this.validator;
  }
  
  public void setValidator(InputValidator validator) {
    this.validator = validator;
  }
  
  public Collection<UserField> getChildren() {
    return this.children;
  }
  
  public void addChild(UserField... children) {
    if (this.children == null)
      this.children = new ArrayList<>(); 
    this.children.addAll(Arrays.asList(children));
  }
  
  public Collection<Object> getSupports() {
    return this.supports;
  }
  
  public void addSupport(Object support) {
    if (this.supports == null)
      this.supports = new ArrayList(); 
    this.supports.add(support);
  }
}
