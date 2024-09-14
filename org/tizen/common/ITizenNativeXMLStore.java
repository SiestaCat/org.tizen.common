package org.tizen.common;

import java.io.File;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IProject;

public interface ITizenNativeXMLStore {
  public static final String EXSD_STORE_EXTENSION_POINT_ID = "org.tizen.nativecommon.store";
  
  public static final String EXSD_APP_XML_STORE_CLASS_ID = "org.tizen.nativecpp.misc.core.NewAppXmlStore";
  
  void setProject(String paramString);
  
  void setProject(IProject paramIProject);
  
  boolean loadXml();
  
  boolean loadXml(File paramFile);
  
  void mergeAppData(ITizenNativeXMLStore paramITizenNativeXMLStore);
  
  void mergePrivData(ITizenNativeXMLStore paramITizenNativeXMLStore);
  
  boolean storeXml(File paramFile);
  
  Collection<String> getFeatures();
  
  Collection<String> getPrivileges();
  
  String getUseSystemCertsOfTrustAnchor();
  
  void setId(String paramString);
  
  void setPackage(String paramString);
  
  void setVersion(String paramString);
  
  void mergeManifest(ITizenNativeXMLStore paramITizenNativeXMLStore);
  
  String getPkgId();
  
  List<String> getExecutableNames();
  
  String getProfileName();
  
  void setProfileName(String paramString);
}
