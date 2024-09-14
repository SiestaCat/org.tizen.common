package org.tizen.common.rds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.FileUtil;

public class RdsDeltaListFile {
  private final Logger logger = LoggerFactory.getLogger(RdsDeltaListFile.class);
  
  public static final String RDS_DELTA_LIST_FILE_NAME = ".rds_delta";
  
  public static final String ADD_TITLE = "#add";
  
  public static final String MODIFY_TITLE = "#modify";
  
  public static final String DELETE_TITLE = "#delete";
  
  List<String> addList = new ArrayList<>();
  
  List<String> modifyList = new ArrayList<>();
  
  List<String> deleteList = new ArrayList<>();
  
  public void addAddDelta(String addDelta) {
    this.addList.add(addDelta);
  }
  
  public void addModifyDelta(String addDelta) {
    this.modifyList.add(addDelta);
  }
  
  public void addDeleteDelta(String addDelta) {
    this.deleteList.add(addDelta);
  }
  
  public File makeFile(String parentDirectory) {
    if (this.addList.isEmpty() && this.modifyList.isEmpty() && this.deleteList.isEmpty())
      return null; 
    StringBuilder strBuilder = new StringBuilder("#delete\n");
    for (String delete : this.deleteList)
      strBuilder.append(String.valueOf(delete) + "\n"); 
    strBuilder.append("#add\n");
    for (String add : this.addList)
      strBuilder.append(String.valueOf(add) + "\n"); 
    strBuilder.append("#modify\n");
    for (String modify : this.modifyList)
      strBuilder.append(String.valueOf(modify) + "\n"); 
    File file = new File(String.valueOf(parentDirectory) + ".rds_delta");
    try {
      FileUtil.writeTextFile(file, strBuilder.toString(), null);
    } catch (IOException e) {
      this.logger.error("Cannot write delta info in " + parentDirectory + "/" + ".rds_delta", e);
      if (file.exists())
        file.delete(); 
      return null;
    } 
    return file;
  }
}
