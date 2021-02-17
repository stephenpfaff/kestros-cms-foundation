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

package io.kestros.cms.sitebuilding.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.componenttypes.api.services.ComponentViewScriptResolutionCacheService;
import io.kestros.cms.sitebuilding.api.models.ParentComponent;
import io.kestros.cms.sitebuilding.api.models.ParentComponentEditContext;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseScriptProviderServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseScriptProviderService scriptProviderService;

  private ThemeProviderService themeProviderService;
  private ParentComponentEditContext parentComponentEditContext;
  private ComponentViewScriptResolutionCacheService componentViewScriptResolutionCacheService;

  private Resource resource;
  private ResourceResolverFactory resourceResolverFactory;
  private Exception exception;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> pageContentProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> componentProperties = new HashMap<>();
  private Map<String, Object> variationProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkViewProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();


  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    resourceResolverFactory = mock(ResourceResolverFactory.class);
    themeProviderService = new BaseThemeProviderService();
    componentViewScriptResolutionCacheService = spy(new CachedScriptProviderService());
    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    scriptProviderService = new BaseScriptProviderService();

    context.registerInjectActivateService(themeProviderService);

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

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
  public void testGetServiceUserName() {
    assertEquals("kestros-script-provider", scriptProviderService.getServiceUserName());
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerInjectActivateService(scriptProviderService);
    assertNotNull(scriptProviderService.getResourceResolverFactory());
  }
/*
  @Test
  public void testGetScriptPathWhenUsingFramework() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getScriptPath("content.html"));
  }

  @Test
  public void testGetScriptPathWhenUsingFrameworkWhenCacheServiceIsNull() throws Exception {
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getScriptPath("content.html"));
    verify(componentViewScriptResolutionCacheService, times(0)).cacheComponentViewScriptPath(any(),
        any(), any(), any(), any());
  }

  @Test
  public void testGetScriptPathWhenUsingCommonFramework() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "common");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/common", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/common/content.html", fileProperties);
    context.create().resource("/apps/my-app/common/content.html/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/common/content.html",
        parentComponentEditContext.getContentScriptPath());
  }

  @Test
  public void testGetScriptPathWhenNoScriptNotFound() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/common", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/common/content.html", fileProperties);
    context.create().resource("/apps/my-app/common/content.html/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    ParentComponent parentComponent = resource.adaptTo(ParentComponent.class);
    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/common/content.html",
        scriptProviderService.getScriptPath(parentComponent, "content.html", context.request()));
  }

  @Test
  public void testGetScriptPathWhenCommonViewNotFound() throws CacheRetrievalException {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    ParentComponent parentComponent = resource.adaptTo(ParentComponent.class);
    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    Exception exception = null;

    try {
      scriptProviderService.getScriptPath(parentComponent, "content.html", context.request());
    } catch (InvalidScriptException e) {
      exception = e;
    } catch (InvalidComponentTypeException e) {
      e.printStackTrace();
    }
    assertEquals(InvalidScriptException.class, exception.getClass());
    assertEquals(
        "Unable to adapt 'content.html' for ComponentUiFrameworkView 'Unable to retrieve theme "
        + "for resource /content/page-with-framework/jcr:content/component, with request URI /.':"
        + " Script not found.", exception.getMessage());
    verify(componentViewScriptResolutionCacheService, times(1)).getCachedScriptPath(any(), any(),
        any(), any());
    verify(componentViewScriptResolutionCacheService, never()).cacheComponentViewScriptPath(any(),
        any(), any(), any(), any());
  }

  @Test
  public void testGetScriptPathWhenFoundScriptIsInvalid() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/common", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/common/content.html", fileProperties);
    context.create().resource("/apps/my-app/common/content.html/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.txt");
    context.create().resource("/apps/my-app/my-framework/content.txt/jcr:content",
        fileJcrContentProperties);

    ParentComponent parentComponent = resource.adaptTo(ParentComponent.class);
    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/common/content.html",
        scriptProviderService.getScriptPath(parentComponent, "content.html", context.request()));
  }

  @Test
  public void testGetScriptPathWhenUsingFrameworkWhenCached() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getContentScriptPath());
    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getContentScriptPath());
    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getContentScriptPath());

    verify(componentViewScriptResolutionCacheService, times(3)).getCachedScriptPath(any(), any(),
        any(), any());
    verify(componentViewScriptResolutionCacheService, times(1)).cacheComponentViewScriptPath(any(),
        any(), any(), any(), any());

  }

  @Test
  public void testGetScriptPathWhenUsingFrameworkFromParameter() throws Exception {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("ui-framework", "my-framework");
    context.request().setParameterMap(parameterMap);
    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getContentScriptPath());
  }

  @Test
  public void testGetScriptPathWhenComponentTypeMissing() {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    properties.put("sling:resourceType", "invalid-resource-type");
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    try {
      parentComponentEditContext.getContentScriptPath();
    } catch (InvalidScriptException e) {
      exception = e;
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals(InvalidComponentTypeException.class, exception.getClass());
    assertEquals("Unable to adapt 'invalid-resource-type' to ComponentType for resource "
                 + "/content/page-with-framework/jcr:content/component. Invalid or missing "
                 + "ComponentType " + "resource.", exception.getMessage());
  }

  @Test
  public void testGetScriptPathWhenComponentTypeIsInvalid() {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    properties.put("sling:resourceType", "/etc/ui-frameworks/my-framework/themes/my-theme");
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    try {
      assertNull(parentComponentEditContext.getContentScriptPath());
    } catch (InvalidScriptException e) {
      exception = e;
    } catch (InvalidComponentTypeException e) {
      exception = e;
    }
    assertEquals(InvalidComponentTypeException.class, exception.getClass());
    assertEquals(
        "Unable to adapt '/etc/ui-frameworks/my-framework/themes/my-theme' to ComponentType for "
        + "resource /content/page-with-framework/jcr:content/component. Invalid or missing "
        + "ComponentType resource.", exception.getMessage());
  }

  @Test
  public void testGetScriptPathWhenNoParentPage() {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/my-theme");

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    Exception exception = null;
    try {
      parentComponentEditContext.getScriptPath("content.html");
    } catch (Exception e) {
      exception = e;
    }
    assertEquals(InvalidScriptException.class, exception.getClass());
  }

  @Test
  public void testGetScriptPathWhenInvalidTheme() {
    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(scriptProviderService);
    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-framework/themes/invalid-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);
    context.create().resource("/apps/my-app/my-framework/content.html", fileProperties);
    context.create().resource("/apps/my-app/my-framework/content.html/jcr:content",
        fileJcrContentProperties);

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    Exception exception = null;
    try {
      parentComponentEditContext.getScriptPath("content.html");
    } catch (Exception e) {
      exception = e;
    }
    assertEquals(InvalidScriptException.class, exception.getClass());
  }*/

}