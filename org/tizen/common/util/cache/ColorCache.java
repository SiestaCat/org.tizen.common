package org.tizen.common.util.cache;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Widget;
import org.tizen.common.util.SWTUtil;

public final class ColorCache {
  public static final RGB BLACK = new RGB(0, 0, 0);
  
  public static final RGB WHITE = new RGB(255, 255, 255);
  
  private static Map<RGB, Color> _colorTable = new HashMap<>();
  
  public static synchronized void disposeColors() {
    SWTUtil.tryDispose((Widget[])_colorTable.values().toArray((Object[])new Widget[0]));
    _colorTable.clear();
  }
  
  public static Color getWhite() {
    return getColorFromRGB(new RGB(255, 255, 255));
  }
  
  public static Color getBlack() {
    return getColorFromRGB(new RGB(0, 0, 0));
  }
  
  public static synchronized Color getColorFromRGB(RGB rgb) {
    Color color = _colorTable.get(rgb);
    if (color == null) {
      color = new Color((Device)SWTUtil.getDisplay(), rgb);
      _colorTable.put(rgb, color);
    } 
    return color;
  }
  
  public static synchronized Color getColor(int r, int g, int b) {
    RGB rgb = new RGB(r, g, b);
    Color color = _colorTable.get(rgb);
    if (color == null) {
      color = new Color((Device)SWTUtil.getDisplay(), rgb);
      _colorTable.put(rgb, color);
    } 
    return color;
  }
}
