package org.tizen.common;

import org.eclipse.ui.PlatformUI;

public class TizenHelpContextIds {
  public static String HELP_CONTENT_BASE_URL = (PlatformUI.getWorkbench().getHelpSystem().resolve("", false) == null) ? "" : PlatformUI.getWorkbench().getHelpSystem().resolve("", false).toString();
  
  private static final String HELP_COMMON = "org.tizen.ide.common.";
  
  public static final String HELP_COMMON_CONNECTION_EXPLORER_CONTEXT = "org.tizen.ide.common.connection_explorer_context";
  
  public static final String HELP_COMMON_LOG_CONTEXT = "org.tizen.ide.common.log_context";
  
  public static final String HELP_COMMON_CERTIFICATE_GENERTOR_CONTEXT = "org.tizen.ide.common.certificate_generator_context";
  
  private static final String HELP_NATIVE = "org.tizen.ide.native.";
  
  public static final String HELP_NATIVE_CODECOVERAGE_CONTEXT = "org.tizen.ide.native.codecoverage_context";
  
  public static final String HELP_NATIVE_UNITTEST_CONTEXT = "org.tizen.ide.native.unittest_context";
  
  public static final String HELP_NATIVE_MANIFEST_CONTEXT = "org.tizen.ide.native.manifest_context";
  
  public static final String HELP_NATIVE_PROJECT_WIZARD_CONTEXT = "org.tizen.ide.native.project_wizard_context";
  
  public static final String HELP_NATIVE_PROJECT_WIZARD_SETTINGS_CONTEXT = "org.tizen.ide.native.project_wizard_settings_context";
  
  public static final String HELP_NATIVE_SMART_LAUNCH_CONTEXT = "org.tizen.ide.native.smart_launch_context";
  
  public static final String HELP_NATIVE_DEBUG_ATTACH_CONTEXT = "org.tizen.ide.native.debug_attach_context";
  
  private static final String HELP_WEB = "org.tizen.ide.web.";
  
  public static final String HELP_WEB_WIDGET_CONFIGURATION_EDITOR_CONTEXT = "org.tizen.ide.web.widget_configuration_editor_context";
  
  public static final String HELP_WEB_HTML_EDITOR_CONTEXT = "org.tizen.ide.web.html_editor_context";
  
  public static final String HELP_WEB_JAVASCRIPT_EDITOR_CONTEXT = "org.tizen.ide.web.javascript_editor_context";
  
  public static final String HELP_WEB_CSS_EDITOR_CONTEXT = "org.tizen.ide.web.css_editor_context";
  
  public static final String HELP_WEB_HTML_PREVIEW_CONTEXT = "org.tizen.ide.web.html_preview_context";
  
  public static final String HELP_WEB_CSS_PREVIEW_CONTEXT = "org.tizen.ide.web.css_preview_context";
  
  public static final String HELP_WEB_PREFERENCES_CONTEXT = "org.tizen.ide.web.preferences_context";
  
  public static final String HELP_WEB_SIMULATOR_PREFERENCES_CONTEXT = "org.tizen.ide.web.simulator_preferences_context";
  
  public static final String HELP_WEB_NEW_PROJECT_WIZARD_CONTEXT = "org.tizen.ide.web.new_project_wizard_context";
  
  public static final String HELP_WEB_USER_TEMPLATE_CONTEXT = "org.tizen.ide.web.user_template_context";
  
  public static final String HELP_WEB_PROJECT_BUILD_PROPERTIES_CONTEXT = "org.tizen.ide.web.project_build_properties_context";
  
  public static final String HELP_WEB_LOCALIZE_WIDGET_CONTEXT = "org.tizen.ide.web.localize_widget_context";
  
  public static final String HELP_WEB_UNIT_TEST_WIDGET_CONTEXT = "org.tizen.ide.web.web_unit_test_context";
}
