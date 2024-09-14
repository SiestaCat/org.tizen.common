package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "canUsePrebuiltIndex")
public class CanUsePrebuiltIndex {
  @XmlAttribute(required = true)
  protected boolean value;
  
  public boolean isValue() {
    return this.value;
  }
  
  public void setValue(boolean value) {
    this.value = value;
  }
}
