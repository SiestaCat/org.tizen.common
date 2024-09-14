package org.tizen.common.ui.widget;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class PictureLabel extends Canvas {
  private Image image;
  
  private String text;
  
  private boolean selected;
  
  private List<ImageClickedListener> imageClickedListeners = new ArrayList<>();
  
  public PictureLabel(Composite parent, int style) {
    super(parent, style);
    setBackground(getDisplay().getSystemColor(1));
    addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {}
        });
    addPaintListener(new PaintListener() {
          public void paintControl(PaintEvent e) {
            PictureLabel.this.paintControl(e);
          }
        });
    addMouseListener((MouseListener)new MouseAdapter() {
          public void mouseDown(MouseEvent event) {
            if (event.button == 1)
              PictureLabel.this.mouseDown(event); 
          }
        });
    addMouseTrackListener((MouseTrackListener)new MouseTrackAdapter() {
          public void mouseEnter(MouseEvent event) {
            PictureLabel.this.mouseEnter(event);
          }
          
          public void mouseExit(MouseEvent event) {
            PictureLabel.this.mouseExit(event);
          }
        });
    setSelection(false);
  }
  
  public void mouseDown(MouseEvent event) {
    ImageClickedEvent e = new ImageClickedEvent(this, event.x, event.y);
    setSelection(!this.selected);
    for (ImageClickedListener listener : this.imageClickedListeners)
      listener.imageClicked(e); 
  }
  
  protected void mouseEnter(MouseEvent event) {
    setBackground(getDisplay().getSystemColor(19));
  }
  
  protected void mouseExit(MouseEvent event) {
    setBackground(getDisplay().getSystemColor(this.selected ? 15 : 1));
  }
  
  public void setSelection(boolean selected) {
    this.selected = selected;
    setBackground(getDisplay().getSystemColor(selected ? 15 : 1));
  }
  
  public void addImageClickedListener(ImageClickedListener listener) {
    this.imageClickedListeners.add(listener);
  }
  
  public void removeImageClickedListener(ImageClickedListener listener) {
    this.imageClickedListeners.remove(listener);
  }
  
  void paintControl(PaintEvent e) {
    GC gc = e.gc;
    int x = 1;
    int y = 1;
    int clientAreaWidth = (getClientArea()).width;
    int imageWidth = (this.image != null) ? (this.image.getBounds()).width : 0;
    int imageHeght = (this.image != null) ? (this.image.getBounds()).height : 0;
    int maxWidth = Math.max(Math.max(imageWidth, imageWidth), clientAreaWidth);
    if ((((this.image != null) ? 1 : 0) & ((this.text != null) ? 1 : 0)) != 0) {
      int textWidth = (gc.stringExtent(this.text)).x;
      x = 1 + (maxWidth - imageWidth) / 2;
      gc.drawImage(this.image, x, y);
      y = y + imageHeght + 5;
      x = 1 + (maxWidth - textWidth) / 2;
      gc.drawText(this.text, x, y);
    } else if (this.image != null) {
      x = 1 + (maxWidth - imageWidth) / 2;
      gc.drawImage(this.image, x, 1);
    } else if (this.text != null) {
      int textWidth = (gc.stringExtent(this.text)).x;
      x = 1 + (maxWidth - textWidth) / 2;
      gc.drawText(this.text, x, 1);
    } 
    gc.dispose();
  }
  
  public Image getImage() {
    return this.image;
  }
  
  public void setImage(Image image) {
    this.image = image;
    redraw();
  }
  
  public String getText() {
    return this.text;
  }
  
  public void setText(String text) {
    this.text = text;
    redraw();
  }
  
  public Point computeSize(int wHint, int hHint, boolean changed) {
    int width = 0;
    int height = 0;
    if (this.image != null && this.text != null) {
      GC gc = new GC((Drawable)this);
      int textWidth = (gc.stringExtent(this.text)).x;
      int textHeight = (gc.stringExtent(this.text)).y;
      int imageWidth = (this.image.getBounds()).width;
      int imageHeight = (this.image.getBounds()).height;
      gc.dispose();
      if (imageWidth >= textWidth) {
        width = imageWidth + 2;
      } else {
        width = textWidth + 2;
      } 
      height = textHeight + imageHeight + 7;
    } else if (this.image != null) {
      Rectangle bounds = this.image.getBounds();
      width = bounds.width + 2;
      height = bounds.height + 2;
    } else if (this.text != null) {
      GC gc = new GC((Drawable)this);
      Point extent = gc.stringExtent(this.text);
      gc.dispose();
      width = extent.x + 2;
      height = extent.y + 2;
    } 
    if (wHint != -1)
      width = wHint; 
    if (hHint != -1)
      height = hHint; 
    return new Point(width + 2, height + 2);
  }
  
  public boolean isSelected() {
    return this.selected;
  }
}
