package org.tizen.common;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.tizen.sdblib.app.IApplicationType;
import org.tizen.sdblib.util.StringUtil;

public enum TizenProjectType implements IApplicationType {
  TIZEN_C_UI_APPLICATION(true, true, true),
  TIZEN_C_COMPONENT_APPLICATION(true, true, true),
  TIZEN_CPP_COMPONENT_APPLICATION(true, true, true),
  TIZEN_C_SERVICE_APPLICATION(false, false, true),
  TIZEN_C_WATCH_APPLICATION(true, true, false),
  TIZEN_C_WIDGET_APPLICATION(true, true, true),
  TIZEN_C_IME_APPLICATION(false, false, false),
  TIZEN_C_SHAREDLIBRARY(false, false, true),
  TIZEN_C_STATICLIBRARY(false, false, false),
  TIZEN_CPP_UI_APPLICATION(true, true, true),
  TIZEN_CPP_SERVICE_APPLICATION(false, false, true),
  TIZEN_CPP_WATCH_APPLICATION(true, true, false),
  TIZEN_CPP_WIDGET_APPLICATION(true, true, true),
  TIZEN_CPP_SHAREDLIBRARY(false, false, true),
  TIZEN_CPP_STATICLIBRARY(false, false, false),
  TIZEN_CPP_UNITTEST(true, false, false),
  TIZEN_PLATFORM_PROJECT(false, false, false),
  TIZEN_WEB_APPLICATION(false, true, true),
  TIZEN_WEB_UIFW_APPLICATION(false, true, true),
  TIZEN_WEB_UIBUILDER_APPLICATION(false, true, true),
  TIZEN_WEB_WIDGET_APPLICATION(false, true, true),
  TIZEN_WEB_WATCH_APPLICATION(true, false, false);
  
  private boolean isNeededIconValidation;
  
  private boolean isRootPrj;
  
  private boolean isRefPrj;
  
  @Deprecated
  private static final Map<TizenProjectType, Set<TizenProjectType>> avalableParentsMap;
  
  private static final Map<TizenProjectType, Set<SubProjectType>> avalableChildrenMap;
  
  public enum SelectableReferenceNum {
    ZERO, ONE, NATURAL;
  }
  
  public static class SubProjectType {
    private TizenProjectType tizenProjectType;
    
    private TizenProjectType.SelectableReferenceNum selectableNum;
    
    public SubProjectType(TizenProjectType tizenProjectType, TizenProjectType.SelectableReferenceNum selectableNum) {
      this.tizenProjectType = tizenProjectType;
      this.selectableNum = selectableNum;
    }
    
    public TizenProjectType getProjectType() {
      return this.tizenProjectType;
    }
    
    public TizenProjectType.SelectableReferenceNum getSelectableReferenceNum() {
      return this.selectableNum;
    }
  }
  
  static {
    avalableParentsMap = new EnumMap<>(TizenProjectType.class);
    avalableChildrenMap = new EnumMap<>(TizenProjectType.class);
    avalableParentsMap.put(TIZEN_WEB_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_WEB_WIDGET_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_WEB_UIBUILDER_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_WEB_UIFW_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_C_UI_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_C_SERVICE_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_C_WIDGET_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_CPP_UI_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_CPP_SERVICE_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_CPP_WIDGET_APPLICATION, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_C_SHAREDLIBRARY, new HashSet<TizenProjectType>() {
        
        });
    avalableParentsMap.put(TIZEN_CPP_SHAREDLIBRARY, new HashSet<TizenProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_C_UI_APPLICATION, new HashSet<SubProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_C_WATCH_APPLICATION, new HashSet<SubProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_C_IME_APPLICATION, new HashSet<SubProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_WEB_APPLICATION, new HashSet<SubProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_WEB_UIBUILDER_APPLICATION, new HashSet<SubProjectType>() {
        
        });
    avalableChildrenMap.put(TIZEN_WEB_UIFW_APPLICATION, new HashSet<SubProjectType>() {
        
        });
  }
  
  TizenProjectType(boolean isNeededIconValidation, boolean isRootPrj, boolean isRefPrj) {
    this.isNeededIconValidation = isNeededIconValidation;
    this.isRootPrj = isRootPrj;
    this.isRefPrj = isRefPrj;
  }
  
  public boolean isNeededIconValidation() {
    return this.isNeededIconValidation;
  }
  
  public boolean isRootProject() {
    return this.isRootPrj;
  }
  
  public boolean isRootProject(TizenProjectType childType) {
    Set<SubProjectType> childrenSet = avalableChildrenMap.get(this);
    if (childrenSet == null)
      return false; 
    for (SubProjectType type : childrenSet) {
      if (type.tizenProjectType.equals(childType))
        return true; 
    } 
    return false;
  }
  
  public SubProjectType getAvailableSubProjectType(TizenProjectType childType) {
    Set<SubProjectType> childrenSet = avalableChildrenMap.get(this);
    if (childrenSet == null)
      return null; 
    for (SubProjectType subType : childrenSet) {
      if (subType.selectableNum == SelectableReferenceNum.ZERO)
        continue; 
      if (subType.tizenProjectType.equals(childType))
        return subType; 
    } 
    return null;
  }
  
  public boolean isReferencedProject() {
    return this.isRefPrj;
  }
  
  public boolean isReferencedProject(TizenProjectType parentType) {
    Set<TizenProjectType> parentSet = avalableParentsMap.get(this);
    if (parentSet == null)
      return false; 
    for (TizenProjectType type : avalableParentsMap.get(this)) {
      if (type.equals(parentType))
        return true; 
    } 
    return false;
  }
  
