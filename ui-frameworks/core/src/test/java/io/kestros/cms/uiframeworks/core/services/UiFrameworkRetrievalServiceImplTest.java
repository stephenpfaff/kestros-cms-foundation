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

package io.kestros.cms.uiframeworks.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class UiFrameworkRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFrameworkRetrievalServiceImpl uiFrameworkRetrievalService;

  private VersionService versionService;

  private ResourceResolverFactory resourceResolverFactory;

  private Resource resource;

  private Map<String, Object> managedUiFrameworkProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    resourceResolverFactory = mock(ResourceResolverFactory.class);
    versionService = new VersionServiceImpl();

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

    uiFrameworkRetrievalService = spy(new UiFrameworkRetrievalServiceImpl());

    managedUiFrameworkProperties.put("jcr:primaryType", "kes:ManagedUiFramework");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    themeProperties.put("jcr:primaryType", "kes:Theme");

  }

  @Test
  public void testGetAllManagedUiFrameworksWhenEtc()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework2",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework3",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework4",
        managedUiFrameworkProperties);

    assertEquals(2, uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, false).size());
    assertEquals("/etc/ui-frameworks/managed-framework1",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, false).get(0).getPath());
    assertEquals("/etc/ui-frameworks/managed-framework2",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, false).get(1).getPath());
  }

  @Test
  public void testGetAllManagedUiFrameworksWhenLibs()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework2",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework3",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework4",
        managedUiFrameworkProperties);

    assertEquals(2, uiFrameworkRetrievalService.getAllManagedUiFrameworks(false, true).size());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework3",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(false, true).get(0).getPath());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework4",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(false, true).get(1).getPath());
  }

  @Test
  public void testGetAllManagedUiFrameworksWhenEtcAndLibs()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework2",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework3",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework4",
        managedUiFrameworkProperties);

    assertEquals(4, uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).size());
    assertEquals("/etc/ui-frameworks/managed-framework1",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).get(0).getPath());
    assertEquals("/etc/ui-frameworks/managed-framework2",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).get(1).getPath());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework3",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).get(2).getPath());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework4",
        uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).get(3).getPath());
  }

  @Test
  public void testGetAllManagedUiFrameworksWhenEtcAndLibsResourcesNotFound()
      throws UiFrameworkRetrievalException, LoginException, PersistenceException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    assertEquals(0, uiFrameworkRetrievalService.getAllManagedUiFrameworks(true, true).size());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksWhenEtc()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/framework1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework4", uiFrameworkProperties);

    assertEquals(2, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, false).size());
    assertEquals("/etc/ui-frameworks/framework1",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, false).get(0).getPath());
    assertEquals("/etc/ui-frameworks/framework2",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, false).get(1).getPath());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksWhenLibs()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/framework1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework4", uiFrameworkProperties);

    assertEquals(2, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(false, true).size());
    assertEquals("/libs/kestros/ui-frameworks/framework3",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(false, true).get(0).getPath());
    assertEquals("/libs/kestros/ui-frameworks/framework4",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(false, true).get(1).getPath());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksWhenEtcAndLibs()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/framework1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework4", uiFrameworkProperties);

    assertEquals(4, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).size());
    assertEquals("/etc/ui-frameworks/framework1",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).get(0).getPath());
    assertEquals("/etc/ui-frameworks/framework2",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).get(1).getPath());
    assertEquals("/libs/kestros/ui-frameworks/framework3",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).get(2).getPath());
    assertEquals("/libs/kestros/ui-frameworks/framework4",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).get(3).getPath());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksWhenEtcLibsResourcesDoNotExist()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    assertEquals(0, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworks(true, true).size());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksAndManagedUiFrameworkVersionsWhenEtc()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework1/versions/0.0.1",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework1/versions/0.0.2",
        uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework1/versions/0.0.1",
        uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework1/versions/0.0.2",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework4", uiFrameworkProperties);

    assertEquals(4, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(true, false).size());
    assertEquals("/etc/ui-frameworks/managed-framework1/versions/0.0.1",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(true, false).get(0).getPath());
    assertEquals("/etc/ui-frameworks/managed-framework1/versions/0.0.2",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(true, false).get(1).getPath());
    assertEquals("/etc/ui-frameworks/framework1",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(true, false).get(2).getPath());
    assertEquals("/etc/ui-frameworks/framework2",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(true, false).get(3).getPath());
  }

  @Test
  public void testGetAllUnmanagedUiFrameworksAndManagedUiFrameworkVersionsWhenLibs()
      throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework1",
        managedUiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework1/versions/0.0.1",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework1/versions/0.0.2",
        uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework2",
        managedUiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework2/versions/0.0.1",
        uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/managed-framework2/versions/0.0.2",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/framework2", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework3", uiFrameworkProperties);
    context.create().resource("/libs/kestros/ui-frameworks/framework4", uiFrameworkProperties);

    assertEquals(4, uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(false, true).size());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework2/versions/0.0.1",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(false, true).get(0).getPath());
    assertEquals("/libs/kestros/ui-frameworks/managed-framework2/versions/0.0.2",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(false, true).get(1).getPath());
    assertEquals("/libs/kestros/ui-frameworks/framework3",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(false, true).get(2).getPath());
    assertEquals("/libs/kestros/ui-frameworks/framework4",
        uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(false, true).get(3).getPath());
  }

  @Test
  public void testGetUiFrameworkWhenHasTheme() throws UiFrameworkRetrievalException {
    context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    resource = context.create().resource("/etc/ui-frameworks/framework/themes/theme",
        themeProperties);
    ThemeResource theme = resource.adaptTo(ThemeResource.class);

    assertEquals("/etc/ui-frameworks/framework",
        uiFrameworkRetrievalService.getUiFramework(theme).getPath());
  }

  @Test
  public void testGetUiFrameworkWhenHasThemeWithNoValidAncestor() {
    resource = context.create().resource("/etc/ui-frameworks/framework/themes/theme",
        themeProperties);

    ThemeResource theme = resource.adaptTo(ThemeResource.class);
    Exception exception = null;

    try {
      uiFrameworkRetrievalService.getUiFramework(theme);
    } catch (UiFrameworkRetrievalException e) {
      exception = e;
    }

    assertNotNull(exception);
    assertEquals(UiFrameworkRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve UiFramework for theme /etc/ui-frameworks/framework/themes/theme. "
        + "Framework Resource was not found, or was an invalid resourceType.",
        exception.getMessage());
  }

  @Test
  public void testGetUiFrameworkWhenHasPath() throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/framework",
        uiFrameworkRetrievalService.getUiFramework("/etc/ui-frameworks/framework").getPath());
  }

  @Test
  public void testGetUiFrameworkWhenResourceDoesNotExist() throws LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    Exception exception = null;

    try {
      uiFrameworkRetrievalService.getUiFramework("/etc/ui-frameworks/framework");
    } catch (UiFrameworkRetrievalException e) {
      exception = e;
    }

    assertNotNull(exception);
    assertEquals(UiFrameworkRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt '/etc/ui-frameworks/framework': Unable to adapt "
                 + "'/etc/ui-frameworks/framework': Resource not found.", exception.getMessage());
  }

  @Test
  public void testGetManagedUiFramework() throws UiFrameworkRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    context.create().resource("/etc/ui-frameworks/managed-framework", managedUiFrameworkProperties);

    assertEquals("/etc/ui-frameworks/managed-framework",
        uiFrameworkRetrievalService.getManagedUiFramework(
            "/etc/ui-frameworks/managed-framework").getPath());
  }

  @Test
  public void testGetManagedUiFrameworkWhenResourceNotFound() throws LoginException {
    doReturn(resourceResolverFactory).when(
        uiFrameworkRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    Exception exception = null;

    try {
      uiFrameworkRetrievalService.getManagedUiFramework("/etc/ui-frameworks/framework");
    } catch (UiFrameworkRetrievalException e) {
      exception = e;
    }

    assertNotNull(exception);
    assertEquals(UiFrameworkRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt '/etc/ui-frameworks/framework': Unable to adapt "
                 + "'/etc/ui-frameworks/framework': Resource not found.", exception.getMessage());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("ui-framework-retrieval", uiFrameworkRetrievalService.getServiceUserName());
  }

  @Test
  public void testGetRequiredResourcePaths() {
    assertEquals(2, uiFrameworkRetrievalService.getRequiredResourcePaths().size());
    assertEquals("/etc/ui-frameworks",
        uiFrameworkRetrievalService.getRequiredResourcePaths().get(0));
    assertEquals("/libs/kestros/ui-frameworks",
        uiFrameworkRetrievalService.getRequiredResourcePaths().get(1));
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerInjectActivateService(uiFrameworkRetrievalService);

    assertNotNull(uiFrameworkRetrievalService.getResourceResolverFactory());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Framework Retrieval Service", uiFrameworkRetrievalService.getDisplayName());
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = spy(context.componentContext());
    uiFrameworkRetrievalService.deactivate();
    verifyZeroInteractions(componentContext);
  }
}