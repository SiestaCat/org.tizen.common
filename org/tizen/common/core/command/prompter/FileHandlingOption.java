package org.tizen.common.core.command.prompter;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.CommandCancelException;
import org.tizen.common.file.FileHandler;

public abstract class FileHandlingOption extends RunnableOption {
  protected FileHandler handler;
  
  private static Logger logger = LoggerFactory.getLogger(FileHandlingOption.class);
  
  public FileHandlingOption(String name, String shortName) {
    super(name, shortName);
  }
  
  public void setHandler(FileHandler handler) {
    this.handler = handler;
  }
  
  public static final FileHandlingOption OVERWRITE = new FileHandlingOption("Overwrite", "o") {
      public boolean isDefault() {
        return false;
      }
      
      protected void runWithArgument(Object... args) throws Exception {
        if (args.length != 2)
          throw new IllegalArgumentException("argument length is wrong"); 
        if (!(args[0] instanceof File) || !(args[1] instanceof File))
          throw new IllegalArgumentException("argument type is wrong"); 
        File sourceFile = (File)args[0];
        File targetFile = (File)args[1];
        this.handler.copyFile(sourceFile.getPath(), targetFile.getPath());
      }
    };
  
  public static final FileHandlingOption IGNORE = new FileHandlingOption("Ignore", "i") {
      public boolean isDefault() {
        return true;
      }
      
      protected void runWithArgument(Object... args) throws Exception {
        FileHandlingOption.logger.trace("do nothing");
      }
    };
  
  public static final FileHandlingOption OVERWRITE_ALL = new FileHandlingOption("Overwrite All", "oa") {
      public boolean isDefault() {
        return false;
      }
      
      protected void runWithArgument(Object... args) throws Exception {
        if (args.length != 2)
          throw new IllegalArgumentException("argument length is wrong"); 
        if (!(args[0] instanceof File) || !(args[1] instanceof File))
          throw new IllegalArgumentException("argument type is wrong"); 
        File sourceFile = (File)args[0];
        File targetFile = (File)args[1];
        this.handler.copyFile(sourceFile.getPath(), targetFile.getPath());
      }
      
      public boolean isAllFlag() {
        return true;
      }
    };
  
  public static final FileHandlingOption IGNORE_ALL = new FileHandlingOption("Ignore All", "ia") {
      public boolean isDefault() {
        return false;
      }
      
      protected void runWithArgument(Object... args) throws Exception {
        FileHandlingOption.logger.trace("do nothing");
      }
      
      public boolean isAllFlag() {
        return true;
      }
    };
  
  public static final FileHandlingOption CANCEL = new FileHandlingOption("Cancel", "c") {
      public boolean isDefault() {
        return false;
      }
      
      protected void runWithArgument(Object... args) throws Exception {
        throw new CommandCancelException();
      }
    };
}
