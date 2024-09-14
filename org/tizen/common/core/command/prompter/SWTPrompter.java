package org.tizen.common.core.command.prompter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.tizen.common.core.command.Prompter;
import org.tizen.common.core.command.UserField;
import org.tizen.common.ui.dialog.DynamicDialog;
import org.tizen.common.ui.dialog.PasswordInputDialog;
import org.tizen.common.util.NotificationType;
import org.tizen.common.util.NotifierDialog;
import org.tizen.common.util.SWTRunner;
import org.tizen.common.util.SWTUtil;
import org.tizen.common.util.StringUtil;

public class SWTPrompter extends AbstractPrompter implements Prompter {
  protected Dialog createDialog(String question, String[] optionNames, int defaultOptionIndex) {
    return (Dialog)new MessageDialog(
        SWTUtil.getActiveShell(), 
        "Question", 
        null, 
        question, 
        3, 
        optionNames, 
        defaultOptionIndex) {
        protected void buttonPressed(int buttonId) {
          super.buttonPressed(buttonId);
        }
      };
  }
  
  public Option interact(final String question, Option... options) {
    HashSet<Option> reducedOptions = new LinkedHashSet<>();
    Option defaultOption = null;
    final ArrayList<String> optionNames = new ArrayList<>();
    byte b;
    int i;
    Option[] arrayOfOption;
    for (i = (arrayOfOption = options).length, b = 0; b < i; ) {
      Option option = arrayOfOption[b];
      if (reducedOptions.contains(option))
        throw new IllegalArgumentException(
            "Question can't have duplicated choice(s) :" + option); 
      optionNames.add(option.getName());
      reducedOptions.add(option);
      if (option.isDefault()) {
        if (defaultOption != null)
          throw new IllegalArgumentException(
              "Question must have only one default choice"); 
        defaultOption = option;
      } 
      b++;
    } 
    final int defaultIndex = (defaultOption != null) ? optionNames.indexOf(defaultOption.getName()) : 0;
    return SWTUtil.<Option>exec(new SWTRunner<Option>() {
          public Option process() {
            Dialog dialog = SWTPrompter.this.createDialog(question, (String[])optionNames.toArray((Object[])new String[0]), defaultIndex);
            dialog.open();
            return options[dialog.getReturnCode()];
          }
        });
  }
  
  public void notify(final String message) {
    SWTUtil.asyncExec(new Runnable() {
          public void run() {
            NotifierDialog.notify("Notify", message, NotificationType.INFO);
          }
        });
  }
  
  public void cancel() {}
  
  public Object password(final String message) {
    return SWTUtil.exec(new SWTRunner<char[]>() {
          public char[] process() {
            PasswordInputDialog dialog = 
              new PasswordInputDialog("Password Input Dialog", message);
            dialog.open();
            if (dialog.getReturnCode() != 0)
              return null; 
            return StringUtil.trim(dialog.getValue()).toCharArray();
          }
        });
  }
  
  public void error(final String message) {
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            MessageDialog.openError(SWTUtil.getActiveShell(), "Error", message);
          }
        });
  }
  
  public void batch(final Collection<UserField> userFields, final Map<String, Object> options) {
    final AtomicInteger returnCode = new AtomicInteger();
    SWTUtil.syncExec(new Runnable() {
          public void run() {
            DynamicDialog dynamicDialog = new DynamicDialog(userFields);
            if (options != null && options.containsKey("title")) {
              Object object = options.get("title");
              if (object instanceof String)
                dynamicDialog.setTitle((String)object); 
            } 
            returnCode.set(dynamicDialog.open());
            if (options != null)
              options.put("returnCode", new Integer(returnCode.get())); 
          }
        });
    if (returnCode.get() == 1)
      throw new OperationCanceledException("Cancel interaction."); 
  }
}
