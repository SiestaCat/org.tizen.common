package org.tizen.common.util;

import org.eclipse.swt.graphics.Image;
import org.tizen.common.util.cache.ImageCache;

public enum NotificationType {
  ERROR(ImageCache.getImage("error.png")),
  DELETE(ImageCache.getImage("delete.png")),
  WARN(ImageCache.getImage("warn.png")),
  SUCCESS(ImageCache.getImage("ok.png")),
  INFO(ImageCache.getImage("info.png")),
  LIBRARY(ImageCache.getImage("library.png")),
  HINT(ImageCache.getImage("hint.png")),
  PRINTED(ImageCache.getImage("printer.png")),
  CONNECTION_TERMINATED(ImageCache.getImage("terminated.png")),
  CONNECTION_FAILED(ImageCache.getImage("connecting.png")),
  CONNECTED(ImageCache.getImage("connected.png")),
  DISCONNECTED(ImageCache.getImage("disconnected.png")),
  TRANSACTION_OK(ImageCache.getImage("ok.png")),
  TRANSACTION_FAIL(ImageCache.getImage("error.png")),
  XBUTTON_NORMAL(ImageCache.getImage("msg_close_normal.png")),
  XBUTTON_HOVER(ImageCache.getImage("msg_close_hover.png")),
  XBUTTON_PUSH(ImageCache.getImage("msg_close_push.png"));
  
  private Image _image;
  
  NotificationType(Image img) {
    this._image = img;
  }
  
  public Image getImage() {
    return this._image;
  }
}
