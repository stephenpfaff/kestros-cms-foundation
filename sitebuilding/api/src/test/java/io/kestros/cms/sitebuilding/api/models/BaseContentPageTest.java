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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.services.AllowedComponentTypeService;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.componenttypes.core.models.ComponentTypeResource;
import io.kestros.cms.sitebuilding.api.services.AllowedUiFrameworkService;
import io.kestros.cms.sitebuilding.api.services.ThemeProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.cms.user.KestrosUser;
import io.kestros.cms.user.services.KestrosUserService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseContentPageTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeProviderService themeProviderService;

  private ComponentTypeRetrievalService componentTypeRetrievalService;

  private AllowedComponentTypeService allowedComponentTypeService;

  private AllowedUiFrameworkService allowedUiFrameworkService;

  private ComponentType componentType;

  private Resource resource;

  private BaseContentPage baseContentPage;

  private Map<String, Object> siteProperties = new HashMap<>();
  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> jcrContentProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();
  private Map<String, Object> themeVariationProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Map<String, Object> componentProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private KestrosUserService userService;

  private KestrosUser user;

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    allowedComponentTypeService = mock(AllowedComponentTypeService.class);
    allowedUiFrameworkService = mock(AllowedUiFrameworkService.class);
    componentTypeRetrievalService = mock(ComponentTypeRetrievalService.class);
    componentType = mock(ComponentType.class);
    themeProviderService = mock(ThemeProviderService.class);
    userService = mock(KestrosUserService.class);
    user = mock(KestrosUser.class);

    context.registerService(KestrosUserService.class, userService);

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
  public void testGetDisplayTitleWhenEmptyString() {
    jcrContentProperties.put("jcr:title", "Title");
    jcrContentProperties.put("displayTitle", "");

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
  public void testGetDisplayDescriptionWhenEmptyString() {
    jcrContentProperties.put("jcr:description", "Description");
    jcrContentProperties.put("metaDescription", "");

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
  public void testGetMetaTitleWhenEmptyString() {
    jcrContentProperties.put("jcr:title", "Title");
    jcrContentProperties.put("metaTitle", "");

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
  public void testGetMetaDescriptionWhenEmptyString() {
    jcrContentProperties.put("jcr:description", "Description");
    jcrContentProperties.put("metaDescription", "");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("Description", baseContentPage.getMetaDescription());
  }

  @Test
  public void testGetTheme()
      throws ResourceNotFoundException, InvalidThemeException, ThemeRetrievalException {
    context.registerService(ThemeProviderService.class, themeProviderService);

    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("kes:theme", "/etc/themes/theme");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);
    Theme theme = context.create().resource("/etc/themes/theme", themeProperties).adaptTo(
        ThemeResource.class);

    when(themeProviderService.getThemeForPage(any())).thenReturn(theme);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertNotNull(baseContentPage.getTheme());
    assertEquals("/etc/themes/theme", baseContentPage.getTheme().getPath());
    assertEquals("theme", baseContentPage.getTheme().getName());
  }

  @Test
  public void testGetThemeWhenThemeIsInvalid()
      throws ResourceNotFoundException, InvalidThemeException, ThemeRetrievalException {
    context.registerService(ThemeProviderService.class, themeProviderService);
    jcrContentProperties.put("jcr:primaryType", "nt:unstructured");
    jcrContentProperties.put("kes:theme", "/etc/themes/theme");

    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);
    context.create().resource("/etc/themes/theme", themeProperties);

    when(themeProviderService.getThemeForPage(any())).thenThrow(InvalidThemeException.class);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    try {
      baseContentPage.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(InvalidThemeException.class, exception.getClass());
  }

  @Test
  public void testGetSite() {
    siteProperties.put("jcr:primaryType", "kes:Site");

    context.create().resource("/site", siteProperties);
    context.create().resource("/site/jcr:content");
    resource = context.create().resource("/site/content/page/child-1", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("/site", baseContentPage.getSite().getPath());
  }

  @Test
  public void testGetSiteWhenNoneFound() {
    context.create().resource("/site");
    resource = context.create().resource("/site/content/page/child-1", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

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
  public void testGetContentComponentWhenResourceIsJcrContent() {
    resource = spy(context.create().resource("/jcr:content"));
    doReturn(null).when(resource).getParent();

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("jcr:content", baseContentPage.getContentComponent().getName());
  }

  @Test
  public void testGetContentComponentWhenPageNameIsJcrContent() {
    resource = spy(context.create().resource("/jcr:content"));
    context.create().resource("/jcr:content/jcr:content");
    doReturn(null).when(resource).getParent();

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("/jcr:content/jcr:content", baseContentPage.getContentComponent().getPath());
  }


  @Test
  public void testGetContentComponentWhenNoJcrContentResource() {
    resource = context.create().resource("/page");

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    assertEquals("page", baseContentPage.getContentComponent().getName());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetContentComponentWhenPageResourceFailsComponentAdaption() {
    resource = spy(context.create().resource("/page"));
    doReturn(null).when(resource).adaptTo(BaseComponent.class);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    baseContentPage.getContentComponent().getName();
  }

  @Test
  public void testGetComponentType()
      throws InvalidResourceTypeException, ResourceNotFoundException, InvalidComponentTypeException,
             ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
    ComponentTypeResource componentTypeResource = context.create().resource("/apps/component",
        componentTypeProperties).adaptTo(ComponentTypeResource.class);

    resource = context.create().resource("/page", pageProperties);

    jcrContentProperties.put("sling:resourceType", "component");
    context.create().resource("/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    when(componentTypeRetrievalService.getComponentType("component")).thenReturn(
        componentTypeResource);

    assertEquals("/apps/component", baseContentPage.getComponentType().getPath());
  }
  //
  //  @Test
  //  public void testGetComponentTypeWhenFallsBackToLibs()
  //      throws InvalidResourceTypeException, ResourceNotFoundException,
  //             InvalidComponentTypeException {
  //    context.create().resource("/libs/component", componentTypeProperties);
  //
  //    resource = context.create().resource("/page", pageProperties);
  //
  //    jcrContentProperties.put("sling:resourceType", "component");
  //    context.create().resource("/page/jcr:content", jcrContentProperties);
  //
  //    baseContentPage = resource.adaptTo(BaseContentPage.class);
  //
  //    assertEquals("/libs/component", baseContentPage.getComponentType().getPath());
  //  }
  //
  //  @Test
  //  public void testGetComponentTypeWhenComponentTypeIsInvalid() {
  //    Exception exception = null;
  //    context.create().resource("/libs/component", componentProperties);
  //
  //    resource = context.create().resource("/page", pageProperties);
  //
  //    jcrContentProperties.put("sling:resourceType", "invalid-component-type");
  //    context.create().resource("/page/jcr:content", jcrContentProperties);
  //
  //    baseContentPage = resource.adaptTo(BaseContentPage.class);
  //
  //    try {
  //      baseContentPage.getComponentType();
  //    } catch (InvalidComponentTypeException e) {
  //      exception = e;
  //    }
  //    assertEquals(
  //        "Unable to adapt 'invalid-component-type' to ComponentType for resource /page.
  //        Invalid or"
  //        + " missing ComponentType resource.", exception.getMessage());
  //  }
  //
  //  @Test
  //  public void testGetComponentTypeWhenFallsBackToLibsAndLibsIsInvalid()
  //      throws InvalidResourceTypeException, ResourceNotFoundException,
  //             InvalidComponentTypeException {
  //    context.create().resource("/libs/component");
  //
  //    resource = context.create().resource("/page", pageProperties);
  //
  //    jcrContentProperties.put("sling:resourceType", "component");
  //    context.create().resource("/page/jcr:content", jcrContentProperties);
  //
  //    baseContentPage = resource.adaptTo(BaseContentPage.class);
  //
  //    try {
  //      baseContentPage.getComponentType();
  //    } catch (InvalidComponentTypeException e) {
  //      exception = e;
  //    }
  //    assertEquals("Unable to adapt 'component' to ComponentType for resource /page. Invalid or"
  //                 + " missing ComponentType resource.", exception.getMessage());
  //  }
  //
  //  @Test
  //  public void testGetComponentTypeWhenNoJcrContent() {
  //    Exception exception = null;
  //    context.create().resource("/apps/component", componentTypeProperties);
  //
  //    resource = context.create().resource("/page", pageProperties);
  //
  //    jcrContentProperties.put("sling:resourceType", "component");
  //
  //    baseContentPage = resource.adaptTo(BaseContentPage.class);
  //
  //    try {
  //      baseContentPage.getComponentType();
  //    } catch (InvalidComponentTypeException e) {
  //      exception = e;
  //    }
  //
  //    assertEquals(
  //        "Unable to adapt 'kes:Page' to ComponentType for resource /page. Invalid or missing "
  //        + "ComponentType resource.", exception.getMessage());
  //  }

  @Test
  public void testGetAllowedUiFrameworks() {
    context.registerService(AllowedUiFrameworkService.class, allowedUiFrameworkService);
    List<UiFramework> uiFrameworkList = new ArrayList<>();

    resource = context.create().resource("/page", pageProperties);
    baseContentPage = resource.adaptTo(BaseContentPage.class);
    when(allowedUiFrameworkService.getAllowedUiFrameworks()).thenReturn(uiFrameworkList);

    assertEquals(0, baseContentPage.getAllowedUiFrameworks().size());

    assertNotNull(allowedUiFrameworkService);
    verify(allowedUiFrameworkService, times(1)).getAllowedUiFrameworks();
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
  public void testGetFontAwesomeIcon() throws ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
    componentTypeProperties.put("fontAwesomeIcon", "icon-class");
    ComponentTypeResource componentTypeResource = context.create().resource("/component-type",
        componentTypeProperties).adaptTo(ComponentTypeResource.class);

    jcrContentProperties.put("sling:resourceType", "/component-type");
    resource = context.create().resource("/content/page/jcr:content", jcrContentProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    when(componentTypeRetrievalService.getComponentType("/component-type")).thenReturn(
        componentTypeResource);

    assertEquals("icon-class", baseContentPage.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenNotConfigured() throws ComponentTypeRetrievalException {
    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);

    resource = context.create().resource("/page", pageProperties);

    baseContentPage = resource.adaptTo(BaseContentPage.class);

    when(componentTypeRetrievalService.getComponentType(anyString())).thenThrow(
        new ComponentTypeRetrievalException("", ""));

    assertEquals("fa fa-file", baseContentPage.getFontAwesomeIcon());
  }

}