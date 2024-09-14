package org.tizen.common.util.cache;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.tizen.common.util.SWTUtil;

class OneFont {
  private String _name;
  
  private int _height;
  
  private int _style;
  
  private Font _font;
  
  public OneFont(String name, int height, int style) {
    this._name = name;
    this._height = height;
    this._style = style;
    this._font = new Font((Device)SWTUtil.getDisplay(), name, height, style);
  }
  
  public OneFont(FontData fd) {
    this._name = fd.getName();
    this._height = fd.getHeight();
    this._style = fd.getStyle();
    this._font = new Font((Device)SWTUtil.getDisplay(), fd);
  }
  
  public String getName() {
    return this._name;
  }
  
  public int getHeight() {
    return this._height;
  }
  
  public int getStyle() {
    return this._style;
  }
  
  public void setFont(Font font) {
    this._font = font;
  }
  
  public Font getFont() {
    return this._font;
  }
  
  public boolean matches(FontData fd) {
    return (fd.getName().equals(this._name) && fd.getHeight() == this._height && fd.getStyle() == this._style);
  }
  
  public String toString() {
    return "Font: " + this._name + " " + this._height + " " + this._style;
  }
}
