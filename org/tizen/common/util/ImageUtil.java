package org.tizen.common.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.bmp.BMPEncoder;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.SurrogateWithArgument;
import org.tizen.common.rds.RdsDeltaDetector;

public class ImageUtil {
  private static final Logger logger = LoggerFactory.getLogger(RdsDeltaDetector.class);
  
  public static class PlatformSurrogate {
    public Bundle getBundle(String pluginID) {
      return Platform.getBundle(pluginID);
    }
  }
  
  public static class PluginSurrogate {
    public Bundle getBundle(Plugin plugin) {
      return plugin.getBundle();
    }
  }
  
  public static final String[] supportedImageExtension = new String[] { "*.gif;*.png;*.jpeg;*.jpg;*.tiff;*.tif;*.ico;*.bmp;*.rle", 
      "*.gif", 
      "*.png", 
      "*.jpeg;*.jpg", 
      "*.tiff;*.tif", 
      "*.ico", 
      "*.bmp", 
      "*.rle" };
  
  public static final String ICO = "ico";
  
  public static final String BMP = "bmp";
  
  public static final String SVG = "svg";
  
  public static final String JPG = "jpg";
  
  public static final String JPEG = "jpeg";
  
  protected static ImageDescriptor getImageDescriptor(Bundle bundle, String filePath) {
    URL url = bundle.getEntry(filePath);
    if (url == null)
      url = bundle.getResource(filePath); 
    return ImageDescriptor.createFromURL(url);
  }
  
  protected static SurrogateWithArgument<Bundle, String> platformSurrogate = new SurrogateWithArgument<Bundle, String>() {
      public Bundle getAdapter(String pluginId) {
        return Platform.getBundle(pluginId);
      }
    };
  
  protected static SurrogateWithArgument<Bundle, Plugin> pluginSurrogate = new SurrogateWithArgument<Bundle, Plugin>() {
      public Bundle getAdapter(Plugin plugin) {
        return plugin.getBundle();
      }
    };
  
  public static ImageDescriptor getImageDescriptor(String pluginID, String filePath) {
    return getImageDescriptor(platformSurrogate.getAdapter(pluginID), filePath);
  }
  
  public static ImageDescriptor getImageDescriptor(Plugin plugin, String filePath) {
    return getImageDescriptor(pluginSurrogate.getAdapter(plugin), filePath);
  }
  
  public static Image getImage(ImageDescriptor descriptor) {
    return (descriptor != null) ? descriptor.createImage() : null;
  }
  
  public static ImageData getImageData(ImageDescriptor descriptor) {
    return (descriptor != null) ? descriptor.getImageData() : null;
  }
  
  public static Image getImage(Plugin plugin, String filePath) {
    ImageDescriptor descriptor = getImageDescriptor(plugin, filePath);
    return getImage(descriptor);
  }
  
  public static ImageData getImageData(Plugin plugin, String filePath) {
    ImageDescriptor descriptor = getImageDescriptor(plugin, filePath);
    return getImageData(descriptor);
  }
  
  public static Image getImage(String pluginID, String filePath) {
    ImageDescriptor descriptor = getImageDescriptor(pluginID, filePath);
    return getImage(descriptor);
  }
  
  public static ImageData getImageData(String pluginID, String filePath) {
    ImageDescriptor descriptor = getImageDescriptor(pluginID, filePath);
    return getImageData(descriptor);
  }
  
  public static BufferedImage getScaledImage(BufferedImage src, int width, int height) {
    return getScaledImage(src, width, height, 2);
  }
  
  public static BufferedImage getScaledImage(BufferedImage src, int width, int height, int imgType) {
    Image scaledImage = src.getScaledInstance(width, height, 4);
    BufferedImage bi = new BufferedImage(width, height, imgType);
    Graphics g = bi.getGraphics();
    g.drawImage(scaledImage, 0, 0, null);
    g.dispose();
    return bi;
  }
  
