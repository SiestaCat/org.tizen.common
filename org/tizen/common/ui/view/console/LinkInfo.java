package org.tizen.common.ui.view.console;

import org.eclipse.ui.console.IHyperlink;

public class LinkInfo implements Comparable<LinkInfo> {
  protected final int offset;
  
  protected final int length;
  
  protected final IHyperlink link;
  
  public LinkInfo(int offset, int length, IHyperlink link) {
    this.offset = offset;
    this.length = length;
    this.link = link;
    if (length < 0)
      throw new IllegalArgumentException("Link's length is negative :" + this); 
  }
  
  public int getStart() {
    return this.offset;
  }
  
  public int getEnd() {
    return getStart() + getLength();
  }
  
  public int getLength() {
    return this.length;
  }
  
  public IHyperlink getLink() {
    return this.link;
  }
  
  public int compareTo(LinkInfo o) {
    return getLength() - o.getLength();
  }
  
  public String toString() {
    return "Link(" + getStart() + ":" + getEnd() + ")-" + this.link;
  }
}
