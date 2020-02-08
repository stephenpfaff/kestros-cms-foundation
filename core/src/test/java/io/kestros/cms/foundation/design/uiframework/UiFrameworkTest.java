package io.kestros.cms.foundation.design.uiframework;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.cache.htltemplate.HtlTemplateCacheService;
import io.kestros.cms.foundation.services.cache.htltemplate.impl.BaseHtlTemplateCacheService;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiFrameworkTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFramework uiFramework;

  private HtlTemplateCacheService htlTemplateCacheService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> vendorLibraryProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Map<String, Object> componentTypeUiFrameworkViewProperties = new HashMap<>();
  private Map<String, Object> componentVariationProperties = new HashMap<>();


  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    htlTemplateCacheService = new BaseHtlTemplateCacheService();
    context.registerService(HtlTemplateCacheService.class, htlTemplateCacheService);

    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
    themeProperties.put("jcr:primaryType", "kes:Theme");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    componentVariationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    componentTypeUiFrameworkViewProperties.put("jcr:primaryType", "kes:ComponentUiFrameworkView");

    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/less");
  }

  @Test
  public void testGetFrameworkCode() throws Exception {
    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);
    assertEquals("test-code", uiFramework.getFrameworkCode());
  }

  @Test
  public void testGetVendorLibraries() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-2", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-3", vendorLibraryProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2", "library-3"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(3, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenMissingVendorLibrariesRoot() throws Exception {
    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2", "library-3"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenVendorLibraryIsInvalid() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1");

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenVendorLibraryIsMissing() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetThemes() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);
    context.create().resource("/ui-framework/themes/theme-2", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getThemes().size());
    assertEquals("theme-1", uiFramework.getThemes().get(0).getName());
    assertEquals("theme-2", uiFramework.getThemes().get(1).getName());
  }

  @Test
  public void testGetThemesThemesRootResourceNotFound() {
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getThemes().size());
  }

  @Test
  public void testGetTheme() throws ChildResourceNotFoundException, InvalidResourceTypeException {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default", themeProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("/ui-framework/themes/theme-1", uiFramework.getTheme("theme-1").getPath());
  }

  @Test
  public void testGetDefaultTheme() throws InvalidResourceTypeException, ResourceNotFoundException,
                                           ChildResourceNotFoundException, InvalidThemeException {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default", themeProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("default", uiFramework.getDefaultTheme().getName());
  }

  @Test
  public void testGetDefaultThemeWhenInvalidResourceType() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default");
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    try {
      uiFramework.getDefaultTheme();
    } catch (ChildResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme 'default' under UiFramework '/ui-framework'. Could not adapt to "
        + "Theme. Resource must have jcr:primaryType 'kes:Theme'.", exception.getMessage());
  }

  @Test
  public void testGetDefaultThemeWhenNotFound() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    try {
      uiFramework.getDefaultTheme();
    } catch (ChildResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
      e.printStackTrace();
    }
    assertEquals("Unable to adapt 'default' under '/ui-framework/themes': Child not found.",
        exception.getMessage());
  }

  @Test
  public void testGetOutputWhenStandAloneAndCss() throws InvalidResourceTypeException {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    resource = context.create().resource("/ui-framework", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/ui-framework/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/ui-framework/css/file.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/ui-framework/css/file.css/jcr:content", fileJcrContentProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasVendorLibrary() throws Exception {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/etc/vendor-libraries/library-1/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasComponentsWithUiFrameworkView() throws Exception {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    context.create().resource("/apps/components");
    context.create().resource("/apps/components/component", componentTypeProperties);
    context.create().resource("/apps/components/component/test-code",
        componentTypeUiFrameworkViewProperties);

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/css",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/css/library-1.css",
        fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/apps/components/component/test-code/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasComponentsWithUiFrameworkViewVariation() throws Exception {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    context.create().resource("/apps/components");
    context.create().resource("/apps/components/component", componentTypeProperties);
    context.create().resource("/apps/components/component/test-code",
        componentTypeUiFrameworkViewProperties);

    context.create().resource("/apps/components/component/test-code/variations/variation",
        componentVariationProperties);

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/variations/variation/css",
        scriptTypeFolderProperties);

    context.create().resource(
        "/apps/components/component/test-code/variations/variation/css/library-1.css",
        fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource(
        "/apps/components/component/test-code/variations/variation/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetUncompiledCssOutputWhenHasVendorLibrary() throws Exception {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/etc/vendor-libraries/library-1/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetUncompiledCssOutputWhenHasComponentsWithUiFrameworkView() throws Exception {
    InputStream importerInputStream = new ByteArrayInputStream(".test-output{}".getBytes());

    context.create().resource("/apps/components");
    context.create().resource("/apps/components/component", componentTypeProperties);
    context.create().resource("/apps/components/component/test-code",
        componentTypeUiFrameworkViewProperties);

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/css",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/css/library-1.css",
        fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/apps/components/component/test-code/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetJavaScriptWhenHasVendorLibrary() throws Exception {
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");
    InputStream importerInputStream = new ByteArrayInputStream("console.log('test')".getBytes());

    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    scriptTypeFolderProperties.put("include", "library-1.js");
    context.create().resource("/etc/vendor-libraries/library-1/js", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/js/library-1.js", fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/etc/vendor-libraries/library-1/js/library-1.js/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("console.log('test')\n", uiFramework.getOutput(JAVASCRIPT));
  }

  @Test
  public void testGetJavaScriptWhenHasComponentsWithUiFrameworkView() throws Exception {
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");
    InputStream importerInputStream = new ByteArrayInputStream("console.log('test')".getBytes());

    context.create().resource("/apps/components");
    context.create().resource("/apps/components/component", componentTypeProperties);
    context.create().resource("/apps/components/component/test-code",
        componentTypeUiFrameworkViewProperties);

    scriptTypeFolderProperties.put("include", "library-1.js");
    context.create().resource("/apps/components/component/test-code/js",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/js/library-1.js",
        fileProperties);

    fileJcrContentProperties.put("jcr:data", importerInputStream);
    context.create().resource("/apps/components/component/test-code/js/library-1.js/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("console.log('test')\n", uiFramework.getOutput(JAVASCRIPT));
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectory() {
    context.create().resource("/apps/components/component-1", componentTypeProperties);
    context.create().resource("/apps/components/component-1/test-code",
        componentTypeUiFrameworkViewProperties);

    context.create().resource("/apps/components/component-2", componentTypeProperties);
    context.create().resource("/apps/components/component-2/test-code",
        componentTypeUiFrameworkViewProperties);

    properties.put("kes:uiFrameworkCode", "test-code");
    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getAllComponentUiFrameworkViewsInADirectory("/apps").size());
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectoryWhenInvalidType() {
    context.create().resource("/apps/components/component-1", componentTypeProperties);
    context.create().resource("/apps/components/component-1/test-code");

    context.create().resource("/apps/components/component-2", componentTypeProperties);
    context.create().resource("/apps/components/component-2/test-code");

    properties.put("kes:uiFrameworkCode", "test-code");
    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getAllComponentUiFrameworkViewsInADirectory("/apps").size());
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectoryWhenViewMatchesFrameworkName() {
    context.create().resource("/apps/components/component-1", componentTypeProperties);
    context.create().resource("/apps/components/component-1/ui-framework",
        componentTypeUiFrameworkViewProperties);

    context.create().resource("/apps/components/component-2", componentTypeProperties);
    context.create().resource("/apps/components/component-2/ui-framework",
        componentTypeUiFrameworkViewProperties);

    properties.put("kes:uiFrameworkCode", "test-code");
    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getAllComponentUiFrameworkViewsInADirectory("/apps").size());
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectoryWhenInvalidAndViewMatchesName() {
    // TODO is this test needed anymore?
    context.create().resource("/apps/components/component-1", componentTypeProperties);
    context.create().resource("/apps/components/component-1/ui-framework");

    context.create().resource("/apps/components/component-2", componentTypeProperties);
    context.create().resource("/apps/components/component-2/ui-framework");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getAllComponentUiFrameworkViewsInADirectory("/apps").size());
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectoryWhenHasCommon() {
    context.create().resource("/apps/components/component-1", componentTypeProperties);
    context.create().resource("/apps/components/component-1/common",
        componentTypeUiFrameworkViewProperties);

    context.create().resource("/apps/components/component-2", componentTypeProperties);
    context.create().resource("/apps/components/component-2/common",
        componentTypeUiFrameworkViewProperties);

    properties.put("kes:uiFrameworkCode", "test-code");
    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getAllComponentUiFrameworkViewsInADirectory("/apps").size());
  }

  @Test
  public void testGetTemplatesPath() throws ResourceNotFoundException {
    context.create().resource(
        "/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework.html");
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework.html",
        uiFramework.getTemplatesPath());
  }

}