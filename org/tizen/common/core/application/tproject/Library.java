package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "library")
public class Library {
  @XmlAttribute(required = true)
  protected String project;
  
  @XmlAttribute(required = true)
  protected String path;
  
  public String getProject() {
    return this.project;
  }
  
  public void setProject(String value) {
    this.project = value;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public void setPath(String value) {
    this.path = value;
  }
}
