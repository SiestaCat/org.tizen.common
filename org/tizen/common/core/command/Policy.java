package org.tizen.common.core.command;

import org.tizen.common.Adaptable;

public interface Policy extends Adaptable {
  public static final String NONEXIST_IN_FILE = "nonexist.file.in";
  
  public static final String NONEXIST_IN_FILE_SIGN_PROFILE = "nonexist.file.in.signprofile";
  
  public static final String NONEXIST_IN_DIRECTORY = "nonexist.dir.in";
  
  public static final String NONEXIST_IN_PROJECT = "nonexist.dir.in.project";
  
  public static final String EXIST_OUT_FILE = "exist.file.out";
  
  public static final String EXIST_OUT_WGT = "exist.file.out.wgt";
  
  public static final String EXIST_FILE_WHEN_COPY = "exist.file.when.copy";
  
  public static final String EXCEPTION_UNHANDLED = "exception.unhandled";
  
  public static final String EXCEPTION_OUT_WGT = "exception.file.out.wgt";
  
  public static final String PRINTOUT_RESULT_SIGNING = "printout.result.signing";
  
  String getName();
}
