/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.sitebuilding.api.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidUiFrameworkException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class ComponentRequestContextTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeProviderService themeProviderService = mock(ThemeProviderService.class);

  private Resource resource;

  private ComponentRequestContext componentRequestContext;

  private Theme theme;

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
    theme = mock(Theme.class);

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
  public void testGetCurrentPage() {
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals("/content/page", componentRequestContext.getCurrentPage().getPath());
  }

  @Test
  public void testGetCurrentPageWhenPagePathContainsJcrContent() {
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    context.requestPathInfo().setResourcePath("/content/page/jcr:content.html");
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals("/content/page", componentRequestContext.getCurrentPage().getPath());
  }

  @Test
  public void testGetCurrentPageWhenPagePathContainsUnderscoreJcrContent() {
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    context.requestPathInfo().setResourcePath("/content/page/_jcr_content.html");
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals("/content/page", componentRequestContext.getCurrentPage().getPath());
  }

  @Test
  public void testGetCurrentPageWhenInvalidResourceType() {
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.requestPathInfo().setResourcePath("/content/page/jcr:content.html");
    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertNull("/content/page", componentRequestContext.getCurrentPage());
  }

  @Test
  public void testGetCurrentPageWhenComponentResourceInvalid() {
    Resource mockResource = mock(Resource.class);
    when(mockResource.getPath()).thenReturn("/path");
    when(mockResource.adaptTo(any())).thenReturn(null);

    context.request().setResource(mockResource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertNull(componentRequestContext.getCurrentPage());
  }

  @Test
  public void testGetCurrentPageWhenResourceNotFound() {
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.requestPathInfo().setResourcePath("/invalid");
    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertNull("/content/page", componentRequestContext.getCurrentPage());
  }

  @Test
  public void testGetUiFrameworkWhenInvalidUiFrameworkException()
      throws InvalidUiFrameworkException, ResourceNotFoundException, InvalidThemeException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);
    when(theme.getUiFramework()).thenThrow(InvalidUiFrameworkException.class);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);
    assertNull(componentRequestContext.getUiFramework());
  }

  @Test
  public void testGetInlineVariations()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);
    variationProperties.put("inline", true);
    context.create().resource("/apps/my-app/my-framework/variations/variation-2",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals("variation-2", componentRequestContext.getInlineVariations());
  }

  @Test
  public void testGetComponentUiFrameworkView()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException,
             InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = spy(context.request().adaptTo(ComponentRequestContext.class));

    assertEquals("my-framework", componentRequestContext.getComponentUiFrameworkView().getName());
    verify(componentRequestContext, times(3)).getComponent();
    assertEquals("my-framework", componentRequestContext.getComponentUiFrameworkView().getName());
    assertEquals("my-framework", componentRequestContext.getComponentUiFrameworkView().getName());
    verify(componentRequestContext, times(3)).getComponent();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetComponentWhenInvalidResourceType()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException,
             InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {
    Resource mockResource = mock(Resource.class);
    when(mockResource.getPath()).thenReturn("/path");
    when(mockResource.adaptTo(any())).thenReturn(null);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(mockResource);
    componentRequestContext = spy(context.request().adaptTo(ComponentRequestContext.class));

    componentRequestContext.getComponent();
  }

  @Test
  public void testGetAppliedVariations()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

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

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(2, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-1", componentRequestContext.getAppliedVariations().get(0).getName());
    assertEquals("variation-2", componentRequestContext.getAppliedVariations().get(1).getName());

    assertEquals("variation-1 variation-2", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenInvalidResourceType()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework/variations/variation-1");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationsFolderDoesNotExist()
      throws InvalidUiFrameworkException, ResourceNotFoundException, InvalidThemeException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenComponentTypeNotFound() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    properties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    properties.put("sling:resourceType", "invalid-resource-type");
    resource = context.create().resource("/content", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenFrameworkScriptRootDoesNotExist()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationDoesNotExist()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(1, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-1", componentRequestContext.getAppliedVariations().get(0).getName());
    assertEquals("variation-1", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenUsingDefaultVariations()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);
    variationProperties.put("default", true);
    context.create().resource("/apps/my-app/my-framework/variations/variation-2",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(1, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-2", componentRequestContext.getAppliedVariations().get(0).getName());

    assertEquals("variation-2", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenUsingDefaultVariationsAndComponentViewIsInvalid()
      throws ResourceNotFoundException, InvalidThemeException, InvalidUiFrameworkException {
    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    UiFramework uiFramework = context.create().resource("/etc/ui-libraries/my-ui",
        uiFrameworkProperties).adaptTo(UiFramework.class);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);
    when(theme.getUiFramework()).thenReturn(uiFramework);

    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);
    variationProperties.put("default", true);
    context.create().resource("/apps/my-app/my-framework/variations/variation-2",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(1, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-2", componentRequestContext.getAppliedVariations().get(0).getName());

    assertEquals("variation-2", componentRequestContext.getWrapperVariations());
  }

}