package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tizenProject")
public class TizenProject {
  @XmlAttribute(required = true)
  protected String project;
  
  @XmlAttribute
  protected String style;
  
  public String getProject() {
    return this.project;
  }
  
  public void setProject(String value) {
    this.project = value;
  }
  
  public String getStyle() {
    return this.style;
  }
  
  public void setStyle(String value) {
    this.style = value;
  }
}
