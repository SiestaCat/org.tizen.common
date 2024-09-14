package org.tizen.common.sdb.command.message;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.DialogUtil;
import org.tizen.common.util.SWTUtil;

public class CertificateDefaultErrorHandler implements ICommandErrorHandler {
  private static final String CERTIFICATE_MGR_COMMAND_ID = "org.tizen.common.signing.certificateManager";
  
  private static final int OPEN_BUTTON_ID = 0;
  
  private static final int CANCEL_BUTTON_ID = 1;
  
  private static final String OPEN_BUTTON = "Open";
  
  private static final String CANCEL_BUTTON = "Cancel";
  
  private static final String MESSAGE = "The application installation on the device has failed due to a signature error! (error code : %d)%n%nYou need an appropriate certificate profile, which can be created or activated in the Certificate Manager. Do you want to open the Certificate Manager?";
  
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  public void handle(final CommandErrorException e) {
    SWTUtil.asyncExec(new Runnable() {
          public void run() {
            int errorCode = e.getErrorCode();
            String message = String.format("The application installation on the device has failed due to a signature error! (error code : %d)%n%nYou need an appropriate certificate profile, which can be created or activated in the Certificate Manager. Do you want to open the Certificate Manager?", new Object[] { Integer.valueOf(errorCode) });
            String title = JFaceResources.getString("Problem_Occurred");
            String[] buttonStrings = { "Open", "Cancel" };
            MessageDialog dialog = new MessageDialog(DialogUtil.getActiveShell(), title, null, 
                message, 1, buttonStrings, 0);
            int result = dialog.open();
            if (result == 0) {
              IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
              IHandlerService handlerService = (IHandlerService)page.getActivePart().getSite().getService(IHandlerService.class);
              try {
                handlerService.executeCommand("org.tizen.common.signing.certificateManager", null);
              } catch (Exception e) {
                CertificateDefaultErrorHandler.this.logger.error("Failed to execute certificate manager", e);
              } 
            } else {
              dialog.close();
            } 
          }
        });
  }
}
