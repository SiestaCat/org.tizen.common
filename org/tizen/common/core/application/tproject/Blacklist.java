package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "blacklist", propOrder = {"name", "path", "regex"})
public class Blacklist {
  protected List<Name> name;
  
  protected List<Path> path;
  
  protected List<Regex> regex;
  
  public List<Name> getName() {
    if (this.name == null)
      this.name = new ArrayList<>(); 
    return this.name;
  }
  
  public List<Path> getPath() {
    if (this.path == null)
      this.path = new ArrayList<>(); 
    return this.path;
  }
  
  public List<Regex> getRegex() {
    if (this.regex == null)
      this.regex = new ArrayList<>(); 
    return this.regex;
  }
}
