package org.tizen.common.util;

import java.net.URI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.internal.filesystem.local.LocalFileNativesManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.ide.dialogs.IDEResourceInfoUtils;
import org.eclipse.ui.internal.ide.dialogs.IFileStoreFilter;
import org.tizen.common.Surrogate;
import org.tizen.common.core.application.Messages;

public class EFSUtil {
  public static final int OWNER_READ_FLAG = 256;
  
  public static final int OWNER_WRITE_FLAG = 128;
  
  public static final int OWNER_EXECUTE_FLAG = 64;
  
  public static final int GROUP_READ_FLAG = 32;
  
  public static final int GROUP_WRITE_FLAG = 16;
  
  public static final int GROUP_EXECUTE_FLAG = 8;
  
  public static final int OTHERS_READ_FLAG = 4;
  
  public static final int OTHERS_WRITE_FLAG = 2;
  
  public static final int OTHERS_EXECUTE_FLAG = 1;
  
  public enum DialogChoicer {
    YesToAll, NoToAll, Yes, No, Cancel;
    
    public static String[] toStringArray() {
      DialogChoicer[] choicers = values();
      String[] values = new String[choicers.length];
      byte b;
      int i;
      DialogChoicer[] arrayOfDialogChoicer1;
      for (i = (arrayOfDialogChoicer1 = choicers).length, b = 0; b < i; ) {
        DialogChoicer choicer = arrayOfDialogChoicer1[b];
        values[choicer.ordinal()] = choicer.name();
        b++;
      } 
      return values;
    }
    
    public static DialogChoicer valueFromOrdinal(int ordinal) {
      byte b;
      int i;
      DialogChoicer[] arrayOfDialogChoicer;
      for (i = (arrayOfDialogChoicer = values()).length, b = 0; b < i; ) {
        DialogChoicer choicer = arrayOfDialogChoicer[b];
        if (choicer.ordinal() == ordinal)
          return choicer; 
        b++;
      } 
      return Cancel;
    }
  }
  
  private static Surrogate<IFileSystem> fileSystemSurrogate = new Surrogate<IFileSystem>() {
      public IFileSystem getAdapter() {
        return EFS.getLocalFileSystem();
      }
    };
  
  private static final String defaultTask = "";
  
  private static final int defaultTicks = 100;
  
  private static final IFileStoreFilter defaultFilter = new IFileStoreFilter() {
      public boolean accept(IFileStore store) {
        return true;
      }
    };
  
  public static void setFileSystemSurrogate(Surrogate<IFileSystem> surrogate) {
    fileSystemSurrogate = surrogate;
  }
  
  protected static IFileSystem getFileSystem() {
    return fileSystemSurrogate.getAdapter();
  }
  
  public static int getPermissions(String path) {
    FileInfo fileInfo = LocalFileNativesManager.fetchFileInfo(path);
    int permissions = 0;
    if (fileInfo.exists()) {
      permissions |= fileInfo.getAttribute(4194304) ? 256 : 0;
      permissions |= fileInfo.getAttribute(8388608) ? 128 : 0;
      permissions |= fileInfo.getAttribute(16777216) ? 64 : 0;
      permissions |= fileInfo.getAttribute(33554432) ? 32 : 0;
      permissions |= fileInfo.getAttribute(67108864) ? 16 : 0;
      permissions |= fileInfo.getAttribute(134217728) ? 8 : 0;
      permissions |= fileInfo.getAttribute(268435456) ? 4 : 0;
      permissions |= fileInfo.getAttribute(536870912) ? 2 : 0;
      permissions |= fileInfo.getAttribute(1073741824) ? 1 : 0;
    } 
    return permissions;
  }
  
  public static IFileStoreFilter getDefaultFileStoreFilter() {
    return defaultFilter;
  }
  
  public static boolean isExistResource(URI fileURI) {
    IFileStore fileStore = getFileSystem().getStore(fileURI);
    return isExistResource(fileStore);
  }
  
