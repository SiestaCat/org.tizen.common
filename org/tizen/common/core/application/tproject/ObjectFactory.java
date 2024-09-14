package org.tizen.common.core.application.tproject;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
  public Regex createRegex() {
    return new Regex();
  }
  
  public Tproject createTproject() {
    return new Tproject();
  }
  
  public Platforms createPlatforms() {
    return new Platforms();
  }
  
  public Package createPackage() {
    return new Package();
  }
  
  public CanUsePrebuiltIndex createCanUsePrebuiltIndex() {
    return new CanUsePrebuiltIndex();
  }
  
  public Exclude createExclude() {
    return new Exclude();
  }
  
  public ResFallback createResFallback() {
    return new ResFallback();
  }
  
  public Name createName() {
    return new Name();
  }
  
  public SubProjects createSubProjects() {
    return new SubProjects();
  }
  
  public Path createPath() {
    return new Path();
  }
  
  public Blacklist createBlacklist() {
    return new Blacklist();
  }
  
  public Platform createPlatform() {
    return new Platform();
  }
  
  public TizenProject createTizenProject() {
    return new TizenProject();
  }
  
  public ReferencedProject createReferencedProject() {
    return new ReferencedProject();
  }
  
  public Library createLibrary() {
    return new Library();
  }
}
