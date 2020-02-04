package io.kestros.cms.foundation.content.parentcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.scriptprovider.BaseScriptProviderService;
import io.kestros.cms.foundation.services.scriptprovider.ScriptProviderService;
import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParentComponentTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();
  private BaseScriptProviderService baseScriptProviderService = new BaseScriptProviderService();

  private Resource resource;

  private ParentComponent parentComponent;

  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> pageContentProperties = new HashMap<>();

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> componentProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkViewProperties = new HashMap<>();

  private Map<String, Object> variationProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.registerService(ThemeProviderService.class, themeProviderService);
    context.registerService(ScriptProviderService.class, baseScriptProviderService);

    properties.put("sling:resourceType", "my-app");

    pageProperties.put("jcr:primaryType", "kes:Page");

    componentProperties.put("jcr:primaryType", "kes:ComponentType");
    uiFrameworkViewProperties.put("jcr:primaryType", "kes:ComponentUiFrameworkView");
    variationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    uiFrameworkProperties.put("kes:uiFrameworkCode", "my-framework");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    themeProperties.put("jcr:primaryType", "kes:Theme");

    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/html");

    context.create().resource("/apps/my-app", componentProperties);
    context.create().resource("/etc/ui-frameworks/my-framework", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/my-framework/themes/my-theme", themeProperties);
  }

  @Test
  public void testGetId() {
    properties.put("id", "my-id");
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-id", parentComponent.getId());
  }

  @Test
  public void testGetIdWhenMissing() {
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("", parentComponent.getId());
  }

  @Test
  public void testCssGetClass() {
    properties.put("class", "my-class");
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-class", parentComponent.getCssClass());
  }

  @Test
  public void testGetCssClassWhenMissing() {
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("", parentComponent.getCssClass());
  }


  @Test
  public void testGetAppliedVariations() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);
    context.create().resource("/apps/my-app/my-framework/variations/variation-2",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(2, parentComponent.getAppliedVariations().size());
    assertEquals("variation-1", parentComponent.getAppliedVariations().get(0).getName());
    assertEquals("variation-2", parentComponent.getAppliedVariations().get(1).getName());

    assertEquals("variation-1 variation-2 ", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetAppliedVariationsWhenInvalidResourceType() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework/variations/variation-1");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(0, parentComponent.getAppliedVariations().size());
    assertEquals("", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationsFolderDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(0, parentComponent.getAppliedVariations().size());
    assertEquals("", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetAppliedVariationsWhenComponentTypeNotFound() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    properties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    properties.put("sling:resourceType", "invalid-resource-type");
    resource = context.create().resource("/content", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(0, parentComponent.getAppliedVariations().size());
    assertEquals("", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetAppliedVariationsWhenFrameworkScriptRootDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(0, parentComponent.getAppliedVariations().size());
    assertEquals("", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals(1, parentComponent.getAppliedVariations().size());
    assertEquals("variation-1", parentComponent.getAppliedVariations().get(0).getName());
    assertEquals("variation-1 ", parentComponent.getAppliedVariationsAsString());
  }

  @Test
  public void testGetScriptPathWhenUsingFramework() throws Exception {

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("/apps/my-app/my-framework/content.html", parentComponent.getContentScriptPath());
  }

  @Test
  public void testGetScriptPathWhenComponentTypeMissing() {

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    properties.put("sling:resourceType", "invalid-resource-type");
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      parentComponent.getContentScriptPath();
    } catch (InvalidScriptException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'content.html' for ComponentUiFrameworkView 'Unable to adapt "
                 + "'invalid-resource-type': Invalid or missing ComponentType resource.': Script "
                 + "not found.",
        exception.getMessage());
  }

  @Test
  public void testGetScriptPathWhenComponentTypeIsInvalid() {

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    properties.put("sling:resourceType", "/etc/ui-frameworks/my-framework/themes/my-theme");
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      assertNull(parentComponent.getContentScriptPath());
    } catch (InvalidScriptException e) {
      exception = e;
    }
    assertEquals("Unable to adapt 'content.html' for ComponentUiFrameworkView 'Unable to adapt "
                 + "'/etc/ui-frameworks/my-framework/themes/my-theme': Invalid or missing "
                 + "ComponentType " + "resource.': Script not found.", exception.getMessage());
  }

  @Test
  public void testGetScriptPathWhenVariablePassed() {
  }

  @Test
  public void testGetTheme() throws Exception {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-theme", parentComponent.getTheme().getName());
  }

  @Test
  public void testGetThemeWhenInvalid() {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme");

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      parentComponent.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme '/etc/ui-frameworks/my-ui/themes/my-theme'. Unable to adapt "
        + "'/etc/ui-frameworks/my-ui/themes/my-theme' to Theme: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeWhenInheritedFromPage() throws Exception {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    context.create().resource("/content/page-with-framework", pageProperties);

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-theme", parentComponent.getTheme().getName());
  }

  @Test
  public void testGetThemeWhenNoContainingPage() {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      parentComponent.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve theme ''. No ancestor resource with configured Theme found.",
        exception.getMessage());
  }


  @Test
  public void testGetContainingPage() throws Exception {
  }

}