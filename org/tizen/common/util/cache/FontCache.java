package org.tizen.common.util.cache;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Widget;
import org.tizen.common.util.ArrayUtil;
import org.tizen.common.util.SWTUtil;

public class FontCache {
  private static List<OneFont> _existing = new ArrayList<>();
  
  public static synchronized void disposeAll() {
    SWTUtil.tryDispose(_existing.<Widget>toArray(new Widget[0]));
    _existing.clear();
  }
  
  public static synchronized Font getFont(FontData fd) {
    if (fd == null)
      return null; 
    boolean disposed = false;
    OneFont toRemove = null;
    for (OneFont oneFont : _existing) {
      if (oneFont.matches(fd)) {
        if (oneFont.getFont().isDisposed()) {
          disposed = true;
          toRemove = oneFont;
          break;
        } 
        return oneFont.getFont();
      } 
    } 
    if (disposed)
      _existing.remove(toRemove); 
    OneFont of = new OneFont(fd);
    _existing.add(of);
    return of.getFont();
  }
  
  public static synchronized int getCount() {
    return _existing.size();
  }
  
  public static Font getFont(Font font) {
    if (font == null || font.isDisposed())
      return null; 
    FontData fd = ArrayUtil.<FontData>pickupFirst(font.getFontData());
    return getFont(fd);
  }
  
  public static synchronized Font getFont(String fontName, int height, int style) {
    if (fontName == null)
      return null; 
    boolean disposed = false;
    OneFont toRemove = null;
    for (OneFont oneFont : _existing) {
      if (oneFont.getName().equals(fontName) && oneFont.getHeight() == height && oneFont.getStyle() == style) {
        if (oneFont.getFont().isDisposed()) {
          disposed = true;
          toRemove = oneFont;
          break;
        } 
        return oneFont.getFont();
      } 
    } 
    if (disposed)
      _existing.remove(toRemove); 
    OneFont of = new OneFont(fontName, height, style);
    _existing.add(of);
    return of.getFont();
  }
}
