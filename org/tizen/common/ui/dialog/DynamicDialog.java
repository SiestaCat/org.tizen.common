package org.tizen.common.ui.dialog;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.command.InputValidator;
import org.tizen.common.core.command.UserField;
import org.tizen.common.util.Assert;
import org.tizen.common.util.CollectionUtil;
import org.tizen.common.util.SWTUtil;
import org.tizen.common.util.StringUtil;

public class DynamicDialog extends Dialog {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected String title = "Dynamic Dialog";
  
  protected int width = 500;
  
  protected Text errorMessageText;
  
  protected String errorMessage;
  
  protected Collection<UserField> fields;
  
  protected Map<String, Control> controlMap = new HashMap<>();
  
  public DynamicDialog(Shell parent, Collection<UserField> fields) {
    super(parent);
    Assert.notNull(fields);
    this.fields = fields;
  }
  
  public DynamicDialog(IShellProvider parentShell, Collection<UserField> fields) {
    super(parentShell);
    Assert.notNull(fields);
    this.fields = fields;
  }
  
  public DynamicDialog(Collection<UserField> fields) {
    this(SWTUtil.getActiveShell(), fields);
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(getTitle());
  }
  
  protected int getShellStyle() {
    return super.getShellStyle() | 0x10000;
  }
  
  protected Point getInitialSize() {
    Point initialSize = super.getInitialSize();
    initialSize.x = this.width;
    return initialSize;
  }
  
