package org.tizen.common.util;

import com.lowagie.text.pdf.BaseFont;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class FontUtil {
  private static final String LOCALE_CODE_EN_US = "1033";
  
  public static String getFontFamilyName(String fontPath) throws FontException {
    String ext = FilenameUtil.getExtension(fontPath);
    int fontType = 0;
    try {
      if ("ttf".equals(ext)) {
        fontType = 0;
      } else {
        if ("otf".equals(ext)) {
          BaseFont bf = BaseFont.createFont(fontPath, "Cp1252", true);
          String[][] families = bf.getFamilyFontName();
          byte b;
          int i;
          String[][] arrayOfString1;
          for (i = (arrayOfString1 = families).length, b = 0; b < i; ) {
            String[] family = arrayOfString1[b];
            if ("1033".equals(family[2]))
              return family[3]; 
            b++;
          } 
          return families[0][3];
        } 
        throw new FontException("Unsupported extension: " + ext);
      } 
      Font font = Font.createFont(fontType, new File(fontPath));
      return font.getFamily(Locale.ENGLISH);
    } catch (Exception e) {
      throw new FontException(e);
    } 
  }
  
  public static String[] getFontPathList(String fontDir, String... fontExtensions) throws IOException {
    ArrayList<String> fontPathList = new ArrayList<>();
    File file = new File(fontDir);
    String[] list = file.list();
    if (list == null)
      return fontPathList.<String>toArray(new String[0]); 
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = list).length, b = 0; b < i; ) {
      String path = arrayOfString1[b];
      String ext = FilenameUtil.getExtension(path);
      byte b1;
      int j;
      String[] arrayOfString;
      for (j = (arrayOfString = fontExtensions).length, b1 = 0; b1 < j; ) {
        String fontExt = arrayOfString[b1];
        if (fontExt.equalsIgnoreCase(ext))
          fontPathList.add(path); 
        b1++;
      } 
      b++;
    } 
    return fontPathList.<String>toArray(new String[0]);
  }
}
