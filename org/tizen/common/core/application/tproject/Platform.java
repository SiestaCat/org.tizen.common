package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "platform", propOrder = {"name"})
public class Platform {
  @XmlElement(required = true)
  protected String name;
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String value) {
    this.name = value;
  }
}
