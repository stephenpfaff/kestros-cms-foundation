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

package io.kestros.cms.foundation.content.components.parentcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.EditModeService;
import io.kestros.cms.foundation.services.impl.BaseScriptProviderService;
import io.kestros.cms.foundation.services.impl.CachedScriptProviderService;
import io.kestros.cms.foundation.services.ComponentViewScriptResolutionCacheService;
import io.kestros.cms.foundation.services.impl.BaseThemeProviderService;
import io.kestros.cms.foundation.services.ThemeProviderService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParentComponentEditContextTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ParentComponentEditContext parentComponentEditContext;

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();

  private BaseScriptProviderService baseScriptProviderService = new BaseScriptProviderService();

  private ComponentViewScriptResolutionCacheService componentViewScriptResolutionCacheService;

  private EditModeService editModeService;

  private Theme editModeTheme;

  private Resource resource;

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

  private Map<String, Object> requestParameters = new HashMap();

  private Exception exception = null;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    componentViewScriptResolutionCacheService = spy(new CachedScriptProviderService());

    context.registerInjectActivateService(componentViewScriptResolutionCacheService);
    context.registerInjectActivateService(baseScriptProviderService);
    context.registerService(ThemeProviderService.class, themeProviderService);

    editModeService = mock(EditModeService.class);
    context.registerService(EditModeService.class, editModeService);

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    editModeTheme = mock(Theme.class);

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
  public void testIsEditMode() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    requestParameters.put("editMode", true);
    context.request().setParameterMap(requestParameters);

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenFalse() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    requestParameters.put("editMode", false);
    context.request().setParameterMap(requestParameters);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringTrue() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    requestParameters.put("editMode", "true");
    context.request().setParameterMap(requestParameters);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringFalse() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    requestParameters.put("editMode", "false");
    context.request().setParameterMap(requestParameters);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }


  @Test
  public void testIsEditModeWhenAttributeNotFound() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenNull() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", null);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }


  @Test
  public void testIsEditModeWhenEditModeIsNotActive() {
    when(editModeService.isEditModeActive()).thenReturn(false);
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenEditModeServiceIsNull() {
    editModeService = null;
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testGetEditTheme() throws InvalidThemeException {
    when(editModeService.isEditModeActive()).thenReturn(true);
    when(editModeService.getEditModeTheme(any())).thenReturn(editModeTheme);

    when(editModeTheme.getPath()).thenReturn("/edit-mode-theme");

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertEquals("/edit-mode-theme", parentComponentEditContext.getEditTheme().getPath());
  }

  @Test
  public void testGetEditThemeWhenEditModeIsNotActive() throws InvalidThemeException {
    when(editModeService.isEditModeActive()).thenReturn(false);

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertNull(parentComponentEditContext.getEditTheme());
  }

  @Test
  public void testGetEditThemeWhenEditModeServiceIsNull() throws InvalidThemeException {
    editModeService = null;

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertNull(parentComponentEditContext.getEditTheme());
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

    context.request().setResource(resource);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/apps/my-app/my-framework/content.html",
        parentComponentEditContext.getContentScriptPath());
  }

  @Test
  public void testGetScriptPathWhenUsingCommonFramework() throws Exception {

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
  public void testGetScriptPathWhenUsingFrameworkWhenCached() throws Exception {

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

}