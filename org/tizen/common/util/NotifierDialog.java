package org.tizen.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.tizen.common.ui.dialog.NotificationIconType;
import org.tizen.common.ui.dialog.NotificationTrayPopup;
import org.tizen.common.util.cache.ColorCache;
import org.tizen.common.util.cache.FontCache;

public class NotifierDialog {
  private static final int DISPLAY_TIME = 4500;
  
  private static final int FADE_TIMER = 50;
  
  private static final int FADE_IN_STEP = 30;
  
  private static final int FADE_OUT_STEP = 8;
  
  private static final int FINAL_ALPHA = 225;
  
  private static Color _titleFgColor = ColorCache.getColor(40, 73, 97);
  
  private static Color _fgColor = _titleFgColor;
  
  private static Color _bgFgGradient = ColorCache.getColor(226, 239, 249);
  
  private static Color _bgBgGradient = ColorCache.getColor(177, 211, 243);
  
  private static Color _borderColor = ColorCache.getColor(40, 73, 97);
  
  private static List<Shell> _activeShells = new ArrayList<>();
  
  private static Image _oldImage;
  
  private static Shell _shell;
  
  private static LabelButtonListener _listner = new LabelButtonListener(null);
  
  private static int startX = 0;
  
  private static int startY = 0;
  
  public static void notify(String title, String message, NotificationType type) {
    NotificationIconType newType = remapNotiType(type);
    NotificationTrayPopup.notify(title, message, newType);
  }
  
  private static NotificationIconType remapNotiType(NotificationType type) {
    if (NotificationType.INFO == type || NotificationType.WARN == type)
      return NotificationIconType.INFO; 
    if (NotificationType.ERROR == type || 
      NotificationType.SUCCESS == type)
      return NotificationIconType.CHECK; 
    if (NotificationType.DISCONNECTED == type)
      return NotificationIconType.DISCONNECTED; 
    return NotificationIconType.INFO;
  }
  
