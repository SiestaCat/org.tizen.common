package org.tizen.common.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.WorkingSetDescriptor;
import org.eclipse.ui.internal.registry.WorkingSetRegistry;

public class WorkspaceUtil {
  public static IWorkspace getWorkspace() {
    return ResourcesPlugin.getWorkspace();
  }
  
  public static IProject[] getProjects() {
    return getWorkspace().getRoot().getProjects();
  }
  
  public static IProject[] getTizenWebProjects() {
    List<IProject> tizenWebProjects = new ArrayList<>();
    byte b;
    int i;
    IProject[] arrayOfIProject;
    for (i = (arrayOfIProject = getProjects()).length, b = 0; b < i; ) {
      IProject project = arrayOfIProject[b];
      if (ProjectUtil.isTizenWebProject(project))
        tizenWebProjects.add(project); 
      b++;
    } 
    return tizenWebProjects.<IProject>toArray(new IProject[0]);
  }
  
  public static WorkingSetRegistry getWorkingSetRegistry() {
    return WorkbenchPlugin.getDefault().getWorkingSetRegistry();
  }
  
  public static WorkingSetDescriptor[] getWorkingSetDescriptors() {
    return getWorkingSetRegistry().getNewPageWorkingSetDescriptors();
  }
  
  public static String[] getIdsOfWorkingSetDescriptors() {
    List<String> workingSetList = new ArrayList<>();
    byte b;
    int i;
    WorkingSetDescriptor[] arrayOfWorkingSetDescriptor;
    for (i = (arrayOfWorkingSetDescriptor = getWorkingSetDescriptors()).length, b = 0; b < i; ) {
      WorkingSetDescriptor desc = arrayOfWorkingSetDescriptor[b];
      workingSetList.add(desc.getId());
      b++;
    } 
    return workingSetList.<String>toArray(new String[0]);
  }
  
  public static IWorkingSetManager getWorkingSetManager() {
    return PlatformUI.getWorkbench().getWorkingSetManager();
  }
}
