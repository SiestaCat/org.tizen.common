package org.tizen.common.ui.dialog;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import net.sf.image4j.codec.bmp.BMPEncoder;
import net.sf.image4j.codec.ico.ICOEncoder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.Messages;
import org.tizen.common.ScreenDensity;
import org.tizen.common.util.DialogUtil;
import org.tizen.common.util.FileUtil;
import org.tizen.common.util.ImageUtil;
import org.tizen.common.util.StringUtil;
import org.tizen.common.util.ValidationUtil;

public class NewIconDialog extends SelectionStatusDialog {
  private static final Logger logger = LoggerFactory.getLogger(NewIconDialog.class);
  
  public static final String ICON_FILE_NAME = "icon.png";
  
  public static final String MASK_IMAGE_PATH = "/icons/mask.png";
  
  public static final String EFFECT_IMAGE_PATH = "/icons/effect.png";
  
  public static final int ICON_SIZE = ScreenDensity.XHIGH.getIconSize();
  
  private Text fileText;
  
  private Button fileBrowseBtn;
  
  private String iconPath;
  
  private Label previewImage;
  
  private Label previewLabel;
  
  private Button shadowOptionChk;
  
  private IProject fProject;
  
  private Label applyOptionLabel;
  
  private Label fileLabel;
  
  private BufferedImage fEffectImage;
  
  private BufferedImage fMaskImage;
  
  private BufferedImage fIconImage;
  
  private Button circularOptionChk;
  
  public String iconFileName = "icon.png";
  
  public NewIconDialog(Shell parentShell, IProject project) {
    super(parentShell);
    setTitle(Messages.NewIconDialog_Title);
    this.fProject = project;
  }
  
  public String getIconPath() {
    return this.iconPath;
  }
  
