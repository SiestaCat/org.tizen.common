package org.tizen.common.ui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.resource.DeviceResourceDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.OSChecker;
import org.tizen.common.util.SWTUtil;
import org.tizen.common.util.StringUtil;
import org.tizen.common.util.cache.ColorCache;
import org.tizen.common.util.cache.FontCache;
import org.tizen.common.util.cache.ImageCache;

public class NotificationTrayPopup {
  private static Logger logger = LoggerFactory.getLogger(NotificationTrayPopup.class);
  
  private static final int DISPLAY_TIME = 3000;
  
  private static final int FADE_TIMER = 100;
  
  private static final int FADE_IN_STEP = 30;
  
  private static final int FADE_OUT_STEP = 10;
  
  private static final int FINAL_ALPHA = 229;
  
  private static final int SHELL_HEIGHT = 110;
  
  private static final int SHELL_WIDTH = 320;
  
  private static final int TITLE_SIZE = 13;
  
  private static final int DETAIL_SIZE = OSChecker.isWindows() ? 9 : 10;
  
  private static final String systemFontName = SWTUtil.getDisplay().getSystemFont().getFontData()[0].getName();
  
  private static final Font titleFont = FontCache.getFont(new Font((Device)SWTUtil.getDisplay(), systemFontName, 13, 1));
  
  private static final Font detailFont = FontCache.getFont(new Font((Device)SWTUtil.getDisplay(), systemFontName, DETAIL_SIZE, 64));
  
  private static Color _bgColorWithAlpha = new Color((Device)SWTUtil.getDisplay(), new RGB(117, 117, 117), 229);
  
  private static Color _titleFgColor = ColorCache.getWhite();
  
  private static Color _fgColor = _titleFgColor;
  
  private static Color _fgOptionColor = new Color((Device)SWTUtil.getDisplay(), new RGB(255, 255, 255), 165);
  
  private static final int REGION_CORNER_LT_IDX = 0;
  
  private static final int REGION_CORNER_RT_IDX = 1;
  
  private static final int REGION_CORNER_LB_IDX = 2;
  
  private static final int REGION_CORNER_RB_IDX = 3;
  
  private static List<Shell> _activeShells = new ArrayList<>();
  
  private static Shell _shell;
  
  private static int startX = 0;
  
  private static int startY = 0;
  
  private static final Region[] regionCorners = (Region[])JFaceResources.getResources().get(new DeviceResourceDescriptor() {
        public Object createResource(Device device) {
          Region[] regions = new Region[4];
          regions[0] = SWTUtil.getTrimmedRegion(ImageCache.getImage("noti_popup_mask_LT.png"), 8);
          regions[1] = SWTUtil.getTrimmedRegion(ImageCache.getImage("noti_popup_mask_RT.png"), 8);
          regions[2] = SWTUtil.getTrimmedRegion(ImageCache.getImage("noti_popup_mask_LB.png"), 8);
          regions[3] = SWTUtil.getTrimmedRegion(ImageCache.getImage("noti_popup_mask_RB.png"), 8);
          return regions;
        }
        
        public void destroyResource(Object previouslyCreatedObject) {
          byte b;
          int i;
          Region[] arrayOfRegion;
          for (i = (arrayOfRegion = (Region[])previouslyCreatedObject).length, b = 0; b < i; ) {
            Region region = arrayOfRegion[b];
            SWTUtil.tryDispose(new Resource[] { (Resource)region });
            b++;
          } 
        }
      });
  
  public static void notify(String title, NotificationIconType type) {
    notify(title, null, null, null, type, true);
  }
  
  public static void notify(String title, String message, NotificationIconType type) {
    notify(title, message, null, null, type, true);
  }
  
  public static void notify(String title, String message, String option, NotificationIconType type) {
    notify(title, message, option, null, type, true);
  }
  
  public static void notify(String title, String message, String option, IHyperlinkListener hyperlinkListener, NotificationIconType info) {
    notify(title, message, option, hyperlinkListener, info, true);
  }
  
