package org.tizen.common.core.command.prompter;

public abstract class RunnableOption extends ChoiceOption {
  private Object[] args;
  
  private boolean allFlag = false;
  
  public RunnableOption(String name) {
    this(name, new Object[] { new Object() });
  }
  
  public RunnableOption(String name, String shortName) {
    super(name, shortName);
    this.shortName = shortName;
  }
  
  public RunnableOption(String name, Object... args) {
    super(name);
    this.args = args;
  }
  
  public void setArgument(Object... args) {
    this.args = args;
  }
  
  protected void setAllFlag(boolean allFlag) {
    this.allFlag = allFlag;
  }
  
  public boolean isAllFlag() {
    return this.allFlag;
  }
  
  protected abstract void runWithArgument(Object... paramVarArgs) throws Exception;
  
  public void run() throws Exception {
    runWithArgument(this.args);
  }
}
