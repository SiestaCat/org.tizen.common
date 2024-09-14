package org.tizen.common.ui.widget;

import java.util.EventObject;

class ImageClickedEvent extends EventObject {
  private static final long serialVersionUID = 2923125560667763769L;
  
  public int x;
  
  public int y;
  
  public ImageClickedEvent(Object source, int x, int y) {
    super(source);
    this.x = x;
    this.y = y;
  }
}