  private static void notify(String title, String message, String option, IHyperlinkListener hyperlinkListener, NotificationIconType type, boolean fade) {
    Shell eclipseShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    if (eclipseShell == null || eclipseShell.getBounds() == null)
      return; 
    if (StringUtil.isEmpty(title))
      return; 
    _shell = new Shell(eclipseShell, 524296);
    _shell.setLayout((Layout)new FillLayout());
    _shell.addListener(12, new Listener() {
          public void handleEvent(Event event) {
            if (event.widget instanceof Shell) {
              Shell shell = (Shell)event.widget;
              NotificationTrayPopup._activeShells.remove(shell);
            } 
          }
        });
    _shell.setBackground(_bgColorWithAlpha);
    _shell.setBackgroundMode(1);
    Composite inner = new Composite((Composite)_shell, 0);
    GridLayout gl = new GridLayout(2, false);
    gl.marginLeft = 25;
    gl.marginTop = 0;
    gl.marginRight = 25;
    gl.marginBottom = 0;
    gl.horizontalSpacing = 20;
    inner.setLayout((Layout)gl);
    createLeftIconSection(type, inner);
    createRightTextSection(title, message, option, inner, hyperlinkListener);
    _shell.setMinimumSize(320, 110);
    _shell.pack();
    Region conerLT = regionCorners[0];
    if (conerLT != null && !conerLT.isDisposed()) {
      Region region = new Region();
      region.add(new Rectangle(0, 0, (_shell.getSize()).x, (_shell.getSize()).y));
      region.subtract(new Rectangle(0, 0, 10, 10));
      region.add(conerLT);
      region.subtract(new Rectangle((_shell.getSize()).x - 10, 0, 10, 10));
      Region cornerRT = new Region();
      cornerRT.add(regionCorners[1]);
      cornerRT.translate((_shell.getSize()).x - 10, 0);
      region.add(cornerRT);
      region.subtract(new Rectangle(0, (_shell.getSize()).y - 10, 10, 10));
      Region cornerLB = new Region();
      cornerLB.add(regionCorners[2]);
      cornerLB.translate(0, (_shell.getSize()).y - 10);
      region.add(cornerLB);
      region.subtract(new Rectangle((_shell.getSize()).x - 10, (_shell.getSize()).y - 10, 10, 10));
      Region cornerRB = new Region();
      cornerRB.add(regionCorners[3]);
      cornerRB.translate((_shell.getSize()).x - 10, (_shell.getSize()).y - 10);
      region.add(cornerRB);
      _shell.setRegion(region);
    } 
    Rectangle clientArea = eclipseShell.getBounds();
    startX = clientArea.x + clientArea.width / 2 - (_shell.getSize()).x / 2;
    startY = clientArea.y + clientArea.height - (_shell.getSize()).y - 225;
    if (!_activeShells.isEmpty()) {
      List<Shell> modifiable = new ArrayList<>(_activeShells);
      Collections.reverse(modifiable);
      for (Shell shell : modifiable) {
        Point curLoc = shell.getLocation();
        if (curLoc.y - (_shell.getSize()).y < 0 || curLoc.x != startX) {
          _activeShells.remove(shell);
          shell.dispose();
          continue;
        } 
        shell.setLocation(curLoc.x, curLoc.y - (_shell.getSize()).y);
      } 
    } 
    _shell.setLocation(startX, startY);
    _shell.setVisible(true);
    _shell.setAlpha(0);
    _activeShells.add(_shell);
    fadeIn(_shell, fade);
  }
  
  private static void createRightTextSection(String title, String message, String option, Composite inner, IHyperlinkListener hyperlinkListener) {
    Composite rightComposit = new Composite(inner, 0);
    GridData data = new GridData(1808);
    data.widthHint = 210;
    rightComposit.setLayoutData(data);
    GridLayout glRight = new GridLayout(1, false);
    rightComposit.setLayout((Layout)glRight);
    Label titleLabel = new Label(rightComposit, 0);
    titleLabel.setLayoutData(new GridData(1844));
    titleLabel.setForeground(_fgColor);
    titleLabel.setText(title);
    titleLabel.setFont(titleFont);
    if (!StringUtil.isEmpty(message)) {
      Label detailLabel = new Label(rightComposit, 64);
      if (!StringUtil.isEmpty(option)) {
        detailLabel.setLayoutData(new GridData(1844));
      } else {
        detailLabel.setLayoutData(new GridData(1840));
      } 
      detailLabel.setForeground(_fgColor);
      detailLabel.setText(message);
      detailLabel.setFont(detailFont);
    } 
    if (!StringUtil.isEmpty(option)) {
      CLabel optionLabel = new CLabel(rightComposit, 0);
      optionLabel.setLayoutData(new GridData(1844));
      RowLayout rowLayout = new RowLayout();
      rowLayout.marginLeft = 2;
      optionLabel.setLayout((Layout)rowLayout);
      Hyperlink optionHyper = new Hyperlink((Composite)optionLabel, 64);
      optionHyper.setText(option);
      optionHyper.setUnderlined(true);
      optionHyper.setForeground(_fgOptionColor);
      optionHyper.setFont(detailFont);
      if (hyperlinkListener != null)
        optionHyper.addHyperlinkListener(hyperlinkListener); 
    } 
  }
  
  private static void createLeftIconSection(NotificationIconType type, Composite inner) {
    CLabel imgLabel = new CLabel(inner, 0);
    imgLabel.setLayoutData(new GridData(68));
    imgLabel.setImage(type.getImage());
  }
  
  private static void fadeIn(final Shell _shell, final boolean fade) {
    Runnable run = new Runnable() {
        public void run() {
          try {
            if (_shell == null || _shell.isDisposed())
              return; 
            int cur = _shell.getAlpha();
            cur += 30;
            if (cur > 229) {
              _shell.setAlpha(229);
              if (fade)
                NotificationTrayPopup.startTimer(_shell); 
              return;
            } 
            _shell.setAlpha(cur);
            SWTUtil.getDisplay().timerExec(100, this);
          } catch (Exception err) {
            NotificationTrayPopup.logger.warn("Excetion occurred during fadein", err);
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(100, run);
  }
  
  private static void startTimer(final Shell _shell) {
    Runnable run = new Runnable() {
        public void run() {
          try {
            if (_shell == null || _shell.isDisposed())
              return; 
            NotificationTrayPopup.fadeOut(_shell);
          } catch (Exception err) {
            NotificationTrayPopup.logger.warn("Excetion occurred during startTimer", err);
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(3000, run);
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
            this.fadeValue -= 10;
            if (this.fadeValue <= 0) {
              _shell.setVisible(false);
              _shell.dispose();
              NotificationTrayPopup._activeShells.remove(_shell);
              return;
            } 
            _shell.setAlpha(this.fadeValue);
            SWTUtil.getDisplay().timerExec(100, this);
          } catch (Exception err) {
            NotificationTrayPopup.logger.warn("Excetion occurred during fadeOut", err);
          } 
        }
      };
    SWTUtil.getDisplay().timerExec(100, run);
  }
}