  public static BufferedImage getBufferedImage(URL url) {
    if (url == null)
      return null; 
    InputStream input = null;
    BufferedImage bImage = null;
    int contentLength = 0;
    try {
      if (url.getProtocol().equalsIgnoreCase("file"))
        url = URIUtil.toURI(url).toURL(); 
      URLConnection con = url.openConnection();
      con.setConnectTimeout(100);
      contentLength = con.getContentLength();
      if (contentLength == 0 || contentLength == -1)
        return null; 
      input = url.openStream();
      bImage = getBufferedImage(input);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } catch (URISyntaxException e) {
      logger.error(e.getMessage(), e);
    } finally {
      IOUtil.tryClose(new Object[] { input });
    } 
    return bImage;
  }
  
  public static List<BufferedImage> getAWTImages(InputStream is, String iconFilePath) throws IOException {
    List<BufferedImage> images = new ArrayList<>();
    BufferedInputStream bis = new BufferedInputStream(is);
    bis.mark(2147483647);
    String formatName = getFormatName(bis, iconFilePath);
    bis.reset();
    if (!StringUtil.isEmpty(formatName))
      if (formatName.equalsIgnoreCase("ico")) {
        images = ICODecoder.read(bis);
      } else if (formatName.equalsIgnoreCase("bmp")) {
        images.add(BMPDecoder.read(bis));
      } else if (formatName.equalsIgnoreCase("svg")) {
        SVGUniverse svgUnverse = new SVGUniverse();
        URI iconUri = svgUnverse.loadSVG(bis, formatName);
        if (iconUri == null)
          return images; 
        SVGDiagram diagram = svgUnverse.getDiagram(iconUri);
        if (diagram == null)
          return images; 
        BufferedImage image = new BufferedImage((int)diagram.getWidth(), (int)diagram.getHeight(), 2);
        Graphics2D graphics = null;
        try {
          graphics = image.createGraphics();
          diagram.render(graphics);
          images.add(image);
        } catch (SVGException e) {
          throw new IOException(e);
        } finally {
          if (graphics != null)
            graphics.dispose(); 
        } 
      } else {
        images.add(ImageIO.read(bis));
      }  
    return images;
  }
  
  public static BufferedImage getBufferedImage(InputStream input) {
    if (input == null)
      return null; 
    BufferedImage bImage = null;
    ImageInputStream stream = null;
    try {
      stream = ImageIO.createImageInputStream(input);
      Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
      while (iter.hasNext()) {
        ImageReader reader = null;
        try {
          reader = iter.next();
          ImageReadParam param = reader.getDefaultReadParam();
          reader.setInput(stream, true, true);
          Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
          while (imageTypes.hasNext()) {
            ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
            int bufferedImageType = imageTypeSpecifier.getBufferedImageType();
            if (bufferedImageType == 10) {
              param.setDestinationType(imageTypeSpecifier);
              break;
            } 
          } 
          bImage = reader.read(0, param);
          if (bImage != null) {
            IOUtil.tryClose(new Object[] { reader });
            break;
          } 
        } catch (Exception e) {
          throw e;
        } finally {
          IOUtil.tryClose(new Object[] { reader });
        } 
      } 
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      IOUtil.tryClose(new Object[] { stream });
    } 
    return bImage;
  }
  
