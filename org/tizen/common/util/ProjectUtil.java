package org.tizen.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.ITizenNativeProject;
import org.tizen.common.ITizenProject;
import org.tizen.common.ITizenProjectAdapter;
import org.tizen.common.ITizenWebProject;
import org.tizen.common.TizenProjectType;
import org.tizen.common.core.application.ProfileInfo;
import org.tizen.common.core.application.TizenProjectDescription;

public class ProjectUtil {
  private static final Logger logger = LoggerFactory.getLogger(ProjectUtil.class);
  
  public static IProjectDescription getDescription(IProject project) throws CoreException {
    return (project != null) ? project.getDescription() : null;
  }
  
  public static ICommand[] getBuildSpec(IProject project) throws CoreException {
    IProjectDescription description = getDescription(project);
    return (description != null) ? description.getBuildSpec() : new ICommand[0];
  }
  
  public static void setBuildSpec(IProject project, ICommand[] commands, IProgressMonitor monitor) throws CoreException {
    IProjectDescription description = getDescription(project);
    if (description != null) {
      description.setBuildSpec(commands);
      project.setDescription(description, monitor);
    } 
  }
  
  public static ICommand[] addCommand(ICommand[] oldCommands, ICommand newCommand) {
    ICommand[] newCommands;
    if (newCommand == null) {
      if (oldCommands == null)
        return new ICommand[0]; 
      return oldCommands;
    } 
    if (oldCommands != null) {
      newCommands = new ICommand[oldCommands.length + 1];
      System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
    } else {
      newCommands = new ICommand[1];
    } 
    newCommands[newCommands.length - 1] = newCommand;
    return newCommands;
  }
  
  public static ICommand findCommand(ICommand[] commands, String builderID) {
    if (commands != null) {
      byte b;
      int i;
      ICommand[] arrayOfICommand;
      for (i = (arrayOfICommand = commands).length, b = 0; b < i; ) {
        ICommand command = arrayOfICommand[b];
        String builderName = command.getBuilderName();
        if (builderName != null && builderName.equals(builderID))
          return command; 
        b++;
      } 
    } 
    return null;
  }
  
  public static ICommand newCommand(IProject project) throws CoreException {
    IProjectDescription description = getDescription(project);
    return (description != null) ? description.newCommand() : null;
  }
  
  public static void setBuildCommandWithArgument(IProject project, String builderID, Map<String, String> args, IProgressMonitor monitor) {
    if (builderID == null)
      return; 
    try {
      ICommand[] commands = getBuildSpec(project);
      ICommand findedCommand = findCommand(commands, builderID);
      if (findedCommand != null)
        return; 
      ICommand command = newCommand(project);
      if (command == null)
        return; 
      command.setBuilderName(builderID);
      command.setArguments(args);
      ICommand[] newCommands = addCommand(commands, command);
      setBuildSpec(project, newCommands, monitor);
    } catch (CoreException e) {
      logger.error("Failed to setBuildSpec", (Throwable)e);
    } 
  }
  
  public static Map<String, String> getBuildCommandArgument(IProject project, String builderID) {
    try {
      ICommand[] commands = getBuildSpec(project);
      ICommand command = findCommand(commands, builderID);
      if (command != null)
        return command.getArguments(); 
    } catch (CoreException e) {
      logger.error("Failed to getBuildSpec", (Throwable)e);
    } 
    return null;
  }
  
  public static boolean isDefaultProjectLocation(IPath descriptionLocation) {
    if (descriptionLocation.segmentCount() < 2)
      return false; 
    IPath location = Platform.getLocation();
    if (location == null)
      return false; 
    return descriptionLocation.removeLastSegments(2).toFile().equals(location.toFile());
  }
  
