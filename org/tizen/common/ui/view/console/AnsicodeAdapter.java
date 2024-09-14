package org.tizen.common.ui.view.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.tizen.common.util.MapUtil;
import org.tizen.common.util.SWTUtil;

public class AnsicodeAdapter {
  public static final char ESCAPE = '\033';
  
  public static final Color BLACK = new Color((Device)SWTUtil.getDisplay(), 0, 0, 0);
  
  public static final Color RED = new Color((Device)SWTUtil.getDisplay(), 255, 0, 0);
  
  public static final Color GREEN = new Color((Device)SWTUtil.getDisplay(), 0, 255, 0);
  
  public static final Color YELLOW = new Color((Device)SWTUtil.getDisplay(), 255, 255, 0);
  
  public static final Color BLUE = new Color((Device)SWTUtil.getDisplay(), 0, 0, 255);
  
  public static final Color MAGENTA = new Color((Device)SWTUtil.getDisplay(), 255, 0, 255);
  
  public static final Color CYAN = new Color((Device)SWTUtil.getDisplay(), 0, 255, 255);
  
  public static final Color WHITE = new Color((Device)SWTUtil.getDisplay(), 255, 255, 255);
  
  public static final Color INTENSE_BLACK = new Color((Device)SWTUtil.getDisplay(), 0, 0, 0);
  
  public static final Color INTENSE_RED = new Color((Device)SWTUtil.getDisplay(), 139, 0, 0);
  
  public static final Color INTENSE_GREEN = new Color((Device)SWTUtil.getDisplay(), 0, 100, 0);
  
  public static final Color INTENSE_YELLOW = new Color((Device)SWTUtil.getDisplay(), 250, 250, 210);
  
  public static final Color INTENSE_BLUE = new Color((Device)SWTUtil.getDisplay(), 0, 0, 139);
  
  public static final Color INTENSE_MAGENTA = new Color((Device)SWTUtil.getDisplay(), 139, 0, 139);
  
  public static final Color INTENSE_CYAN = new Color((Device)SWTUtil.getDisplay(), 0, 139, 139);
  
  public static final Color INTENSE_WHITE = new Color((Device)SWTUtil.getDisplay(), 245, 245, 245);
  
  public static final Map<Integer, Color[]> CODE2COLOR = (Map)Collections.unmodifiableMap(MapUtil.asMap(new Object[][] { 
          { Integer.valueOf(30), { INTENSE_BLACK, BLACK } }, { Integer.valueOf(31), { INTENSE_RED, RED } }, { Integer.valueOf(32), { INTENSE_GREEN, GREEN } }, { Integer.valueOf(33), { INTENSE_YELLOW, YELLOW } }, { Integer.valueOf(34), { INTENSE_BLUE, BLUE } }, { Integer.valueOf(35), { INTENSE_MAGENTA, MAGENTA } }, { Integer.valueOf(36), { INTENSE_CYAN, CYAN } }, { Integer.valueOf(37), { INTENSE_WHITE, WHITE } }, { Integer.valueOf(40), { INTENSE_BLACK, BLACK } }, { Integer.valueOf(41), { INTENSE_RED, RED } }, 
          { Integer.valueOf(42), { INTENSE_GREEN, GREEN } }, { Integer.valueOf(43), { INTENSE_YELLOW, YELLOW } }, { Integer.valueOf(44), { INTENSE_BLUE, BLUE } }, { Integer.valueOf(45), { INTENSE_MAGENTA, MAGENTA } }, { Integer.valueOf(46), { INTENSE_CYAN, CYAN } }, { Integer.valueOf(47), { INTENSE_WHITE, WHITE } } }));
  
  private static int[] commands = new int[] { 109, 110 };
  
  private static List<TextStyle> parseAnsiString(String ansiString) {
    char command = Character.MIN_VALUE;
    List<TextStyle> styles = new ArrayList<>();
    int arrayIndex = 0;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ansiString.length(); i++) {
      if (ansiString.charAt(i) == '\033') {
        int min = ansiString.length() - 1;
        int startCommand = 0;
        for (int j = 0; j < commands.length; j++) {
          startCommand = ansiString.indexOf(commands[j], i);
          if (min > startCommand && startCommand > -1)
            min = startCommand; 
        } 
        command = ansiString.charAt(min);
        startCommand = min;
        if (startCommand < ansiString.length()) {
          String colorCodes = ansiString.substring(i + 2, startCommand);
          String[] codes = colorCodes.split(";");
          Color[] colors = getColorAttribute(codes);
          int styleStart = startCommand + 1;
          int styleEnd = 0;
          int endEscape = ansiString.indexOf('\033', styleStart);
          if (endEscape < 0) {
            styleEnd = ansiString.length();
            i = ansiString.length();
          } else {
            int endCommand = ansiString.indexOf(command, endEscape);
            if (endCommand < 0) {
              styleEnd = ansiString.length();
              i = ansiString.length();
            } else {
              styleEnd = endEscape;
              i = endCommand;
            } 
          } 
          String styleString = null;
          styleString = ansiString.substring(styleStart, styleEnd);
          TextStyle styleTs = new TextStyle(colors[0], colors[1], styleString);
          if (sb.length() > 0) {
            TextStyle defaultTs = new TextStyle(BLACK, WHITE, sb.toString());
            styles.add(arrayIndex++, defaultTs);
            sb.setLength(0);
          } 
          styles.add(arrayIndex++, styleTs);
        } 
      } else {
        sb.append(ansiString.charAt(i));
      } 
    } 
    if (sb.length() > 0) {
      TextStyle defaultTs = new TextStyle(BLACK, WHITE, sb.toString());
      styles.add(arrayIndex, defaultTs);
    } 
    return styles;
  }
  
  private static Color[] getColorAttribute(String[] codes) {
    boolean brighter = false;
    Color[] color = { BLACK, WHITE };
    for (int j = 0; j < codes.length; j++) {
      if (codes[j].length() > 0 && codes[j].matches("[\\d]*")) {
        int code = Integer.parseInt(codes[j]);
        if (code == 0) {
          brighter = false;
        } else if (code == 1) {
          brighter = true;
        } else if (code >= 30 && code <= 39) {
          if (code == 39) {
            color[0] = BLACK;
          } else if (code >= 30 && code <= 37) {
            color[0] = getColorFromANSICode(code, brighter);
            brighter = false;
          } 
        } else if (code >= 40 && code <= 49) {
          if (code == 49) {
            color[1] = WHITE;
          } else if (code >= 40 && code <= 47) {
            color[1] = getColorFromANSICode(code, brighter);
          } 
        } 
      } 
    } 
    return color;
  }
  
  private static Color getColorFromANSICode(int code, boolean brighter) {
    Color[] colorSet = CODE2COLOR.get(Integer.valueOf(code));
    if (colorSet == null)
      return null; 
    return colorSet[brighter ? 0 : 1];
  }
  
  public static String getStripAnsiString(String newLineStr) {
    List<TextStyle> styles = parseAnsiString(newLineStr);
    if (styles != null) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < styles.size(); i++)
        sb.append(((TextStyle)styles.get(i)).getStripString()); 
      return sb.toString();
    } 
    return newLineStr;
  }
  
  public static List<TextStyle> getStringStyles(String newLineStr) {
    List<TextStyle> styles = parseAnsiString(newLineStr);
    return styles;
  }
}