  public static BufferedImage getAWTImage(String path, boolean isRelativePath) {
    InputStream is = null;
    List<BufferedImage> images = null;
    try {
      if (isRelativePath) {
        is = ImageUtil.class.getResourceAsStream(path);
      } else {
        is = new FileInputStream(new File(path));
      } 
      images = getAWTImages(is, path);
    } catch (IOException e) {
      logger.error("Failed to read", e);
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
    if (images == null || 
      images.isEmpty())
      return new BufferedImage(1, 1, 2); 
    return images.get(0);
  }
  
  public static Image getSWTImage(Display display, String relativePath) {
    if (StringUtil.isEmpty(relativePath))
      return new Image((Device)display, 1, 1); 
    InputStream is = ImageUtil.class.getResourceAsStream(relativePath);
    try {
      Image image = new Image((Device)Display.getDefault(), is);
      return image;
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
  }
  
  public static Image getScaledSWTImage(Image src, int width, int height) {
    if (src == null)
      return null; 
    if (src.isDisposed())
      SWT.error(24); 
    Image scaledImage = new Image(src.getDevice(), width, height);
    GC gc = new GC((Drawable)scaledImage);
    gc.setAntialias(1);
    gc.setInterpolation(2);
    gc.drawImage(src, 0, 0, (src.getBounds()).width, (src.getBounds()).height, 0, 0, width, height);
    gc.dispose();
    return scaledImage;
  }
  
  public static Image getSimpleScaledSWTImage(Image src, int width, int height) {
    Image scaledImage = null;
    if (src != null) {
      ImageData srcData = src.getImageData();
      if (srcData.width == width && srcData.height == height) {
        scaledImage = new Image(src.getDevice(), src, 0);
      } else {
        scaledImage = new Image(src.getDevice(), srcData.scaledTo(width, height));
      } 
    } 
    return scaledImage;
  }
  
  public static Image convertImageToSWT(Display display, BufferedImage bImage) {
    int width = bImage.getWidth();
    int height = bImage.getHeight();
    BufferedImage bufferedImage = new BufferedImage(width, height, 2);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(bImage, 0, 0, null);
    graphics.dispose();
    if (bufferedImage.getColorModel() instanceof DirectColorModel) {
      DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
      PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
      ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
      for (int y = 0; y < data.height; y++) {
        for (int x = 0; x < data.width; x++) {
          int rgb = bufferedImage.getRGB(x, y);
          int pixel = palette.getPixel(new RGB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF));
          data.setPixel(x, y, pixel);
          if (colorModel.hasAlpha())
            data.setAlpha(x, y, rgb >> 24 & 0xFF); 
        } 
      } 
      return new Image((Device)display, data);
    } 
    if (bufferedImage.getColorModel() instanceof IndexColorModel) {
      IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
      int size = colorModel.getMapSize();
      byte[] reds = new byte[size];
      byte[] greens = new byte[size];
      byte[] blues = new byte[size];
      colorModel.getReds(reds);
      colorModel.getGreens(greens);
      colorModel.getBlues(blues);
      RGB[] rgbs = new RGB[size];
      for (int i = 0; i < rgbs.length; i++)
        rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF); 
      PaletteData palette = new PaletteData(rgbs);
      ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
      data.transparentPixel = colorModel.getTransparentPixel();
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[1];
      for (int y = 0; y < data.height; y++) {
        for (int x = 0; x < data.width; x++) {
          raster.getPixel(x, y, pixelArray);
          data.setPixel(x, y, pixelArray[0]);
        } 
      } 
      return new Image((Device)display, data);
    } 
    return null;
  }
  
  public static BufferedImage pileUp(BufferedImage bottom, Point bottomPoint, BufferedImage top, Point topPoint, int width, int height, int imgType) {
    BufferedImage img = new BufferedImage(width, height, imgType);
    boolean result = false;
    Graphics2D g = null;
    try {
      g = img.createGraphics();
      result = g.drawImage(bottom, (int)bottomPoint.getX(), (int)bottomPoint.getY(), (ImageObserver)null);
      if (!result)
        return null; 
      result = g.drawImage(top, (int)topPoint.getX(), (int)topPoint.getY(), (ImageObserver)null);
    } finally {
      if (g != null)
        g.dispose(); 
    } 
    if (g != null)
      g.dispose(); 
    return img;
  }
  
