package org.tizen.common.ui.view.console;

import org.eclipse.swt.graphics.Color;

public class TextStyle {
  private Color foreground;
  
  private Color background;
  
  private String stripString;
  
  TextStyle(Color foreground, Color background, String stripString) {
    this.foreground = foreground;
    this.background = background;
    this.stripString = stripString;
  }
  
  public Color getForeground() {
    return this.foreground;
  }
  
  public Color getBackground() {
    return this.background;
  }
  
  public String getStripString() {
    return this.stripString;
  }
}
