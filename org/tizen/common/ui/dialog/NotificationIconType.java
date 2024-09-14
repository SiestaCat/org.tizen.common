package org.tizen.common.ui.dialog;

import org.eclipse.swt.graphics.Image;
import org.tizen.common.util.cache.ImageCache;

public enum NotificationIconType {
  CHECK(ImageCache.getImage("noti_icon_check.png")),
  CLOSED(ImageCache.getImage("noti_icon_closed.png")),
  DISCONNECTED(ImageCache.getImage("noti_icon_disconnected.png")),
  INFO(ImageCache.getImage("noti_icon_info.png"));
  
  private Image _image;
  
  NotificationIconType(Image img) {
    this._image = img;
  }
  
  public Image getImage() {
    return this._image;
  }
}
