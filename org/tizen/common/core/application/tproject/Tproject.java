package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "tproject")
public class Tproject {
  @XmlElement(required = true)
  protected Platforms platforms;
  
  @XmlElement(name = "package", required = true)
  protected Package _package;
  
  protected CanUsePrebuiltIndex canUsePrebuiltIndex;
  
  public Platforms getPlatforms() {
    return this.platforms;
  }
  
  public void setPlatforms(Platforms value) {
    this.platforms = value;
  }
  
  public Package getPackage() {
    return this._package;
  }
  
  public void setPackage(Package value) {
    this._package = value;
  }
  
  public CanUsePrebuiltIndex getCanUsePrebuiltIndex() {
    return this.canUsePrebuiltIndex;
  }
  
  public void setCanUsePrebuiltIndex(CanUsePrebuiltIndex value) {
    this.canUsePrebuiltIndex = value;
  }
}
