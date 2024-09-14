package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "name", propOrder = {"exclude"})
public class Name {
  protected List<Exclude> exclude;
  
  @XmlAttribute(required = true)
  protected String value;
  
  @XmlAttribute
  protected String type;
  
  public List<Exclude> getExclude() {
    if (this.exclude == null)
      this.exclude = new ArrayList<>(); 
    return this.exclude;
  }
  
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
