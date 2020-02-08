package io.kestros.cms.foundation.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.services.KestrosUserService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseContentPageTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private Resource resource;

  private BaseContentPage baseContentPage;

  private Map<String, Object> siteProperties = new HashMap<>();
  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> jcrContentProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();
  private Map<String, Object> themeVariationProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Map<String, Object> componentProperties = new HashMap<>();
  private ThemeProviderService themeProviderService = new BaseThemeProviderService();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private KestrosUserService userService;

  private KestrosUser user;

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    user = mock(KestrosUser.class);
    userService = mock(KestrosUserService.class);

    context.registerService(KestrosUserService.class, userService);
    context.registerService(ThemeProviderService.class, themeProviderService);

    when(userService.getUser("user", context.resourceResolver())).thenReturn(user);
    when(user.getId()).thenReturn("user");

    pageProperties.put("jcr:primaryType", "kes:Page");
    themeProperties.put("jcr:primaryType", "kes:Theme");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    componentProperties.put("sling:resourceType", "kestros/commons/components/content-area");

    resource = context.create().resource("/content/page", pageProperties);
    baseContentPage = resource.adaptTo(BaseContentPage.class);
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    exception = null;
  }

  @Test
  public void testGetDisplayTitle() {
    jcrContentProperties.put("displayTitle", "Display Title");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Display Title", baseContentPage.getDisplayTitle());
  }

  @Test
  public void testGetDisplayTitleWhenEmpty() {
    jcrContentProperties.put("jcr:title", "Title");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Title", baseContentPage.getDisplayTitle());
  }

  @Test
  public void testGetDisplayDescription() {
    jcrContentProperties.put("displayDescription", "Display Description");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Display Description", baseContentPage.getDisplayDescription());
  }

  @Test
  public void testGetDisplayDescriptionWhenEmpty() {
    jcrContentProperties.put("jcr:description", "Description");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Description", baseContentPage.getDisplayDescription());
  }

  @Test
  public void testGetMetaTitle() {
    jcrContentProperties.put("metaTitle", "Meta Title");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Meta Title", baseContentPage.getMetaTitle());
  }

  @Test
  public void testGetMetaTitleWhenEmpty() {
    jcrContentProperties.put("jcr:title", "Title");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Title", baseContentPage.getMetaTitle());
  }

  @Test
  public void testGetMetaDescription() {
    jcrContentProperties.put("metaDescription", "Meta Description");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Meta Description", baseContentPage.getMetaDescription());
  }

  @Test
  public void testGetMetaDescriptionWhenEmpty() {
    jcrContentProperties.put("jcr:description", "Description");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Description", baseContentPage.getMetaDescription());
  }

  @Test
  public void testGetTheme() throws ResourceNotFoundException, InvalidThemeException {
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("kes:theme", "/etc/themes/theme");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);
    context.create().resource("/etc/themes/theme", themeProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.doDetailedValidation();

    assertNotNull(baseContentPage.getTheme());
    assertEquals("/etc/themes/theme", baseContentPage.getTheme().getPath());
    assertEquals("theme", baseContentPage.getTheme().getName());

    assertFalse(baseContentPage.getErrorMessages().contains("Must have an assigned Theme."));

  }

  @Test
  public void testGetThemeWhenThemeIsInvalid() {
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("kes:theme", "/etc/themes/theme");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);
    context.create().resource("/etc/themes/theme");

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.doDetailedValidation();

    try {
      baseContentPage.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme '/etc/themes/theme'. Could not adapt to Theme. Resource must "
        + "have jcr:primaryType 'kes:Theme'.", exception.getMessage());
  }

  @Test
  public void testGetThemeWhenNotFound() {
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("themePath", "/etc/themes/theme-does-not-exist");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.doDetailedValidation();

    try {
      assertNull(baseContentPage.getTheme());
    } catch (ResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
    }
    assertEquals("Unable to adapt '': Theme reference resource missing or invalid.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeVariantWhenNoThemeIsSet() {
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("themeVariantPath", "/etc/themes/theme/variants/themeVariant");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    try {
      baseContentPage.getTheme();
    } catch (ResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
    }
    assertEquals("Unable to adapt '': Theme reference resource missing or invalid.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeVariantWhenResourceNotFound() {

    jcrContentProperties.put("kes:theme", "invalid-theme");
    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    try {
      baseContentPage.getTheme();
    } catch (ResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
    }
    assertEquals("Unable to adapt 'invalid-theme': Theme reference resource missing or invalid.",
        exception.getMessage());
  }

  @Test
  public void testGetSite() {
    siteProperties.put("jcr:primaryType", "kes:Site");

    context.create().resource("/site", siteProperties);
    context.create().resource("/site/jcr:content");
    resource = context.create().resource("/site/content/page/child-1", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.doDetailedValidation();

    assertEquals("/site", baseContentPage.getSite().getPath());
  }

  @Test
  public void testGetSiteWhenNoneFound() {
    context.create().resource("/site");
    resource = context.create().resource("/site/content/page/child-1", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.doDetailedValidation();

    assertNull(baseContentPage.getSite());
  }

  @Test
  public void testGetSiteWhenPageIsSite() {
    siteProperties.put("jcr:primaryType", "kes:Site");
    resource = context.create().resource("/site", siteProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("/site", baseContentPage.getSite().getPath());
  }

  @Test
  public void testGetAllComponents() {
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", jcrContentProperties);
    componentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content/component-1", componentProperties);
    context.create().resource("/page/jcr:content/component-2", componentProperties);
    context.create().resource("/page/jcr:content/component-3", componentProperties);
    componentProperties.put("sling:resourceType", "kestros/commons/components/content-area");
    context.create().resource("/page/jcr:content/content-area", componentProperties);
    componentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content/content-area/component-1", componentProperties);
    context.create().resource("/page/jcr:content/content-area/component-2", componentProperties);
    context.create().resource("/page/jcr:content/content-area/component-3", componentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals(6, baseContentPage.getAllComponents().size());
  }

  @Test
  public void testGetChildPages() throws Exception {
    context.create().resource("/content/page/child-1", pageProperties);
    context.create().resource("/content/page/child-2", pageProperties);
    context.create().resource("/content/page/child-3", pageProperties);

    baseContentPage.doDetailedValidation();

    assertEquals(3, baseContentPage.getChildPages().size());
    assertEquals("child-1", baseContentPage.getChildPages().get(0).getName());
    assertEquals("child-2", baseContentPage.getChildPages().get(1).getName());
    assertEquals("child-3", baseContentPage.getChildPages().get(2).getName());
  }

  @Test
  public void testGetChildPagesWhenJcrContent() throws Exception {
    pageProperties.put("jcr:primaryType", "kes:Page");
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);
    baseContentPage = resource.adaptTo(BaseContentPage.class);

    context.create().resource("/content/page/child-1", pageProperties);
    context.create().resource("/content/page/child-2", pageProperties);
    context.create().resource("/content/page/child-3", pageProperties);

    baseContentPage.doDetailedValidation();

    assertEquals(3, baseContentPage.getChildPages().size());
    assertEquals("child-1", baseContentPage.getChildPages().get(0).getName());
    assertEquals("child-2", baseContentPage.getChildPages().get(1).getName());
    assertEquals("child-3", baseContentPage.getChildPages().get(2).getName());
  }

  @Test
  public void testGetChildPagesWhenNoChildren() throws Exception {
    assertEquals(0, baseContentPage.getChildPages().size());
  }

  @Test
  public void testGetContentComponent() {
    resource = context.create().resource("/page");
    context.create().resource("/page/jcr:content");

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("jcr:content", baseContentPage.getContentComponent().getName());
  }

  @Test
  public void testGetContentComponentWhenMissing() {
    resource = context.create().resource("/page");

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("page", baseContentPage.getContentComponent().getName());
  }

  @Test
  public void testGetComponentType() throws InvalidResourceTypeException, ResourceNotFoundException,
                                            InvalidComponentTypeException {
    context.create().resource("/apps/component", componentTypeProperties);

    resource = context.create().resource("/page", pageProperties);

    jcrContentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("/apps/component", baseContentPage.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenFallsBackToLibs()
      throws InvalidResourceTypeException, ResourceNotFoundException,
             InvalidComponentTypeException {
    context.create().resource("/libs/component", componentTypeProperties);

    resource = context.create().resource("/page", pageProperties);

    jcrContentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("/libs/component", baseContentPage.getComponentType().getPath());
  }

  @Test
  public void testGetComponentTypeWhenComponentTypeIsInvalid() {
    Exception exception = null;
    context.create().resource("/libs/component", componentProperties);

    resource = context.create().resource("/page", pageProperties);

    jcrContentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    try {
      baseContentPage.getComponentType();
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals("Unable to adapt '/page': Invalid or missing ComponentType resource.",
        exception.getMessage());
  }

  @Test
  public void testGetComponentTypeWhenNoJcrContent() {
    Exception exception = null;
    context.create().resource("/apps/component", componentTypeProperties);

    resource = context.create().resource("/page", pageProperties);

    jcrContentProperties.put("sling:resourceType", "component");

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    try {
      baseContentPage.getComponentType();
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }

    assertEquals("Unable to adapt '/page': Invalid or missing ComponentType resource.",
        exception.getMessage());
  }

  @Test
  public void testGetAllowedUiFrameworks() {
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/ui-framework-4", uiFrameworkProperties);

    jcrContentProperties.put("allowedUiFrameworks",
        new String[]{"/etc/ui-frameworks/ui-framework-1", "/etc/ui-frameworks/ui-framework-2"});

    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals(2, baseContentPage.getAllowedUiFrameworks().size());
    assertEquals("/etc/ui-frameworks/ui-framework-1",
        baseContentPage.getAllowedUiFrameworks().get(0).getPath());
    assertEquals("/etc/ui-frameworks/ui-framework-2",
        baseContentPage.getAllowedUiFrameworks().get(1).getPath());
  }

  @Test
  public void testGetAllowedUiFrameworksWhenNotSpecified() {
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-3", uiFrameworkProperties);

    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals(3, baseContentPage.getAllowedUiFrameworks().size());
    assertEquals("/etc/ui-frameworks/ui-framework-1",
        baseContentPage.getAllowedUiFrameworks().get(0).getPath());
    assertEquals("/etc/ui-frameworks/ui-framework-2",
        baseContentPage.getAllowedUiFrameworks().get(1).getPath());
    assertEquals("/etc/ui-frameworks/ui-framework-3",
        baseContentPage.getAllowedUiFrameworks().get(2).getPath());
  }

  @Test
  public void testGetTopLevelComponents() {
    resource = context.create().resource("/page", pageProperties);

    context.create().resource("/page/jcr:content");
    context.create().resource("/page/jcr:content/component-1", componentProperties);
    context.create().resource("/page/jcr:content/component-1/child-1", componentProperties);
    context.create().resource("/page/jcr:content/component-2", componentProperties);
    context.create().resource("/page/jcr:content/component-2/child-1", componentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals(2, baseContentPage.getTopLevelComponents().size());
    assertEquals("component-1", baseContentPage.getTopLevelComponents().get(0).getName());
    assertEquals("component-2", baseContentPage.getTopLevelComponents().get(1).getName());

  }

  @Test
  public void testGetLastModifiedBy() {
    jcrContentProperties.put("kes:lastModifiedBy", "user");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("user", baseContentPage.getLastModifiedBy().getId());
  }


  @Test
  public void testGetCreatedBy() {
    jcrContentProperties.put("kes:createdBy", "user");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("user", baseContentPage.getCreatedBy().getId());
  }


  @Test
  public void testGetLastModified() {
    pageProperties.put("kes:lastModified", new Date().getTime());
    resource = context.create().resource("/page", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Just now", baseContentPage.getLastModified().getTimeAgo());
  }

  @Test
  public void testGetCreated() {
    pageProperties.put("kes:created", new Date().getTime());
    resource = context.create().resource("/page", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Just now", baseContentPage.getCreated().getTimeAgo());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    componentTypeProperties.put("fontAwesomeIcon", "icon-class");
    context.create().resource("/component-type", componentTypeProperties);

    jcrContentProperties.put("sling:resourceType", "/component-type");
    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    assertEquals("icon-class", baseContentPage.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenNotConfigured() {
    resource = context.create().resource("/page", pageProperties);

    assertEquals("fa fa-file", baseContentPage.getFontAwesomeIcon());
  }

}