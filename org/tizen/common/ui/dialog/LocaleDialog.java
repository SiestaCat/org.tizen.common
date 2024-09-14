package org.tizen.common.ui.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.tizen.common.util.StringUtil;

public class LocaleDialog extends SelectionStatusDialog {
  protected static String DEFAULT_LANG = "en-gb";
  
  public static final String[] WRT_LANG_LISTS = new String[] { 
      "ar-ae", 
      "as-in", 
      "az-az", 
      "be-by", 
      "bg-bg", 
      "bn-bd", 
      "bn-in", 
      "bn", 
      "ca-es", 
      "cs-cz", 
      "da-dk", 
      "de-at", 
      "de-ch", 
      "de-de", 
      "de", 
      "el-gr", 
      "en-au", 
      "en-ca", 
      "en-gb", 
      "en-ie", 
      "en-nz", 
      "en-ph", 
      "en-us", 
      "en-za", 
      "es-es", 
      "es-mx", 
      "es-us", 
      "et-ee", 
      "eu-es", 
      "en", 
      "fa-ir", 
      "fi-fi", 
      "fr-be", 
      "fr-ca", 
      "fr-ch", 
      "fr-fr", 
      "fr", 
      "ga-ie", 
      "gl-es", 
      "gu-in", 
      "he-il", 
      "hi-in", 
      "hr-hr", 
      "hu-hu", 
      "hy-am", 
      "id-id", 
      "is-is", 
      "it-it", 
      "ja-jp", 
      "jv-id", 
      "ka-ge", 
      "kk-kz", 
      "km-kh", 
      "kn-ca", 
      "kn-in", 
      "kn", 
      "ko-kr", 
      "ky-kg", 
      "lo-la", 
      "lt-lt", 
      "lv-lv", 
      "mk-mk", 
      "ml-in", 
      "ml-my", 
      "ml", 
      "mn-mn", 
      "mr-in", 
      "ms-mw", 
      "my-mm", 
      "nb-no", 
      "ne-np", 
      "nl-be", 
      "nl-nl", 
      "nl", 
      "or-in", 
      "pa-in", 
      "pa-pk", 
      "pa", 
      "pl-pl", 
      "pt-br", 
      "pt-pt", 
      "pt", 
      "ro-ro", 
      "ru-ru", 
      "si-lk", 
      "sk-sk", 
      "sl-si", 
      "sq-al", 
      "sr-rs", 
      "su-id", 
      "sv-se", 
      "ta-in", 
      "te-in", 
      "tg-tj", 
      "th-th", 
      "tk-tm", 
      "tl-ph", 
      "tr-tr", 
      "uk-ua", 
      "ur-pk", 
      "uz-uz", 
      "vi-vn", 
      "xh-za", 
      "zh-cn", 
      "zh-hk", 
      "zh-sg", 
      "zh-tw", 
      "zh", 
      "zu-za" };
  
  protected List<String> CUSTOM_LANG_LISTS = new LinkedList<>();
  
  protected Label contentLabel;
  
  protected String lang;
  
  protected String content;
  
  protected ComboViewer langComboViewer;
  
  protected Text contentText;
  
  protected Set<String> filter;
  
  private String title;
  
  public LocaleDialog(Shell parentShell, String title, HashMap<String, String> locales) {
    super(parentShell);
    setTitle(title);
    this.title = title;
    this.filter = new HashSet<>();
    for (String lang : locales.keySet())
      this.filter.add(lang); 
  }
  
  public LocaleDialog(Shell parentShell, HashMap<String, String> locales) {
    this(parentShell, "Name", locales);
  }
  
