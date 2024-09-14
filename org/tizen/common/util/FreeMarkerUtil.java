package org.tizen.common.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class FreeMarkerUtil {
  private static final String DEFAULT_ENCODING = "8859_1";
  
  public static Configuration getDefaultConfiguration() {
    Configuration cfg = new Configuration();
    cfg.setEncoding(Locale.getDefault(), "8859_1");
    cfg.setObjectWrapper((ObjectWrapper)new DefaultObjectWrapper());
    return cfg;
  }
  
  public static Configuration getConfiguration(AbstractUIPlugin activator, String relativeParentPath) throws URISyntaxException, IOException {
    Assert.notNull(activator);
    Assert.notNull(relativeParentPath);
    Configuration cfg = getDefaultConfiguration();
    URL templateFolderURL = activator.getBundle().getEntry(relativeParentPath);
    if (templateFolderURL == null)
      throw new FileNotFoundException(); 
    URI templateFolderURI = FileLocator.toFileURL(templateFolderURL).toURI();
    File templateFolder = new File(templateFolderURI);
    cfg.setDirectoryForTemplateLoading(templateFolder);
    return cfg;
  }
  
  public static void generateDocument(Map<String, Object> root, Configuration cfg, String templateFile, String destination) throws IOException, TemplateException {
    generateDocument(root, cfg, templateFile, new File(destination));
  }
  
  public static void generateDocument(Map<String, Object> root, Configuration cfg, String templateFile, File destinationFile) throws IOException, TemplateException {
    OutputStream out = null;
    try {
      out = new FileOutputStream(destinationFile);
      generateDocument(root, cfg, templateFile, out);
    } finally {
      IOUtil.tryClose(new Object[] { out });
    } 
  }
  
  public static void generateDocument(Map<String, Object> root, Configuration cfg, String templateFile, OutputStream outputStream) throws IOException, TemplateException {
    Writer writer = null;
    try {
      writer = new OutputStreamWriter(outputStream);
      writer = new BufferedWriter(writer);
      generateDocument(root, cfg, templateFile, writer);
    } finally {
      IOUtil.tryClose(new Object[] { writer });
    } 
  }
  
  public static void generateDocument(Map<String, Object> root, Configuration cfg, String templateFile, Writer writer) throws IOException, TemplateException {
    Assert.notNull(root);
    Assert.notNull(cfg);
    Assert.notNull(templateFile);
    Assert.notNull(writer);
    Template template = cfg.getTemplate(templateFile);
    template.process(root, writer);
    IOUtil.tryFlush(new Flushable[] { writer });
  }
}
