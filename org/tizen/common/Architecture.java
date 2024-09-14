package org.tizen.common;

import org.tizen.common.util.Assert;

public enum Architecture {
  i386(new Object[] { Group.x86 }),
  i486(new Object[] { Group.x86 }),
  i586(new Object[] { Group.x86 }),
  i686(new Object[] { Group.x86 }),
  ia32(new Object[] { Group.x86 }),
  armel(new Object[] { Group.arm }),
  armv7a(new Object[] { Group.arm, "ARMv7-a" }),
  armv7l(new Object[] { Group.arm });
  
  protected Object[] attributes;
  
  public enum Group {
    x86, arm, unknown;
  }
  
  Architecture(Object... attributes) {
    Assert.notNull(attributes);
    this.attributes = attributes;
  }
  
  public Group getGroup() {
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = this.attributes).length, b = 0; b < i; ) {
      Object attribute = arrayOfObject[b];
      if (attribute instanceof Group)
        return (Group)attribute; 
      b++;
    } 
    return Group.unknown;
  }
  
  public String getName() {
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = this.attributes).length, b = 0; b < i; ) {
      Object attribute = arrayOfObject[b];
      if (attribute instanceof String)
        return (String)attribute; 
      b++;
    } 
    return name();
  }
  
  public static Group getGroup(String arch) {
    Architecture result = valueOf(arch);
    return (result != null) ? result.getGroup() : Group.unknown;
  }
}