  public static boolean isExistResource(IFileStore fileStore) {
    return (fileStore != null) ? fileStore.fetchInfo().exists() : false;
  }
  
  public static DialogChoicer isExistResourceWithDialog(Shell parentShell, String title, String msg, DialogChoicer previousChoice, IFileStore fileStore) {
    Assert.notNull(parentShell);
    Assert.notNull(msg);
    Assert.notNull(previousChoice);
    Assert.notNull(fileStore);
    switch (previousChoice) {
      case Yes:
      case No:
        break;
      default:
        return previousChoice;
    } 
    if (isExistResource(fileStore)) {
      MessageDialog msgDialog = new MessageDialog(parentShell, 
          title, null, msg, 0, DialogChoicer.toStringArray(), 
          DialogChoicer.Cancel.ordinal());
      int msgResult = msgDialog.open();
      return DialogChoicer.valueFromOrdinal(msgResult);
    } 
    return DialogChoicer.Yes;
  }
  
  public static void copy(URI source, URI destination, int options, IProgressMonitor monitor) throws CoreException {
    Assert.notNull(source);
    Assert.notNull(destination);
    monitor = ObjectUtil.<IProgressMonitor>nvl(new IProgressMonitor[] { monitor, (IProgressMonitor)new NullProgressMonitor() });
    try {
      monitor.beginTask("", 100);
      getFileSystem().getStore(source).copy(getFileSystem().getStore(destination), options, monitor);
    } finally {
      monitor.done();
    } 
  }
  
  public static DialogChoicer copyWithFilter(URI source, URI destination, IFileStoreFilter fileFilter, IProgressMonitor monitor) throws CoreException {
    return copyWithFilter(source, destination, fileFilter, DialogChoicer.Yes, monitor);
  }
  
  public static DialogChoicer copyWithFilter(URI source, URI destination, IFileStoreFilter fileFilter, DialogChoicer previousChoice, IProgressMonitor monitor) throws CoreException {
    Assert.notNull(source);
    Assert.notNull(destination);
    monitor = ObjectUtil.<IProgressMonitor>nvl(new IProgressMonitor[] { monitor, (IProgressMonitor)new NullProgressMonitor() });
    try {
      monitor.beginTask("", 100);
      if (fileFilter == null)
        fileFilter = getDefaultFileStoreFilter(); 
      return internalCopyWithFilter(getFileSystem().getStore(source), 
          getFileSystem().getStore(destination), 
          fileFilter, previousChoice, monitor);
    } finally {
      monitor.done();
    } 
  }
  
  private static DialogChoicer internalCopyWithFilter(IFileStore source, IFileStore destination, IFileStoreFilter fileFilter, DialogChoicer previousChoice, IProgressMonitor monitor) throws CoreException {
    Assert.notNull(source);
    Assert.notNull(destination);
    IWorkbenchWindow window = ViewUtil.getWorkbenchWindow();
    if (window == null)
      throw new OperationCanceledException("Could not find a window"); 
    previousChoice = isExistResourceWithDialog(
        window.getShell(), Messages.FileExistDialogTitle, 
        Messages.bind(Messages.FileExistMsg, destination.getName()), 
        previousChoice, destination);
    switch (previousChoice) {
      case YesToAll:
      case Yes:
        source.copy(destination, 6, monitor);
        break;
      case null:
        throw new OperationCanceledException();
    } 
    monitor.worked(1);
    if (source.fetchInfo().isDirectory()) {
      IFileStore[] childStores = IDEResourceInfoUtils.listFileStores(source, fileFilter, null);
      byte b;
      int i;
      IFileStore[] arrayOfIFileStore1;
      for (i = (arrayOfIFileStore1 = childStores).length, b = 0; b < i; ) {
        IFileStore childStore = arrayOfIFileStore1[b];
        previousChoice = internalCopyWithFilter(childStore, 
            destination.getChild(childStore.getName()), 
            fileFilter, previousChoice, monitor);
        b++;
      } 
    } 
    return previousChoice;
  }
}