  public boolean isNativeProject() {
    switch (this) {
      case TIZEN_C_UI_APPLICATION:
      case TIZEN_C_COMPONENT_APPLICATION:
      case null:
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_C_WATCH_APPLICATION:
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_C_IME_APPLICATION:
      case TIZEN_C_SHAREDLIBRARY:
      case TIZEN_C_STATICLIBRARY:
      case TIZEN_CPP_UI_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
      case TIZEN_CPP_WATCH_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
      case TIZEN_CPP_SHAREDLIBRARY:
      case TIZEN_CPP_STATICLIBRARY:
      case TIZEN_CPP_UNITTEST:
      case TIZEN_PLATFORM_PROJECT:
        return true;
    } 
    return false;
  }
  
  public boolean isNativeApplicationProject() {
    switch (this) {
      case TIZEN_C_UI_APPLICATION:
      case TIZEN_C_COMPONENT_APPLICATION:
      case null:
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_C_WATCH_APPLICATION:
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_C_IME_APPLICATION:
      case TIZEN_CPP_UI_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
      case TIZEN_CPP_WATCH_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
      case TIZEN_CPP_UNITTEST:
        return true;
    } 
    return false;
  }
  
  public boolean isNativeSharedLibraryProject() {
    switch (this) {
      case TIZEN_C_SHAREDLIBRARY:
      case TIZEN_CPP_SHAREDLIBRARY:
        return true;
    } 
    return false;
  }
  
  public boolean isNativeStaticLibraryProject() {
    switch (this) {
      case TIZEN_C_STATICLIBRARY:
      case TIZEN_CPP_STATICLIBRARY:
        return true;
    } 
    return false;
  }
  
  public boolean isNativeDebuggableProject() {
    switch (this) {
      case TIZEN_C_UI_APPLICATION:
      case TIZEN_C_COMPONENT_APPLICATION:
      case null:
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_C_IME_APPLICATION:
      case TIZEN_CPP_UI_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
      case TIZEN_CPP_UNITTEST:
        return true;
    } 
    return false;
  }
  
  public boolean isWebProject() {
    switch (this) {
      case TIZEN_WEB_APPLICATION:
      case TIZEN_WEB_UIFW_APPLICATION:
      case TIZEN_WEB_UIBUILDER_APPLICATION:
      case TIZEN_WEB_WIDGET_APPLICATION:
      case TIZEN_WEB_WATCH_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public boolean isPlatformProject() {
    switch (this) {
      case TIZEN_PLATFORM_PROJECT:
        return true;
    } 
    return false;
  }
  
  public boolean isOSPProject() {
    switch (this) {
      case TIZEN_CPP_UI_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
      case TIZEN_CPP_WATCH_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public boolean isCoreProject() {
    switch (this) {
      case TIZEN_C_UI_APPLICATION:
      case TIZEN_C_COMPONENT_APPLICATION:
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_C_WATCH_APPLICATION:
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_C_IME_APPLICATION:
      case TIZEN_CPP_UNITTEST:
        return true;
    } 
    return false;
  }
  
  public boolean isNativeServiceApplicationProject() {
    switch (this) {
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public boolean isImeProject() {
    switch (this) {
      case TIZEN_C_IME_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public String getAppIdForLaunch(String appId) {
    if (StringUtil.isEmpty(appId))
      return appId; 
    if (equals(TIZEN_C_WIDGET_APPLICATION) || 
      equals(TIZEN_C_WATCH_APPLICATION))
      return "org.tizen.widget_viewer_sdk widget_id " + appId; 
    return appId;
  }
  
  public boolean isNativeWidgetApplicationProject() {
    switch (this) {
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public boolean isWebWidgetApplicationProject() {
    switch (this) {
      case TIZEN_WEB_WIDGET_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public boolean isWebWatchApplicationProject() {
    switch (this) {
      case TIZEN_WEB_WATCH_APPLICATION:
        return true;
    } 
    return false;
  }
  
  public SelectableReferenceNum getSelectableReferenceNum(TizenProjectType childTizenProjectType) {
    SubProjectType type = getAvailableSubProjectType(childTizenProjectType);
    if (type != null)
      return type.getSelectableReferenceNum(); 
    return SelectableReferenceNum.ZERO;
  }
  
  public String getProjectType() {
    if (isWebProject())
      return "Web"; 
    if (isNativeProject())
      return "Native"; 
    return "";
  }
  
  public String getAppType() {
    switch (this) {
      case TIZEN_C_UI_APPLICATION:
      case TIZEN_CPP_UI_APPLICATION:
        return "UI";
      case TIZEN_C_SERVICE_APPLICATION:
      case TIZEN_CPP_SERVICE_APPLICATION:
        return "Service";
      case TIZEN_C_WATCH_APPLICATION:
      case TIZEN_CPP_WATCH_APPLICATION:
      case TIZEN_WEB_WATCH_APPLICATION:
        return "Watch";
      case TIZEN_C_WIDGET_APPLICATION:
      case TIZEN_CPP_WIDGET_APPLICATION:
      case TIZEN_WEB_WIDGET_APPLICATION:
        return "Widget";
      case TIZEN_C_SHAREDLIBRARY:
      case TIZEN_C_STATICLIBRARY:
      case TIZEN_CPP_SHAREDLIBRARY:
      case TIZEN_CPP_STATICLIBRARY:
        return "Library";
      case TIZEN_C_IME_APPLICATION:
        return "IME";
      case TIZEN_CPP_UNITTEST:
        return "Unittest";
      case TIZEN_PLATFORM_PROJECT:
        return "Platform";
      case TIZEN_C_COMPONENT_APPLICATION:
      case null:
        return "COMPONENT";
      case TIZEN_WEB_APPLICATION:
      case TIZEN_WEB_UIFW_APPLICATION:
      case TIZEN_WEB_UIBUILDER_APPLICATION:
        return "";
    } 
    return "";
  }
}
