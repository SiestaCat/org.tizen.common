package org.tizen.common.util.cache;

import java.io.InputStream;
import java.util.HashMap;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.SWTUtil;

public class ImageCache {
  private static final String ICON_ROOT_PATH = "icons/";
  
  private static final HashMap<String, Image> _ImageMap = new HashMap<>();
  
  public static Image getImage(String fileName) {
    fileName = "icons/" + fileName;
    Image image = _ImageMap.get(fileName);
    if (image == null) {
      image = createImage(fileName);
      _ImageMap.put(fileName, image);
    } 
    return image;
  }
  
  private static synchronized Image createImage(String fileName) {
    ClassLoader classLoader = ImageCache.class.getClassLoader();
    InputStream is = classLoader.getResourceAsStream(fileName);
    try {
      if (is == null)
        is = classLoader.getResourceAsStream(fileName.substring(1)); 
      if (is == null) {
        is = classLoader.getResourceAsStream(fileName);
        if (is == null) {
          is = classLoader.getResourceAsStream(fileName.substring(1));
          if (is == null)
            return null; 
        } 
      } 
      return new Image((Device)SWTUtil.getDisplay(), is);
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
  }
  
  public static synchronized void dispose() {
    SWTUtil.tryDispose((Widget[])_ImageMap.values().toArray((Object[])new Widget[0]));
    _ImageMap.clear();
  }
}
