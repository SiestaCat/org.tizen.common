package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "package", propOrder = {"blacklist", "referencedProject", "resFallback", "subProjects"})
public class Package {
  @XmlElement(required = true)
  protected Blacklist blacklist;
  
  protected List<Library> referencedProject;
  
  protected ResFallback resFallback;
  
  protected SubProjects subProjects;
  
  public Blacklist getBlacklist() {
    return this.blacklist;
  }
  
  public void setBlacklist(Blacklist value) {
    this.blacklist = value;
  }
  
  public List<Library> getReferencedProject() {
    if (this.referencedProject == null)
      this.referencedProject = new ArrayList<>(); 
    return this.referencedProject;
  }
  
  public ResFallback getResFallback() {
    return this.resFallback;
  }
  
  public void setResFallback(ResFallback value) {
    this.resFallback = value;
  }
  
  public SubProjects getSubProjects() {
    return this.subProjects;
  }
  
  public void setSubProjects(SubProjects value) {
    this.subProjects = value;
  }
}
