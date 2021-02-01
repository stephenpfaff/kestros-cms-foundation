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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.componenttypes.api.services.ComponentViewScriptResolutionCacheService;
import io.kestros.cms.sitebuilding.api.services.EditModeService;
import io.kestros.cms.sitebuilding.api.services.ScriptProviderService;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.models.Theme;
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

  private ThemeProviderService themeProviderService = mock(ThemeProviderService.class);

  private ScriptProviderService scriptProviderService = mock(ScriptProviderService.class);

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

    componentViewScriptResolutionCacheService = mock(
        ComponentViewScriptResolutionCacheService.class);

    context.registerService(ComponentViewScriptResolutionCacheService.class,
        componentViewScriptResolutionCacheService);
    context.registerService(ScriptProviderService.class, scriptProviderService);
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
  public void testGetScriptPath() throws InvalidScriptException, InvalidComponentTypeException {
    when(scriptProviderService.getScriptPath(any(), any(), any())).thenReturn("/path");
        context.create().resource("/content/page-with-framework", pageProperties);
        context.create().resource("/content/page-with-framework/jcr:content",
        pageContentProperties);
        resource = context.create().resource("/content/page-with-framework/jcr:content/component",
            properties);
        context.request().setResource(resource);

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/path", parentComponentEditContext.getScriptPath("script.html"));
  }

  @Test
  public void testGetContentScriptPathPath() throws InvalidScriptException, InvalidComponentTypeException {
    when(scriptProviderService.getScriptPath(any(), eq("content.html"), any())).thenReturn("/path");
    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content",
        pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);
    context.request().setResource(resource);

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);

    assertEquals("/path", parentComponentEditContext.getContentScriptPath());
  }


}