  public static void notify(String title, String message, NotificationType type, boolean fade) {
    Shell eclipseShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    _shell = new Shell(eclipseShell, 524296);
    _shell.setLayout((Layout)new FillLayout());
    _shell.setForeground(_fgColor);
    _shell.setBackgroundMode(1);
    _shell.addListener(12, new Listener() {
          public void handleEvent(Event event) {
            if (event.widget instanceof Shell) {
              Shell shell = (Shell)event.widget;
              NotifierDialog._activeShells.remove(shell);
            } 
          }
        });
    Composite inner = new Composite((Composite)_shell, 0);
    GridLayout gl = new GridLayout(3, false);
    gl.marginLeft = 5;
    gl.marginTop = 0;
    gl.marginRight = 5;
    gl.marginBottom = 5;
    inner.setLayout((Layout)gl);
    _shell.addListener(11, new Listener() {
          public void handleEvent(Event e) {
            try {
              Rectangle rect = NotifierDialog._shell.getClientArea();
              Image newImage = new Image((Device)SWTUtil.getDisplay(), Math.max(1, rect.width), rect.height);
              GC gc = new GC((Drawable)newImage);
              gc.setForeground(NotifierDialog._bgFgGradient);
              gc.setBackground(NotifierDialog._bgBgGradient);
              gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
              gc.setLineWidth(2);
              gc.setForeground(NotifierDialog._borderColor);
              gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
              gc.dispose();
              NotifierDialog._shell.setBackgroundImage(newImage);
              if (NotifierDialog._oldImage != null)
                NotifierDialog._oldImage.dispose(); 
              NotifierDialog._oldImage = newImage;
            } catch (Exception err) {
              err.printStackTrace();
            } 
          }
        });
    GC gc = new GC((Drawable)_shell);
    String[] lines = message.split("\n");
    Point longest = null;
    int typicalHeight = (gc.stringExtent("X")).y;
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = lines).length, b = 0; b < i; ) {
      String line = arrayOfString1[b];
      Point extent = gc.stringExtent(line);
      if (longest == null) {
        longest = extent;
      } else if (extent.x > longest.x) {
        longest = extent;
      } 
      b++;
    } 
    gc.dispose();
    int minHeight = typicalHeight * lines.length;
    CLabel imgLabel = new CLabel(inner, 0);
    imgLabel.setLayoutData(new GridData(34));
    imgLabel.setImage(type.getImage());
    CLabel titleLabel = new CLabel(inner, 0);
    titleLabel.setLayoutData(new GridData(4));
    titleLabel.setText(title);
    titleLabel.setForeground(_titleFgColor);
    Font f = titleLabel.getFont();
    FontData fd = f.getFontData()[0];
    fd.setStyle(1);
    fd.height = 11.0F;
    titleLabel.setFont(FontCache.getFont(fd));
    Label button = new Label(inner, 0);
    button.setLayoutData(new GridData(130));
    button.setImage(NotificationType.XBUTTON_NORMAL.getImage());
    addLabelButtonListner(button);
    Label text = new Label(inner, 64);
    Font tf = text.getFont();
    FontData tfd = tf.getFontData()[0];
    tfd.setStyle(1);
    tfd.height = 8.0F;
    text.setFont(FontCache.getFont(tfd));
    GridData gd = new GridData(1808);
    gd.horizontalSpan = 2;
    text.setLayoutData(gd);
    text.setForeground(_fgColor);
    text.setText(message);
    minHeight = 100;
    _shell.setSize(350, minHeight);
    if (eclipseShell == null || eclipseShell.getBounds() == null)
      return; 
    Rectangle clientArea = eclipseShell.getBounds();
    startX = clientArea.x + clientArea.width - 352;
    startY = clientArea.y + clientArea.height - 102;
    if (!_activeShells.isEmpty()) {
      List<Shell> modifiable = new ArrayList<>(_activeShells);
      Collections.reverse(modifiable);
      for (Shell shell : modifiable) {
        Point curLoc = shell.getLocation();
        if (curLoc.y - 100 < 0 || curLoc.x != startX) {
          _activeShells.remove(shell);
          shell.dispose();
          continue;
        } 
        shell.setLocation(curLoc.x, curLoc.y - 100);
      } 
    } 
    _shell.setLocation(startX, startY);
    _shell.setAlpha(0);
    _shell.setVisible(true);
    _activeShells.add(_shell);
    fadeIn(_shell, fade);
  }
  
  private static void addLabelButtonListner(Label button) {
    button.addListener(3, _listner);
    button.addListener(4, _listner);
    button.addListener(6, _listner);
    button.addListener(7, _listner);
    button.addListener(32, _listner);
  }
  
  private static class LabelButtonListener implements Listener {
    private LabelButtonListener() {}
    
    public void handleEvent(Event event) {
      if (event.widget instanceof Label) {
        Label bt = (Label)event.widget;
        Shell shell = bt.getParent().getShell();
        if (3 == event.type) {
          bt.setImage(NotificationType.XBUTTON_PUSH.getImage());
        } else if (4 == event.type) {
          bt.setImage(NotificationType.XBUTTON_NORMAL.getImage());
          if (shell != null) {
            NotifierDialog._activeShells.remove(shell);
            shell.dispose();
            if (NotifierDialog._activeShells.size() > 0) {
              List<Shell> modifiable = new ArrayList<>(NotifierDialog._activeShells);
              Collections.reverse(modifiable);
              int y = 0;
              for (Shell tempshell : modifiable) {
                tempshell.setLocation((tempshell.getLocation()).x, NotifierDialog.startY - y);
                y += 100;
              } 
            } 
          } 
        } else if (32 == event.type) {
          bt.setImage(NotificationType.XBUTTON_HOVER.getImage());
        } else if (6 == event.type) {
          bt.setImage(NotificationType.XBUTTON_HOVER.getImage());
        } else if (7 == event.type) {
          bt.setImage(NotificationType.XBUTTON_NORMAL.getImage());
        } 
      } 
    }
  }
  
  private static void fadeIn(final Shell _shell, final boolean fade) {
    Runnable run = new Runnable() {
        public void run() {
          try {
            if (_shell == null || _shell.isDisposed())
              return; 
            int cur = _shell.getAlpha();
            cur += 30;
            if (cur > 225) {
              _shell.setAlpha(225);
              if (fade)
                NotifierDialog.startTimer(_shell); 
              return;
            } 
            _shell.setAlpha(cur);
            SWTUtil.getDisplay().timerExec(50, this);
          } catch (Exception err) {
            err.printStackTrace();
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(50, run);
  }
  
  private static void startTimer(final Shell _shell) {
    Runnable run = new Runnable() {
        public void run() {
          try {
            if (_shell == null || _shell.isDisposed())
              return; 
            NotifierDialog.fadeOut(_shell);
          } catch (Exception err) {
            err.printStackTrace();
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(4500, run);
  }
  
  private static void fadeOut(final Shell _shell) {
    Runnable run = new Runnable() {
        private int fadeValue = -1;
        
        public void run() {
          try {
            if (_shell == null || _shell.isDisposed())
              return; 
            if (this.fadeValue < 0)
              this.fadeValue = _shell.getAlpha(); 
            this.fadeValue -= 8;
            if (this.fadeValue <= 0) {
              _shell.setAlpha(0);
              if (NotifierDialog._oldImage != null)
                NotifierDialog._oldImage.dispose(); 
              _shell.dispose();
              NotifierDialog._activeShells.remove(_shell);
              return;
            } 
            _shell.setAlpha(this.fadeValue);
            SWTUtil.getDisplay().timerExec(50, this);
          } catch (Exception err) {
            err.printStackTrace();
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(50, run);
  }
}