  public static ITizenProject getTizenProject(IProject project, Class<? extends ITizenProject> adapter) {
    Assert.notNull(project, "Project can not be null");
    Object tizenProject = AdapterUtil.getAdapter((IAdaptable)project, adapter);
    if (tizenProject instanceof ITizenProject)
      return (ITizenProject)tizenProject; 
    logger.warn("Failed to get adapter of " + project + " (adapterType - " + adapter + ")");
    return null;
  }
  
  public static ITizenProject getTizenProject(IProject project) {
    ITizenProject tizenProject = null;
    IExtensionRegistry x = RegistryFactory.getRegistry();
    IConfigurationElement[] ces = x.getConfigurationElementsFor("org.tizen.common.project.adapter");
    byte b;
    int i;
    IConfigurationElement[] arrayOfIConfigurationElement1;
    for (i = (arrayOfIConfigurationElement1 = ces).length, b = 0; b < i; ) {
      IConfigurationElement ce = arrayOfIConfigurationElement1[b];
      if ("adapter".equals(ce.getName())) {
        String className = ce.getAttribute("class");
        if (className != null)
          try {
            Object obj = ce.createExecutableExtension("class");
            if (obj instanceof ITizenProjectAdapter) {
              ITizenProjectAdapter adapter = (ITizenProjectAdapter)obj;
              if (adapter.canHandle(project)) {
                tizenProject = adapter.getTizenProject(project);
                if (tizenProject != null)
                  break; 
              } 
            } 
          } catch (CoreException e) {
            logger.warn("cannot get adapter of " + project + " from extension", (Throwable)e);
          }  
      } 
      b++;
    } 
    return tizenProject;
  }
  
  public static TizenProjectDescription getTizenProjectDescription(IProject project) {
    ITizenProject tizenProject = getTizenProject(project);
    return (tizenProject == null) ? null : tizenProject.getDescription();
  }
  
  public static boolean createTizenProjectDescription(IProject project, ProfileInfo platformInfo) {
    return createTizenProjectDescription(project, platformInfo, false);
  }
  
  public static boolean updateTizenProjectDescription(IProject project, ProfileInfo platformInfo) {
    return createTizenProjectDescription(project, platformInfo, true);
  }
  
  protected static boolean createTizenProjectDescription(IProject project, ProfileInfo platformInfo, boolean bUpdate) {
    Assert.notNull(project, "project can not be null");
    Assert.notNull(platformInfo, "platformInfo can not be null");
    ITizenProject tizenPrj = getTizenProject(project);
    if (tizenPrj != null) {
      TizenProjectDescription description = bUpdate ? tizenPrj.getDescription() : new TizenProjectDescription(project);
      if (description != null) {
        description.setPlatform(platformInfo);
        return tizenPrj.setDescription(description);
      } 
    } 
    return false;
  }
  
  public static TizenProjectType getTizenProjectType(IProject project) {
    ITizenProject tProject = getTizenProject(project);
    return (tProject == null) ? null : tProject.getTizenProjectType();
  }
  
  public static TizenProjectType getTizenProjectType(IProject project, Class<? extends ITizenProject> adapter) {
    ITizenProject tProject = getTizenProject(project, adapter);
    return (tProject == null) ? null : tProject.getTizenProjectType();
  }
  
  public static boolean isTizenNativeProject(IProject project) {
    TizenProjectType type = getTizenProjectType(project, (Class)ITizenNativeProject.class);
    return (type == null) ? false : type.isNativeProject();
  }
  
  public static boolean isTizenWebProject(IProject project) {
    TizenProjectType type = getTizenProjectType(project, (Class)ITizenWebProject.class);
    return (type == null) ? false : type.isWebProject();
  }
  
  public static boolean isTizenProject(IProject project) {
    return !(!isTizenNativeProject(project) && !isTizenWebProject(project));
  }
  
