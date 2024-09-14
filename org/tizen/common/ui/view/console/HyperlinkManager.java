package org.tizen.common.ui.view.console;

import java.util.Iterator;
import java.util.TreeSet;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.console.TextConsole;

public class HyperlinkManager implements IHyperlinkManager, IDocumentListener {
  protected TextConsole console;
  
  protected final TreeSet<LinkInfo> dangledLinks = new TreeSet<>();
  
  public HyperlinkManager(TextConsole console) {
    this.console = console;
  }
  
  public void documentAboutToBeChanged(DocumentEvent event) {}
  
  public void documentChanged(DocumentEvent event) {
    IDocument doc = event.getDocument();
    int docLen = doc.getLength();
    for (Iterator<LinkInfo> iter = this.dangledLinks.iterator(); iter.hasNext(); ) {
      LinkInfo info = iter.next();
      if (docLen < info.getEnd())
        return; 
      try {
        this.console.addHyperlink(info.getLink(), info.getStart(), info.getLength());
        iter.remove();
      } catch (BadLocationException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public void addLinker(LinkInfo link) {
    this.dangledLinks.add(link);
  }
}