  public static BufferedImage convertToAWT(ImageData data) {
    ColorModel colorModel = null;
    PaletteData palette = data.palette;
    if (palette.isDirect) {
      BufferedImage bufferedImage1 = new BufferedImage(data.width, 
          data.height, 2);
      for (int j = 0; j < data.height; j++) {
        for (int x = 0; x < data.width; x++) {
          int pixel = data.getPixel(x, j);
          RGB rgb = palette.getRGB(pixel);
          bufferedImage1.setRGB(x, j, data.getAlpha(x, j) << 24 | 
              rgb.red << 16 | rgb.green << 8 | rgb.blue);
        } 
      } 
      return bufferedImage1;
    } 
    RGB[] rgbs = palette.getRGBs();
    byte[] red = new byte[rgbs.length];
    byte[] green = new byte[rgbs.length];
    byte[] blue = new byte[rgbs.length];
    for (int i = 0; i < rgbs.length; i++) {
      RGB rgb = rgbs[i];
      red[i] = (byte)rgb.red;
      green[i] = (byte)rgb.green;
      blue[i] = (byte)rgb.blue;
    } 
    if (data.transparentPixel != -1) {
      colorModel = new IndexColorModel(data.depth, rgbs.length, red, 
          green, blue, data.transparentPixel);
    } else {
      colorModel = new IndexColorModel(data.depth, rgbs.length, red, 
          green, blue);
    } 
    BufferedImage bufferedImage = new BufferedImage(colorModel, 
        colorModel.createCompatibleWritableRaster(data.width, 
          data.height), false, null);
    WritableRaster raster = bufferedImage.getRaster();
    int[] pixelArray = new int[1];
    for (int y = 0; y < data.height; y++) {
      for (int x = 0; x < data.width; x++) {
        int pixel = data.getPixel(x, y);
        pixelArray[0] = pixel;
        raster.setPixel(x, y, pixelArray);
      } 
    } 
    return bufferedImage;
  }
  
  public static void write(BufferedImage bImg, String formatName, OutputStream os) throws IOException {
    if ("ico".equalsIgnoreCase(formatName)) {
      ICOEncoder.write(bImg, os);
    } else if ("bmp".equalsIgnoreCase(formatName)) {
      BMPEncoder.write(bImg, os);
    } else if (!"svg".equalsIgnoreCase(formatName)) {
      ImageIO.write(bImg, formatName, os);
    } 
  }
  
  public static void writeICO(List<BufferedImage> bImgs, OutputStream os) throws IOException {
    ICOEncoder.write(bImgs, os);
  }
  
  public static String getFormatName(InputStream is, String path) throws IOException {
    if (is == null)
      return null; 
    String extension = FilenameUtil.getExtension(path);
    if ("ico".equalsIgnoreCase(extension))
      return "ico"; 
    String result = null;
    result = getRawFormatName(is);
    if (result == null)
      result = extension; 
    return result;
  }
  
  public static String getFormatName(String fullPath) throws IOException {
    File file = new File(fullPath);
    if (!file.exists())
      return null; 
    InputStream is = null;
    try {
      is = new FileInputStream(fullPath);
      return getFormatName(is, fullPath);
    } finally {
      IOUtil.tryClose(new Object[] { is });
    } 
  }
  
  public static String getRawFormatName(InputStream is) throws IOException {
    if (is == null)
      return null; 
    ImageInputStream iis = null;
    String result = null;
    try {
      iis = ImageIO.createImageInputStream(is);
      result = getRawFormatName(iis);
    } finally {
      IOUtil.tryClose(new Object[] { iis });
    } 
    return result;
  }
  
  public static String getRawFormatName(ImageInputStream iis) throws IOException {
    if (iis == null)
      return null; 
    String result = null;
    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
    while (readers.hasNext()) {
      ImageReader read = readers.next();
      String formatName = read.getFormatName();
      if (formatName != null)
        return formatName; 
    } 
    return result;
  }
  
  public static void tryDispose(Image image) {
    if (image != null)
      image.dispose(); 
  }
}
