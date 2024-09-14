package org.tizen.common.util.cache;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.tizen.common.util.Assert;
import org.tizen.common.util.SWTUtil;

public class NamedColorCache {
  private String[] keys;
  
  private Color[] colors;
  
  public NamedColorCache(String[] keys) {
    Assert.notEmpty(keys, "Argument must not be null");
    this.keys = keys;
    this.colors = new Color[keys.length];
  }
  
  private int getIndex(String key) {
    int index = -1;
    for (int i = 0; i < this.keys.length; i++) {
      if (key != null && key.equals(this.keys[i])) {
        index = i;
        break;
      } 
    } 
    return index;
  }
  
  public Color getColor(String key) {
    int index = getIndex(key);
    if (index < 0)
      return null; 
    return this.colors[index];
  }
  
  public void setColor(String key, RGB rgb) {
    int index = getIndex(key);
    if (index < 0)
      return; 
    SWTUtil.tryDispose(new Resource[] { (Resource)this.colors[index] });
    this.colors[index] = new Color((Device)SWTUtil.getDisplay(), rgb);
  }
  
  public void dispose() {
    SWTUtil.tryDispose((Resource[])this.colors);
  }
}