  protected void computeResult() {
    IStructuredSelection selection = (IStructuredSelection)this.langComboViewer.getSelection();
    if (!selection.isEmpty())
      this.lang = (String)selection.getFirstElement(); 
    this.content = this.contentText.getText().trim();
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite composite = new Composite(parent, 0);
    GridLayout grid = new GridLayout(2, false);
    grid.marginWidth = grid.marginHeight = 20;
    grid.verticalSpacing = 10;
    composite.setLayout((Layout)grid);
    composite.setLayoutData(new GridData(1808));
    applyDialogFont((Control)composite);
    Label label = new Label(composite, 0);
    label.setText("Language");
    GridData data = new GridData(1, 2, false, false);
    data.widthHint = 105;
    label.setLayoutData(data);
    CCombo langCCombo = new CCombo(composite, 2056);
    data = new GridData(768);
    data.widthHint = 365;
    langCCombo.setLayoutData(data);
    langCCombo.setVisibleItemCount(10);
    this.langComboViewer = new ComboViewer(langCCombo);
    this.langComboViewer.setContentProvider((IContentProvider)ArrayContentProvider.getInstance());
    this.langComboViewer.setLabelProvider((IBaseLabelProvider)new LabelProvider() {
          public String getText(Object element) {
            String lang = (String)element;
            if (lang.equalsIgnoreCase(LocaleDialog.DEFAULT_LANG))
              return String.valueOf(LocaleDialog.DEFAULT_LANG) + " (default)"; 
            return super.getText(element);
          }
        });
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = WRT_LANG_LISTS).length, b = 0; b < i; ) {
      String lang = arrayOfString[b];
      if (!this.filter.contains(lang))
        this.langComboViewer.add(lang); 
      b++;
    } 
    for (String lang : this.CUSTOM_LANG_LISTS) {
      if (!this.filter.contains(lang))
        this.langComboViewer.add(lang); 
    } 
    this.langComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
          public void selectionChanged(SelectionChangedEvent event) {
            LocaleDialog.this.validate();
          }
        });
    if (this.filter.contains(DEFAULT_LANG)) {
      if (this.langComboViewer.getCCombo().getItemCount() != 0)
        this.langComboViewer.setSelection((ISelection)new StructuredSelection(this.langComboViewer.getCCombo().getItem(0))); 
    } else {
      this.langComboViewer.setSelection((ISelection)new StructuredSelection(DEFAULT_LANG));
    } 
    this.contentLabel = new Label(composite, 0);
    this.contentLabel.setText(StringUtil.isEmpty(this.title) ? "Name" : this.title);
    data = new GridData(1, 2, false, false);
    data.widthHint = 105;
    this.contentLabel.setLayoutData(data);
    this.contentText = new Text(composite, 2048);
    data = new GridData(768);
    data.widthHint = 365;
    this.contentText.setLayoutData(data);
    this.contentText.addModifyListener(new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            LocaleDialog.this.validate();
          }
        });
    this.contentText.setFocus();
    if (!StringUtil.isEmpty(this.content)) {
      if (StringUtil.isEmpty(this.lang) && this.CUSTOM_LANG_LISTS.contains("")) {
        this.langComboViewer.add("");
        this.langComboViewer.setSelection((ISelection)new StructuredSelection(""));
      } else {
        this.langComboViewer.add(this.lang);
        this.langComboViewer.setSelection((ISelection)new StructuredSelection(this.lang));
      } 
      this.contentText.setText(this.content);
    } 
    setHelpAvailable(false);
    validate();
    return (Control)composite;
  }
  
  protected void validate() {
    Status status1;
    if (this.contentText == null)
      return; 
    IStatus status = null;
    if (this.contentText.getText().trim().length() == 0) {
      status1 = new Status(4, "org.tizen.common", "Enter a " + this.title.toLowerCase());
    } else {
      status1 = new Status(0, "org.tizen.common", null);
    } 
    updateStatus((IStatus)status1);
  }
  
  public void editLang(String lang, String content) {
    this.lang = lang;
    this.content = content;
  }
  
  public String getLang() {
    if (StringUtil.isEmpty(this.lang))
      return null; 
    return this.lang;
  }
  
  public void setLang(String lang) {
    this.lang = lang;
  }
  
  public String getContent() {
    return this.content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public List<String> getCustomLangList() {
    return this.CUSTOM_LANG_LISTS;
  }
  
  public void setCustomLang(String lang) {
    this.CUSTOM_LANG_LISTS.add(lang);
  }
  
  public void setDefaultLang(String lang) {
    DEFAULT_LANG = lang;
  }
  
  public String getDefaultLang() {
    return DEFAULT_LANG;
  }
  
  public static int getDefaultLangLength() {
    return WRT_LANG_LISTS.length;
  }
}
