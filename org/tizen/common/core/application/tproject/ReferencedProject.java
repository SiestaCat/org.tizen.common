package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referencedProject", propOrder = {"library"})
public class ReferencedProject {
  protected List<Library> library;
  
  public List<Library> getLibrary() {
    if (this.library == null)
      this.library = new ArrayList<>(); 
    return this.library;
  }
}
