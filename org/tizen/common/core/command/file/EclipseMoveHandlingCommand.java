package org.tizen.common.core.command.file;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Stack;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.core.command.Policy;
import org.tizen.common.core.command.policy.OptionPolicy;
import org.tizen.common.core.command.prompter.FileHandlingOption;
import org.tizen.common.core.command.prompter.Option;
import org.tizen.common.core.command.prompter.RunnableOption;
import org.tizen.common.file.FileHandler;

public class EclipseMoveHandlingCommand extends FileHandlingCommand<Boolean> {
  private static Logger logger = LoggerFactory.getLogger(EclipseMoveHandlingCommand.class);
  
  private List<MoveFile> moveFileList;
  
  private FileHandlingOption selectedAllFlagOption;
  
  public EclipseMoveHandlingCommand(List<MoveFile> moveFileList) {
    this.moveFileList = moveFileList;
  }
  
  protected boolean isExcludedFile(IResource sourceFile, File targetFile) {
    return false;
  }
  
  public void setOverwriteOption(FileHandlingOption opt) {
    this.selectedAllFlagOption = opt;
  }
  
  protected FileHandlingOption getOverwriteOption() {
    return this.selectedAllFlagOption;
  }
  
  protected boolean isCopyChildren() {
    return false;
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    FileHandler handler = context.getFileHandler();
    Policy policy = context.getPolicy("exist.file.when.copy");
    if (policy == null)
      return; 
    OptionPolicy optionPolicy = policy.<OptionPolicy>adapt(OptionPolicy.class);
    if (optionPolicy == null)
      return; 
    Option[] options = optionPolicy.getOptions();
    byte b;
    int i;
    Option[] arrayOfOption1;
    for (i = (arrayOfOption1 = options).length, b = 0; b < i; ) {
      Option option = arrayOfOption1[b];
      if (!(option instanceof FileHandlingOption))
        throw new IllegalArgumentException(String.format("Option %s is not a FileHandlingOption", new Object[] { option.getName() })); 
      ((FileHandlingOption)option).setHandler(handler);
      b++;
    } 
    for (MoveFile moveFile : this.moveFileList) {
      String sourcePath = moveFile.source;
      String targetPath = moveFile.target;
      IResource member = moveFile.project.findMember(sourcePath);
      if (member == null || !member.exists())
        continue; 
      Stack<IResource> sourceStack = new Stack<>();
      Stack<File> targetStack = new Stack<>();
      sourceStack.push(member);
      targetStack.push(new File(targetPath));
      while (!sourceStack.isEmpty()) {
        IResource sourceFile = sourceStack.pop();
        File targetFile = targetStack.pop();
        if (sourceFile.getType() == 2) {
          IFolder folder = (IFolder)sourceFile;
          handler.makeDirectory(targetFile.getPath());
          if (isCopyChildren()) {
            byte b1;
            int j;
            IResource[] arrayOfIResource;
            for (j = (arrayOfIResource = folder.members()).length, b1 = 0; b1 < j; ) {
              IResource sourceChild = arrayOfIResource[b1];
              sourceStack.push(sourceChild);
              targetStack.push(new File(targetFile, sourceChild.getName()));
              b1++;
            } 
          } 
          continue;
        } 
        if (sourceFile.getType() == 1) {
          if (isExcludedFile(sourceFile, targetFile))
            continue; 
          if (targetFile.exists()) {
            RunnableOption resultOption = null;
            FileHandlingOption selectedAllFlagOption = getOverwriteOption();
            if (selectedAllFlagOption == null) {
              resultOption = (RunnableOption)context.getPrompter().interact(String.valueOf(targetFile.getCanonicalPath()) + " already exists", options);
            } else {
              resultOption = selectedAllFlagOption;
            } 
            if (resultOption.isAllFlag())
              setOverwriteOption((FileHandlingOption)resultOption); 
            if (!resultOption.equals(FileHandlingOption.IGNORE) && !resultOption.equals(FileHandlingOption.IGNORE_ALL))
              if (!resultOption.equals(FileHandlingOption.OVERWRITE) && !resultOption.equals(FileHandlingOption.OVERWRITE_ALL)) {
                resultOption.setArgument(new Object[] { sourceFile.getLocation().toFile(), targetFile });
                resultOption.run();
              }  
          } 
          if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists())
            targetFile.getParentFile().mkdirs(); 
          Files.move((new File(sourceFile.getLocation().toOSString())).toPath(), (new File(targetFile.getPath())).toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
          continue;
        } 
        logger.warn(String.format("%s is not a file or directory", new Object[] { sourceFile }));
      } 
      moveFile.project.refreshLocal(2, null);
    } 
  }
  
  public static class MoveFile {
    private IProject project;
    
    private String source;
    
    private String target;
    
    public MoveFile(IProject project, String source, String target) {
      this.project = project;
      this.source = source;
      this.target = target;
    }
  }
}
