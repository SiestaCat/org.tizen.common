package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resFallback")
public class ResFallback {
  @XmlAttribute(required = true)
  protected boolean autoGen;
  
  public boolean isAutoGen() {
    return this.autoGen;
  }
  
  public void setAutoGen(boolean value) {
    this.autoGen = value;
  }
}
