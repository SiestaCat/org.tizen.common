package org.tizen.common.core.command.file;

import java.io.File;
import java.util.List;
import java.util.Stack;
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

public class DirectoryCopyHandlingCommand extends FileHandlingCommand<Boolean> {
  protected List<String> targetPathList;
  
  protected List<String> sourcePathList;
  
  private FileHandlingOption selectedAllFlagOption;
  
  private static Logger logger = LoggerFactory.getLogger(DirectoryCopyHandlingCommand.class);
  
  public DirectoryCopyHandlingCommand(List<String> sourcePathList, List<String> targetPathList) throws IllegalArgumentException {
    if (sourcePathList.size() != targetPathList.size())
      throw new IllegalArgumentException("Lenghs of sourcePathList and target paths are not same"); 
    this.sourcePathList = sourcePathList;
    this.targetPathList = targetPathList;
  }
  
  protected boolean isExcludedFile(File sourceFile, File targetFile) {
    return false;
  }
  
  public void setOverwriteOption(FileHandlingOption opt) {
    this.selectedAllFlagOption = opt;
  }
  
  protected FileHandlingOption getOverwriteOption() {
    return this.selectedAllFlagOption;
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
    int j;
    Option[] arrayOfOption1;
    for (j = (arrayOfOption1 = options).length, b = 0; b < j; ) {
      Option option = arrayOfOption1[b];
      if (!(option instanceof FileHandlingOption))
        throw new IllegalArgumentException(String.format("Option %s is not a FileHandlingOption", new Object[] { option.getName() })); 
      ((FileHandlingOption)option).setHandler(handler);
      b++;
    } 
    for (int i = 0; i < this.sourcePathList.size(); i++) {
      String sourcePath = this.sourcePathList.get(i);
      String targetPath = this.targetPathList.get(i);
      Stack<File> sourceStack = new Stack<>();
      Stack<File> targetStack = new Stack<>();
      sourceStack.push(new File(sourcePath));
      targetStack.push(new File(targetPath));
      while (!sourceStack.isEmpty()) {
        File sourceFile = sourceStack.pop();
        File targetFile = targetStack.pop();
        if (sourceFile.isDirectory()) {
          handler.makeDirectory(targetFile.getPath());
          File[] sourceChildren = sourceFile.listFiles();
          if (sourceChildren != null) {
            byte b1;
            int k;
            File[] arrayOfFile;
            for (k = (arrayOfFile = sourceChildren).length, b1 = 0; b1 < k; ) {
              File sourceChild = arrayOfFile[b1];
              sourceStack.push(sourceChild);
              targetStack.push(new File(targetFile, sourceChild.getName()));
              b1++;
            } 
          } 
          continue;
        } 
        if (sourceFile.isFile()) {
          if (isExcludedFile(sourceFile, targetFile))
            continue; 
          if (targetFile.exists()) {
            RunnableOption resultOption = null;
            if (this.selectedAllFlagOption == null) {
              resultOption = (RunnableOption)context.getPrompter().interact(String.valueOf(targetFile.getCanonicalPath()) + " already exists", options);
            } else {
              resultOption = this.selectedAllFlagOption;
            } 
            if (resultOption.isAllFlag())
              this.selectedAllFlagOption = (FileHandlingOption)resultOption; 
            resultOption.setArgument(new Object[] { sourceFile, targetFile });
            resultOption.run();
            continue;
          } 
          if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists())
            targetFile.getParentFile().mkdirs(); 
          targetFile.createNewFile();
          handler.copyFile(sourceFile.getPath(), targetFile.getPath());
          continue;
        } 
        logger.warn(String.format("%s is not a file or directory", new Object[] { sourceFile }));
      } 
    } 
  }
}
