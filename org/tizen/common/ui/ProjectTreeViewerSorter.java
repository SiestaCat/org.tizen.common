package org.tizen.common.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ViewerSorter;

public class ProjectTreeViewerSorter extends ViewerSorter {
  public int category(Object element) {
    if (element instanceof IResource)
      return 100 / ((IResource)element).getType(); 
    return super.category(element);
  }
}
