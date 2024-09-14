package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exclude")
public class Exclude {
  @XmlAttribute(required = true)
  protected String value;
  
  @XmlAttribute
  protected String type;
  
  public String getValue() {
    return this.value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public String getType() {
    if (this.type == null)
      return "all"; 
    return this.type;
  }
  
  public void setType(String value) {
    this.type = value;
  }
}