  public static IProject getProject(String projectName) {
    IProject proj = null;
    if (projectName != null)
      proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName); 
    return proj;
  }
  
  public static IProject getProject(IPath fullPath) {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProject[] projects = workspace.getRoot().getProjects();
    IProject project = null;
    byte b;
    int i;
    IProject[] arrayOfIProject1;
    for (i = (arrayOfIProject1 = projects).length, b = 0; b < i; ) {
      IProject p = arrayOfIProject1[b];
      if (p.getName().equals(fullPath.toString().substring(1))) {
        project = p;
        break;
      } 
      b++;
    } 
    return project;
  }
  
  public static int getMaxProblemSeverity(IResource resource, boolean isAllRefPrj) {
    int maxSeverity = -1;
    int tempSeverity = -1;
    if (resource == null)
      return maxSeverity; 
    try {
      IProject project = resource.getProject();
      maxSeverity = resource.findMaxProblemSeverity("org.eclipse.core.resources.problemmarker", true, 2);
      IProject[] refProjects = null;
      if (isAllRefPrj) {
        refProjects = project.getReferencedProjects();
      } else {
        refProjects = getReferencedProjects(project).<IProject>toArray(new IProject[0]);
      } 
      byte b;
      int i;
      IProject[] arrayOfIProject1;
      for (i = (arrayOfIProject1 = refProjects).length, b = 0; b < i; ) {
        IProject refProject = arrayOfIProject1[b];
        if (refProject != null) {
          tempSeverity = refProject.findMaxProblemSeverity("org.eclipse.core.resources.problemmarker", true, 2);
          if (tempSeverity > maxSeverity)
            maxSeverity = tempSeverity; 
        } 
        b++;
      } 
    } catch (CoreException e) {
      logger.error("cannot find max problem severity", (Throwable)e);
      return -1;
    } 
    return maxSeverity;
  }
  
  public static List<IProject> getReferencedProjects(IProject targetProject) {
    List<IProject> refProject = new ArrayList<>();
    TizenProjectDescription targetTpd = getTizenProjectDescription(targetProject);
    if (targetTpd == null)
      return refProject; 
    List<TizenProjectDescription.RefTizenProject> subProjects = targetTpd.getSubProjectList();
    IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (TizenProjectDescription.RefTizenProject rtp : subProjects) {
      byte b;
      int i;
      IProject[] arrayOfIProject;
      for (i = (arrayOfIProject = workspaceProjects).length, b = 0; b < i; ) {
        IProject workspaceProject = arrayOfIProject[b];
        if (rtp.getName().equals(workspaceProject.getName())) {
          ITizenProject workspaceTproject = getTizenProject(workspaceProject);
          if (workspaceTproject != null) {
            TizenProjectType workspaceProjectType = workspaceTproject.getTizenProjectType();
            if (workspaceProjectType != null && workspaceProjectType.isReferencedProject(getTizenProjectType(targetProject)))
              refProject.add(workspaceProject); 
          } 
        } 
        b++;
      } 
    } 
    return refProject;
  }
  
  public static boolean isSupportedPlatform(TizenProjectDescription desc) {
    if (desc == null)
      return false; 
    return desc.isSupportedPlatform();
  }
  
  public static IProject getProjectOnEditor() {
    IEditorPart activeEditor = SWTUtil.getActiveEditor();
    if (activeEditor == null)
      return null; 
    IEditorInput editorInput = activeEditor.getEditorInput();
    if (editorInput instanceof IFileEditorInput) {
      IFile currentEditingFile = ((IFileEditorInput)editorInput).getFile();
      return currentEditingFile.getProject();
    } 
    return null;
  }
  
  public static IProject getProject(ExecutionEvent event) {
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    if (selection == null || selection.isEmpty())
      return null; 
    if (!(selection instanceof ITreeSelection))
      return null; 
    ITreeSelection treeSelection = (ITreeSelection)selection;
    TreePath[] treePaths = treeSelection.getPaths();
    Object firstSegment = treePaths[0].getFirstSegment();
    if (firstSegment instanceof IProject)
      return (IProject)firstSegment; 
    return null;
  }
}
