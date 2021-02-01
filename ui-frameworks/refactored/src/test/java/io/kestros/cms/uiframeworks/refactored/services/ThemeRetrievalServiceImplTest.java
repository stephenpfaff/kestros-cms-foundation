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

package io.kestros.cms.uiframeworks.refactored.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.refactored.models.UiFrameworkResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class ThemeRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeRetrievalServiceImpl themeRetrievalService;

  private VersionServiceImpl versionService;

  private ResourceResolverFactory resourceResolverFactory;

  private UiFrameworkResource uiFramework;

  private Resource resource;

  private Map<String, Object> managedUiFrameworkProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    themeRetrievalService = spy(new ThemeRetrievalServiceImpl());
    versionService = new VersionServiceImpl();
    resourceResolverFactory = mock(ResourceResolverFactory.class);

    managedUiFrameworkProperties.put("jcr:primaryType", "kes:ManagedUiFramework");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    themeProperties.put("jcr:primaryType", "kes:Theme");
  }

  @Test
  public void testGetTheme() throws ThemeRetrievalException, LoginException {
    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals("/ui-framework/themes/theme-1",
        themeRetrievalService.getTheme("theme-1", uiFramework).getPath());
  }

  @Test
  public void testGetThemeWhenThemeNotFound() throws LoginException {
    resource = context.create().resource("/ui-framework", uiFrameworkProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getTheme("theme-1", uiFramework);
    } catch (ThemeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ThemeRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve theme theme-1 for UiFramework /ui-framework. Resource was not found, "
        + "or was an invalid resourceType.", exception.getMessage());
  }

  @Test
  public void testGetThemes() throws ThemeRetrievalException, LoginException {
    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);
    context.create().resource("/ui-framework/themes/theme-2", themeProperties);
    context.create().resource("/ui-framework/themes/theme-3", themeProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals(3, themeRetrievalService.getThemes(uiFramework).size());
  }

  @Test
  public void testGetThemesWhenInheritingFromPreviousVersion()
      throws ThemeRetrievalException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    resource = context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2",
        uiFrameworkProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals(2, themeRetrievalService.getThemes(uiFramework).size());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.2", themeRetrievalService.getThemes(
        uiFramework).get(0).getUiFramework().getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.2/themes/default",
        themeRetrievalService.getThemes(uiFramework).get(0).getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.2", themeRetrievalService.getThemes(
        uiFramework).get(1).getUiFramework().getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.2/themes/theme-1",
        themeRetrievalService.getThemes(uiFramework).get(1).getPath());
  }

  @Test
  public void testGetThemesWhenInheritingThroughMultipleVersions()
      throws ThemeRetrievalException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.3", uiFrameworkProperties);
    resource = context.create().resource("/etc/ui-frameworks/framework/versions/0.0.4",
        uiFrameworkProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals(2, themeRetrievalService.getThemes(uiFramework).size());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.4", themeRetrievalService.getThemes(
        uiFramework).get(0).getUiFramework().getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.4/themes/default",
        themeRetrievalService.getThemes(uiFramework).get(0).getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.4", themeRetrievalService.getThemes(
        uiFramework).get(1).getUiFramework().getPath());
    assertEquals("/etc/ui-frameworks/framework/versions/0.0.4/themes/theme-1",
        themeRetrievalService.getThemes(uiFramework).get(1).getPath());
  }

  @Test
  public void testGetThemesWhenNoThemesResourceOnAnyVersions()
      throws ThemeRetrievalException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.3", uiFrameworkProperties);
    resource = context.create().resource("/etc/ui-frameworks/framework/versions/0.0.4",
        uiFrameworkProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals(0, themeRetrievalService.getThemes(uiFramework).size());
  }

  @Test
  public void testGetThemesWhenInheritingAndVersionServiceIsNull()
      throws ThemeRetrievalException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    resource = context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2",
        uiFrameworkProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals(0, themeRetrievalService.getThemes(uiFramework).size());
  }

  @Test
  public void testGetVirtualTheme()
      throws InvalidThemeException, ThemeRetrievalException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    assertEquals("/etc/ui-frameworks/framework/versions/0.0.2/themes/default",
        themeRetrievalService.getVirtualTheme(
            "/etc/ui-frameworks/framework/versions/0.0.2/themes/default").getPath());
  }

  @Test
  public void testGetVirtualThemeWhenInvalidResourceType() throws LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default");
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getVirtualTheme(
          "/etc/ui-frameworks/framework/versions/0.0.2/themes/default");
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ThemeRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve themes for UiFramework /etc/ui-frameworks/framework/versions/0.0.2. "
        + "Resource was not found, or was an invalid resourceType.", exception.getMessage());
  }

  @Test
  public void testGetVirtualThemeWhenUiFrameworkNotFound() throws LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getVirtualTheme(
          "/etc/ui-frameworks/framework/versions/0.0.2/themes/default");
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(InvalidThemeException.class, exception.getClass());
    assertEquals("Unable to retrieve theme 'default'. /etc/ui-frameworks/framework/versions/0.0.2",
        exception.getMessage());
  }

  @Test
  public void testGetVirtualThemeWhenUiFrameworkIsInvalid() throws LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/default",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1/themes/theme-1",
        themeProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2");

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getVirtualTheme(
          "/etc/ui-frameworks/framework/versions/0.0.2/themes/default");
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(InvalidThemeException.class, exception.getClass());
    assertEquals("Unable to retrieve theme 'default'. /etc/ui-frameworks/framework/versions/0.0.2",
        exception.getMessage());
  }

  @Test
  public void testGetVirtualThemeSpecifiedUiFrameworkIsInvalid() throws LoginException {
    context.create().resource("/etc/ui-frameworks/framework", managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getVirtualTheme(
          "/etc/ui-frameworks/framework/versions/0.0.2/themes/default");
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ThemeRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve themes for UiFramework /etc/ui-frameworks/framework/versions/0.0.2. "
        + "Resource was not found, or was an invalid resourceType.", exception.getMessage());
  }

  @Test
  public void testGetVirtualThemeWhenNoManagedUiFramework()
      throws InvalidThemeException, LoginException {
    context.create().resource("/etc/ui-frameworks/framework/versions/0.0.2", uiFrameworkProperties);

    doReturn(resourceResolverFactory).when(themeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerService(VersionService.class, versionService);
    context.registerInjectActivateService(themeRetrievalService);

    Exception exception = null;
    try {
      themeRetrievalService.getVirtualTheme(
          "/etc/ui-frameworks/framework/versions/0.0.2/themes/default");
    } catch (ThemeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ThemeRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve themes for UiFramework /etc/ui-frameworks/framework/versions/0.0.2. "
        + "Resource was not found, or was an invalid resourceType.",
        exception.getMessage());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("theme-retrieval", themeRetrievalService.getServiceUserName());
  }

  @Test
  public void testGetRequiredResourcePaths() {
    assertEquals(0, themeRetrievalService.getRequiredResourcePaths().size());
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerInjectActivateService(themeRetrievalService);
    assertNotNull(themeRetrievalService.getResourceResolverFactory());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Theme Retrieval Service", themeRetrievalService.getDisplayName());
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = spy(context.componentContext());
    themeRetrievalService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }
}