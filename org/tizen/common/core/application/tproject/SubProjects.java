package org.tizen.common.core.application.tproject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subProjects", propOrder = {"tizenProject"})
public class SubProjects {
  protected List<TizenProject> tizenProject;
  
  public List<TizenProject> getTizenProject() {
    if (this.tizenProject == null)
      this.tizenProject = new ArrayList<>(); 
    return this.tizenProject;
  }
}