  protected void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
      this.errorMessageText.setText(StringUtil.nvl(new String[] { errorMessage }));
      boolean hasError = StringUtil.hasLength(errorMessage);
      this.errorMessageText.setEnabled(hasError);
      this.errorMessageText.setVisible(hasError);
      this.errorMessageText.getParent().update();
      Button button = getButton(0);
      if (button != null)
        button.setEnabled(!hasError); 
    } 
  }
  
  protected Text createErrorMessageText(Composite parent) {
    this.errorMessageText = new Text(parent, 72);
    this.errorMessageText.setLayoutData(new GridData(768));
    this.errorMessageText.setBackground(this.errorMessageText.getDisplay().getSystemColor(22));
    setErrorMessage(this.errorMessage);
    return this.errorMessageText;
  }
  
  private String validateInput(UserField userField) {
    InputValidator validator = userField.getValidator();
    if (validator != null) {
      Control control = this.controlMap.get(userField.getId());
      if (control instanceof Text && control.isEnabled()) {
        String text = ((Text)control).getText();
        String errorMsg = validator.check(text);
        if (StringUtil.hasText(errorMsg))
          return errorMsg; 
      } 
    } 
    Collection<UserField> children = userField.getChildren();
    if (children != null)
      for (UserField child : children) {
        String errorMsg = validateInput(child);
        if (StringUtil.hasText(errorMsg))
          return errorMsg; 
      }  
    return "";
  }
  
  protected void validateInput() {
    String errorMessage = null;
    for (UserField userField : this.fields) {
      errorMessage = validateInput(userField);
      if (StringUtil.hasText(errorMessage))
        break; 
    } 
    setErrorMessage(errorMessage);
  }
  
  protected Control createContents(Composite parent) {
    Composite superContents = (Composite)super.createContents(parent);
    Composite dialogArea = (Composite)getDialogArea();
    createErrorMessageText(dialogArea);
    boolean firstControl = true;
    for (UserField field : this.fields) {
      Control control = createDynamicControl(dialogArea, field);
      if (firstControl && control != null) {
        firstControl = false;
        control.setFocus();
        if (control instanceof Text)
          ((Text)control).selectAll(); 
      } 
      if (control instanceof Button)
        enableChildControls(field.getId(), getBoolean(field.getValue())); 
    } 
    return (Control)superContents;
  }
  
  protected Control createDynamicControl(Composite parent, UserField field) {
    Button button;
    Control control = null;
    if (!isSupportField(field))
      return null; 
    Class<?> type = field.getType();
    if (String.class.equals(type)) {
      Text text = createStringTypeControl(parent, field);
    } else if (char[].class.equals(type) || Character[].class.equals(type)) {
      Text text = createCharacterTypeControl(parent, field);
    } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
      button = createBooleanTypeControl(parent, field);
    } else {
      throw new IllegalArgumentException("Unsupported user field type.");
    } 
    Collection<UserField> children = field.getChildren();
    if (children != null) {
      Composite composite = createComposite(parent, (Layout)new GridLayout(), new GridData(1808));
      for (UserField childField : children)
        createDynamicControl(composite, childField); 
    } 
    return (Control)button;
  }
  
  protected boolean isSupportField(UserField field) {
    Collection<Object> supports = field.getSupports();
    if (supports != null && !supports.contains("eclipse")) {
      this.logger.warn("{} is not supported in {}", field, this);
      return false;
    } 
    return true;
  }
  
  public int getCompositeStyle() {
    return 0;
  }
  
  public int getTextStyle() {
    return 2052;
  }
  
  public int getPasswordTextStyle() {
    return getTextStyle() | 0x400000;
  }
  
  public int getLabelStyle() {
    return 64;
  }
  
  public int getCheckButtonStyle() {
    return 16777248;
  }
  
  protected Text createStringTypeControl(Composite parent, final UserField field) {
    Composite composite = createComposite(parent, (Layout)new GridLayout(2, false), new GridData(1808));
    createLabel(composite, new GridData(128), field.getMessage(), field.canModify());
    final Text text = createText(composite, new GridData(1808), field.getValue(), field.canModify(), field.getValidator());
    final ModifyListener modifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          Text modifiedText = (Text)e.widget;
          field.setValue(modifiedText.getText());
        }
      };
    text.addModifyListener(modifyListener);
    text.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            text.removeModifyListener(modifyListener);
            text.removeDisposeListener(this);
          }
        });
    this.controlMap.put(field.getId(), text);
    return text;
  }
  
  protected Text createCharacterTypeControl(Composite parent, final UserField field) {
    Composite composite = createComposite(parent, (Layout)new GridLayout(2, false), new GridData(1808));
    createLabel(composite, new GridData(128), field.getMessage(), field.canModify());
    final Text text = createPasswordText(composite, new GridData(1808), field.getValue(), field.canModify(), field.getValidator());
    final ModifyListener modifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          Text modifiedText = (Text)e.widget;
          field.setValue(modifiedText.getText().toCharArray());
        }
      };
    text.addModifyListener(modifyListener);
    text.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            text.removeModifyListener(modifyListener);
            text.removeDisposeListener(this);
          }
        });
    this.controlMap.put(field.getId(), text);
    return text;
  }
  
  protected Button createBooleanTypeControl(Composite parent, final UserField field) {
    final Button button = createCheckButton(parent, new GridData(2), field.getId(), field.getMessage(), field.getValue(), field.canModify());
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          Button modifiedButton = (Button)e.widget;
          field.setValue(Boolean.valueOf(modifiedButton.getSelection()));
        }
      };
    button.addSelectionListener((SelectionListener)selectionAdapter);
    button.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            button.removeSelectionListener((SelectionListener)selectionAdapter);
            button.removeDisposeListener(this);
          }
        });
    this.controlMap.put(field.getId(), button);
    return button;
  }
  
  private Composite createComposite(Composite parent, Layout layout, Object layoutData) {
    Composite composite = new Composite(parent, getCompositeStyle()) {
        public void setEnabled(boolean enabled) {
          super.setEnabled(enabled);
          byte b;
          int i;
          Control[] arrayOfControl;
          for (i = (arrayOfControl = getChildren()).length, b = 0; b < i; ) {
            Control children = arrayOfControl[b];
            if (DynamicDialog.this.controlMap.containsValue(children)) {
              String controlId = (String)DynamicDialog.this.getKeyByValue((Map)DynamicDialog.this.controlMap, (E)children);
              UserField userField = DynamicDialog.this.getModel(DynamicDialog.this.fields, controlId);
              if (userField == null || !userField.canModify())
                continue; 
            } 
            children.setEnabled(enabled);
            continue;
            b++;
          } 
        }
      };
    composite.setLayout(layout);
    composite.setLayoutData(layoutData);
    return composite;
  }
  
  private Label createLabel(Composite parent, Object layoutData, String msg, boolean enabled) {
    Label label = new Label(parent, getLabelStyle());
    label.setLayoutData(layoutData);
    label.setText(msg);
    label.setEnabled(enabled);
    return label;
  }
  
  private Text createText(Composite parent, Object layoutData, Object defaultText, boolean enabled, InputValidator validator) {
    final Text text = new Text(parent, getTextStyle());
    text.setLayoutData(layoutData);
    final ModifyListener textModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          DynamicDialog.this.validateInput();
        }
      };
    text.addModifyListener(textModifyListener);
    text.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            text.removeModifyListener(textModifyListener);
            text.removeDisposeListener(this);
          }
        });
    if (defaultText instanceof String) {
      text.setText((String)defaultText);
    } else {
      text.setText("");
    } 
    text.setEnabled(enabled);
    return text;
  }
  
  private Text createPasswordText(Composite parent, Object layoutData, Object defaultText, boolean enabled, InputValidator validator) {
    final Text text = new Text(parent, getPasswordTextStyle());
    text.setLayoutData(layoutData);
    final ModifyListener textModifyListener = new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          DynamicDialog.this.validateInput();
        }
      };
    text.addModifyListener(textModifyListener);
    text.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            text.removeModifyListener(textModifyListener);
            text.removeDisposeListener(this);
          }
        });
    if (defaultText instanceof char[]) {
      text.setText(new String((char[])defaultText));
    } else if (defaultText instanceof Character[]) {
      text.setText(Arrays.toString((Object[])defaultText));
    } else {
      text.setText("");
    } 
    text.setEnabled(enabled);
    return text;
  }
  
  private Button createCheckButton(Composite parent, Object layoutData, final String id, String msg, Object value, boolean enabled) {
    final Button button = new Button(parent, getCheckButtonStyle());
    button.setLayoutData(layoutData);
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          DynamicDialog.this.enableChildControls(id, ((Button)e.widget).getSelection());
        }
      };
    button.addSelectionListener((SelectionListener)selectionAdapter);
    button.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            button.removeSelectionListener((SelectionListener)selectionAdapter);
            button.removeDisposeListener(this);
          }
        });
    button.setText(msg);
    button.setEnabled(enabled);
    button.setSelection(getBoolean(value));
    return button;
  }
  
  private <T, E> T getKeyByValue(Map<T, E> map, E value) {
    Assert.notNull(map);
    Assert.notNull(value);
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (value.equals(entry.getValue()))
        return entry.getKey(); 
    } 
    return null;
  }
  
  private boolean getBoolean(Object obj) {
    return (obj == null) ? true : Boolean.parseBoolean(String.valueOf(obj));
  }
  
  private void enableChildControls(String id, boolean enabled) {
    Collection<UserField> childIds = null;
    for (UserField field : this.fields) {
      childIds = getChildModelById(field, id);
      if (!CollectionUtil.isEmpty(childIds))
        break; 
    } 
    if (!CollectionUtil.isEmpty(childIds))
      for (UserField childId : childIds) {
        Composite composite;
        if (!childId.canModify() && enabled)
          continue; 
        Control control = this.controlMap.get(childId.getId());
        if (!(control instanceof Composite))
          composite = control.getParent(); 
        composite.setEnabled(enabled);
      }  
  }
  
  private UserField getModel(Collection<UserField> fields, String id) {
    for (UserField field : fields) {
      if (field.getId().equals(id))
        return field; 
      if (!CollectionUtil.isEmpty(field.getChildren())) {
        UserField result = getModel(field.getChildren(), id);
        if (result != null)
          return result; 
      } 
    } 
    return null;
  }
  
  private Collection<UserField> getChildModelById(UserField field, String id) {
    Assert.notNull(id);
    Collection<UserField> children = field.getChildren();
    if (CollectionUtil.isEmpty(field.getChildren()))
      return null; 
    if (id.equals(field.getId()))
      return children; 
    for (UserField child : children) {
      Collection<UserField> childResult = getChildModelById(child, child.getId());
      if (!CollectionUtil.isEmpty(childResult))
        return childResult; 
    } 
    return null;
  }
}
