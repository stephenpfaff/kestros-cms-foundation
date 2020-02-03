package io.kestros.cms.foundation.design.theme;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.exceptions.InvalidUiFrameworkException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private Theme theme;

  private Resource resource;

  private Map<String, String> properties = new HashMap<>();
  private Map<String, String> frameworkProperties = new HashMap<>();

  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() {
    context.addModelsForPackage("com.slingware");
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    fileJcrContentProperties.put("jcr:mimeType", "text/css");
  }

  @Test
  public void testGetUiFramework() throws InvalidThemeException, InvalidUiFrameworkException {
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    context.create().resource("/etc/ui-frameworks/my-ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/my-ui/themes/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("my-ui", theme.getUiFramework().getName());
  }

  @Test
  public void testGetUiFrameworkWhenFrameworkIsInvalid() {
    resource = context.create().resource("/etc/themes/theme", properties);

    theme = resource.adaptTo(Theme.class);

    try {
      theme.getUiFramework();
    } catch (InvalidUiFrameworkException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve parent UiFramework for theme '/etc/themes/theme'. Unable to adapt "
        + "'/etc' to UiFramework: Invalid resource type.", exception.getMessage());
  }


  @Test
  public void testGetUiFrameworkWhenInvalidResourceType() {
    context.create().resource("/etc/ui-frameworks/my-ui");
    resource = context.create().resource("/etc/ui-frameworks/my-ui/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    try {
      theme.getUiFramework();
    } catch (InvalidUiFrameworkException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve parent UiFramework for theme "
                 + "'/etc/ui-frameworks/my-ui/theme-with-ui-framework'. Unable to adapt "
                 + "'/etc/ui-frameworks' to UiFramework: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetOutput() throws InvalidResourceTypeException {
    InputStream importerInputStream = new ByteArrayInputStream(
        ".test-output{ color: red;}".getBytes());

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n", theme.getOutput(CSS, false));
  }

  @Test
  public void testGetOutputWhenHasUiFramework() throws InvalidResourceTypeException {
    InputStream uiFrameworkInputStream = new ByteArrayInputStream(
        ".test-output{ color: blue;}".getBytes());
    InputStream themeInputStream = new ByteArrayInputStream(
        ".test-output{ color: red;}".getBytes());

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", uiFrameworkInputStream);
    context.create().resource("/etc/ui-frameworks/ui/css/file.css/jcr:content",
        fileJcrContentProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", themeInputStream);
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: blue;\n" + "  color: red;\n" + "}\n",
        theme.getOutput(CSS, false));
  }

  @Test
  public void testGetOutputWhenHasUiFrameworkAndUsingLess() throws InvalidResourceTypeException {
    InputStream uiFrameworkInputStream = new ByteArrayInputStream(
        ".test-output{ color: @theme-color;}".getBytes());
    InputStream themeInputStream = new ByteArrayInputStream("@theme-color: red;".getBytes());

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", uiFrameworkInputStream);
    context.create().resource("/etc/ui-frameworks/ui/css/file.css/jcr:content",
        fileJcrContentProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", themeInputStream);
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n", theme.getOutput(CSS, false));
  }

  @Test
  public void testGetOutputWhenHasUiFrameworkAndUsingLessAndThemeOverridesValue()
      throws InvalidResourceTypeException {
    InputStream uiFrameworkInputStream = new ByteArrayInputStream(
        "@theme-color:blue; .test-output{ color: @theme-color;}".getBytes());
    InputStream themeInputStream = new ByteArrayInputStream("@theme-color: red;".getBytes());

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", uiFrameworkInputStream);
    context.create().resource("/etc/ui-frameworks/ui/css/file.css/jcr:content",
        fileJcrContentProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", themeInputStream);
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n", theme.getOutput(CSS, false));
  }

  @Test
  public void testGetOutputWhenJavaScript() throws InvalidResourceTypeException {
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");

    InputStream importerInputStream = new ByteArrayInputStream("console.log('test');".getBytes());

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.js");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js/file.js", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js/file.js/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("console.log('test');\n", theme.getOutput(JAVASCRIPT, false));
  }
}