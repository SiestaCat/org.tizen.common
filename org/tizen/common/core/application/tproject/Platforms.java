package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "platforms", propOrder = {"platform"})
public class Platforms {
  @XmlElement(required = true)
  protected List<Platform> platform;
  
  public List<Platform> getPlatform() {
    if (this.platform == null)
      this.platform = new ArrayList<>(); 
    return this.platform;
  }
}