  public String getIconFileName() {
    return this.iconFileName;
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite baseComposite = new Composite(parent, 0);
    GridLayout grid = new GridLayout(2, false);
    grid.marginWidth = grid.marginHeight = 20;
    grid.verticalSpacing = 10;
    baseComposite.setLayout((Layout)grid);
    baseComposite.setLayoutData(new GridData(1808));
    applyDialogFont((Control)baseComposite);
    this.fileLabel = new Label(baseComposite, 0);
    this.fileLabel.setText(Messages.NewIconDialog_ImageFileLabel);
    GridData data = new GridData(1, 16777216, false, false);
    data.widthHint = 150;
    this.fileLabel.setLayoutData(data);
    Composite fileComposite = new Composite(baseComposite, 0);
    grid = new GridLayout(2, false);
    grid.marginWidth = grid.marginHeight = 0;
    grid.horizontalSpacing = 10;
    grid.verticalSpacing = 0;
    fileComposite.setLayout((Layout)grid);
    data = new GridData(4, 16777216, true, false);
    data.widthHint = 320;
    fileComposite.setLayoutData(data);
    this.fileText = new Text(fileComposite, 2048);
    this.fileText.addModifyListener(new ModifyListener() {
          public void modifyText(ModifyEvent e) {
            NewIconDialog.this.validate();
          }
        });
    this.fileText.setLayoutData(new GridData(4, 16777216, true, false));
    this.fileBrowseBtn = new Button(fileComposite, 8);
    this.fileBrowseBtn.setText(Messages.NewIconDialog_BrowseBtnLabel);
    this.fileBrowseBtn.addSelectionListener((SelectionListener)new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            NewIconDialog.this.handleBrowse();
          }
        });
    this.fileBrowseBtn.setLayoutData(new GridData(16777224, 16777216, false, false));
    this.applyOptionLabel = new Label(baseComposite, 0);
    this.applyOptionLabel.setLayoutData(new GridData(1, 16777216, false, false));
    this.applyOptionLabel.setText(Messages.NewIconDialog_OptionLabel);
    Composite optionComposite = new Composite(baseComposite, 0);
    RowLayout row = new RowLayout();
    row.marginWidth = 0;
    row.spacing = 20;
    optionComposite.setLayout((Layout)row);
    this.circularOptionChk = new Button(optionComposite, 32);
    this.circularOptionChk.setText(Messages.NewIconDialog_CircularCheckBoxLabel);
    this.circularOptionChk.addSelectionListener((SelectionListener)new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            NewIconDialog.this.validate();
          }
        });
    this.shadowOptionChk = new Button(optionComposite, 32);
    this.shadowOptionChk.setText(Messages.NewIconDialog_ShadowCheckBoxLabel);
    this.shadowOptionChk.addSelectionListener((SelectionListener)new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            NewIconDialog.this.validate();
          }
        });
    this.previewLabel = new Label(baseComposite, 0);
    this.previewLabel.setLayoutData(new GridData(1, 16777216, false, false));
    this.previewLabel.setText(String.format("Preview\n(%1$dx%2$d)", new Object[] { Integer.valueOf(ICON_SIZE), Integer.valueOf(ICON_SIZE) }));
    this.previewImage = new Label(baseComposite, 2048);
    data = new GridData(1, 4, false, false);
    data.widthHint = data.heightHint = 117;
    this.previewImage.setLayoutData(data);
    setHelpAvailable(false);
    validate();
    return (Control)baseComposite;
  }
  
  private void validate() {
    Status status1;
    this.shadowOptionChk.setEnabled(this.circularOptionChk.getSelection());
    this.iconPath = this.fileText.getText();
    IStatus status = null;
    File file = new File(this.iconPath);
    if (StringUtil.isEmpty(this.iconPath) || file.isDirectory()) {
      status1 = new Status(4, "org.tizen.common", Messages.NewIconDialog_FileSelectError);
    } else if (!file.exists()) {
      status1 = new Status(4, "org.tizen.common", String.valueOf(this.iconPath) + " does not exist");
    } else {
      status1 = new Status(0, "org.tizen.common", null);
    } 
    updateStatus((IStatus)status1);
    if (status1.isOK())
      updatePreview(); 
  }
  
  protected void okPressed() {
    String fileName = FileUtil.getFileNameFromPath(this.iconPath);
    IFile destFile = this.fProject.getFile(fileName);
    String destFilePath = destFile.getLocation().toOSString();
    try {
      destFile.refreshLocal(0, null);
    } catch (CoreException e) {
      logger.error("Failed to synchronize with the file system", (Throwable)e);
    } 
    if (destFile.exists())
      if (DialogUtil.openQuestionDialog(Messages.NewIconDialog_MessageDialogTitle, 
          String.valueOf(destFile.getProjectRelativePath().toOSString()) + Messages.NewIconDialog_MessageDialogMessage) == 128) {
        InputDialog iconFileNameInputDialog = new InputDialog(getShell(), 
            Messages.NewIconDialog_RenameInputDialogTitle, 
            Messages.NewIconDialog_RenameInputDialogMessage, 
            "", 
            new IInputValidator() {
              public String isValid(String name) {
                if (StringUtil.isEmpty(name))
                  return Messages.NewIconDialog_EmptyFileName; 
                if (!ValidationUtil.checkForFileURI(name) || 
                  !ValidationUtil.checkForWidgetIconFileExtension(name))
                  return Messages.NewIconDialog_InvalidFileName; 
                if (FileUtil.isExist(String.valueOf(NewIconDialog.this.fProject.getLocation().toOSString()) + File.separator + name))
                  return Messages.NewIconDialog_ExistFileName; 
                return null;
              }
            });
        if (iconFileNameInputDialog.open() == 1)
          return; 
        this.iconFileName = iconFileNameInputDialog.getValue();
        destFile = this.fProject.getFile(this.iconFileName);
        destFilePath = destFile.getLocation().toOSString();
      } else {
        try {
          if (!this.iconPath.equals(destFilePath))
            destFile.delete(true, null); 
        } catch (CoreException e) {
          logger.error("Failed to delete", (Throwable)e);
        } 
      }  
    try {
      Image image = this.previewImage.getImage();
      String iconExtension = FileUtil.getFileExtension(destFilePath);
      if (iconExtension == null) {
        logger.error("Failed to load the icon extension");
      } else if (iconExtension.equalsIgnoreCase("ico")) {
        ICOEncoder.write(ImageUtil.convertToAWT(image.getImageData()), new File(destFilePath));
      } else if (iconExtension.equalsIgnoreCase("bmp")) {
        BMPEncoder.write(ImageUtil.convertToAWT(image.getImageData()), new File(destFilePath));
      } else {
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { image.getImageData() };
        loader.save(destFilePath, 5);
      } 
      this.iconFileName = FileUtil.getFileNameFromPath(destFilePath);
      destFile.getParent().refreshLocal(1, null);
    } catch (Exception e) {
      logger.error("Failed to save", e);
    } 
    this.iconPath = destFilePath;
    super.okPressed();
  }
  
  protected void computeResult() {}
  
  protected void updatePreview() {
    BufferedImage srcImage = ImageUtil.getAWTImage(this.iconPath, false);
    this.fIconImage = ImageUtil.getScaledImage(srcImage, ICON_SIZE, ICON_SIZE);
    if (this.circularOptionChk.getSelection()) {
      if (this.fMaskImage == null)
        this.fMaskImage = ImageUtil.getAWTImage("/icons/mask.png", true); 
      if (this.fEffectImage == null)
        this.fEffectImage = ImageUtil.getAWTImage("/icons/effect.png", true); 
      srcImage = new BufferedImage(this.fMaskImage.getWidth(), this.fMaskImage.getHeight(), 2);
      Graphics2D graph = srcImage.createGraphics();
      graph.drawImage(this.fMaskImage, 0, 0, (ImageObserver)null);
      graph.setComposite(AlphaComposite.SrcAtop);
      graph.drawImage(this.fIconImage, 0, 0, (ImageObserver)null);
      if (this.fEffectImage != null && this.shadowOptionChk.getSelection())
        graph.drawImage(this.fEffectImage, 0, 0, (ImageObserver)null); 
      graph.dispose();
    } else {
      srcImage = this.fIconImage;
    } 
    this.previewImage.setImage(ImageUtil.convertImageToSWT(getShell().getDisplay(), srcImage));
  }
  
  public void handleBrowse() {
    FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), 4096);
    dialog.setFilterExtensions(ValidationUtil.WIDGET_ICON_FILTER_EXTENSIONS);
    String src = dialog.open();
    if (src == null)
      return; 
    this.fileText.setText(src);
  }
}
