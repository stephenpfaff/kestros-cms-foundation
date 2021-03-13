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

package io.kestros.cms.componenttypes.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.core.models.ComponentTypeResource;
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.uiframeworks.core.models.ManagedUiFrameworkResource;
import io.kestros.cms.uiframeworks.core.models.ManagedVendorLibraryResource;
import io.kestros.cms.uiframeworks.core.models.UiFrameworkResource;
import io.kestros.cms.uiframeworks.core.models.VendorLibraryResource;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class ComponentUiFrameworkViewRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentUiFrameworkViewRetrievalServiceImpl componentUiFrameworkViewRetrievalService;

  private ComponentTypeRetrievalServiceImpl componentTypeRetrievalService;

  private VendorLibraryRetrievalService vendorLibraryRetrievalService;

  private VersionService versionService;

  private ResourceResolverFactory resourceResolverFactory;

  private Resource resource;

  private ComponentType componentType;

  private ManagedUiFramework managedUiFramework1;
  private UiFramework managedUiFramework1Version1;
  private UiFramework managedUiFramework1Version2;
  private UiFramework managedUiFramework1Version3;
  private UiFramework managedUiFramework1Version4;

  private UiFramework uiFramework1;

  private ManagedVendorLibrary managedVendorLibrary1;
  private VendorLibrary managedVendorLibrary1Version1;
  private VendorLibrary managedVendorLibrary1Version2;
  private VendorLibrary managedVendorLibrary1Version3;
  private VendorLibrary managedVendorLibrary1Version4;

  private VendorLibrary vendorLibrary1;

  private Map<String, Object> componentTypeProperties = new HashMap<>();
  private Map<String, Object> managedUiFrameworkProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> managedVendorLibraryProperties = new HashMap<>();
  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    resourceResolverFactory = mock(ResourceResolverFactory.class);

    vendorLibraryRetrievalService = mock(VendorLibraryRetrievalService.class);

    versionService = new VersionServiceImpl();
    componentTypeRetrievalService = spy(new ComponentTypeRetrievalServiceImpl());
    componentUiFrameworkViewRetrievalService = spy(
        new ComponentUiFrameworkViewRetrievalServiceImpl());

    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);

    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");

    managedUiFrameworkProperties.put("jcr:primaryType", "kes:ManagedUiFramework");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    managedVendorLibraryProperties.put("jcr:primaryType", "kes:ManagedVendorLibrary");
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");

    uiFramework1 = context.create().resource("/etc/ui-frameworks/ui-framework-1",
        uiFrameworkProperties).adaptTo(UiFrameworkResource.class);
    vendorLibrary1 = context.create().resource("/etc/vendor-libraries/vendor-library-1",
        uiFrameworkProperties).adaptTo(VendorLibraryResource.class);

    managedUiFramework1 = context.create().resource("/etc/ui-frameworks/managed-ui-framework-1",
        managedUiFrameworkProperties).adaptTo(ManagedUiFrameworkResource.class);

    managedVendorLibrary1 = context.create().resource(
        "/etc/vendor-libraries/managed-vendor-library-1", managedVendorLibraryProperties).adaptTo(
        ManagedVendorLibraryResource.class);

    managedVendorLibrary1Version1 = context.create().resource(
        "/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.1",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);
    managedVendorLibrary1Version2 = context.create().resource(
        "/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.2",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);
    managedVendorLibrary1Version3 = context.create().resource(
        "/etc/vendor-libraries/managed-vendor-library-1/versions/0.1.0",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);
    managedVendorLibrary1Version4 = context.create().resource(
        "/etc/vendor-libraries/managed-vendor-library-1/versions/1.0.0",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);

    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"managed-vendor-library-1/0.0.1"});
    managedUiFramework1Version1 = context.create().resource(
        "/etc/ui-frameworks/managed-ui-framework-1/versions/0.0.1", uiFrameworkProperties).adaptTo(
        UiFrameworkResource.class);

    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"managed-vendor-library-1/0.0.2"});
    managedUiFramework1Version2 = context.create().resource(
        "/etc/ui-frameworks/managed-ui-framework-1/versions/0.0.2", uiFrameworkProperties).adaptTo(
        UiFrameworkResource.class);

    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"managed-vendor-library-1/0.1.0"});
    managedUiFramework1Version3 = context.create().resource(
        "/etc/ui-frameworks/managed-ui-framework-1/versions/0.1.0", uiFrameworkProperties).adaptTo(
        UiFrameworkResource.class);

    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"managed-vendor-library-1/1.0.0"});
    managedUiFramework1Version4 = context.create().resource(
        "/etc/ui-frameworks/managed-ui-framework-1/versions/1.0.0", uiFrameworkProperties).adaptTo(
        UiFrameworkResource.class);

    when(vendorLibraryRetrievalService.getVendorLibrary(eq("managed-vendor-library-1/0.0.1"),
        anyBoolean(), anyBoolean())).thenReturn(managedVendorLibrary1Version1);
    when(vendorLibraryRetrievalService.getVendorLibrary(eq("managed-vendor-library-1/0.0.2"),
        anyBoolean(), anyBoolean())).thenReturn(managedVendorLibrary1Version2);
    when(vendorLibraryRetrievalService.getVendorLibrary(eq("managed-vendor-library-1/0.1.0"),
        anyBoolean(), anyBoolean())).thenReturn(managedVendorLibrary1Version3);
    when(vendorLibraryRetrievalService.getVendorLibrary(eq("managed-vendor-library-1/1.0.0"),
        anyBoolean(), anyBoolean())).thenReturn(managedVendorLibrary1Version4);
  }

  @Test
  public void testGetCommonUiFrameworkView()
      throws InvalidCommonUiFrameworkException, InvalidComponentTypeException {
    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/common");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/common",
        componentUiFrameworkViewRetrievalService.getCommonUiFrameworkView(componentType).getPath());
  }

  @Test
  public void testGetCommonUiFrameworkViewWhenNotFound() {
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    Exception exception = null;
    try {
      componentUiFrameworkViewRetrievalService.getCommonUiFrameworkView(componentType);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(InvalidCommonUiFrameworkException.class, exception.getClass());
    assertEquals("Unable to retrieve 'common' ComponentUiFrameworkView for '/apps/component-type'.",
        exception.getMessage());
  }

  @Test
  public void testGetComponentUiFrameworkViewFromStandaloneUiFramework()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             LoginException, ChildResourceNotFoundException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/ui-framework-1");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/ui-framework-1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromStandaloneUiFramework(
            componentType, uiFramework1).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewFromStandaloneVendorLibrary()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             LoginException, ChildResourceNotFoundException, ResourceNotFoundException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/etc/vendor-libraries/vendor-library-1");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/etc/vendor-libraries/vendor-library-1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromStandaloneVendorLibrary(
            componentType, vendorLibrary1).getPath());
  }


  @Test
  public void testGetComponentUiFrameworkViewWithFallbackNoViews()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             LoginException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/common");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/common",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
            componentType, managedUiFramework1Version1).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewWithFallbackNoViewsWhenVendorLibraryHasView()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             LoginException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/common");
    context.create().resource(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.1.0");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/common",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
            componentType, managedUiFramework1Version1).getPath());
    assertEquals("/apps/component-type/common",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
            componentType, managedUiFramework1Version2).getPath());
    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.1.0",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
            componentType, managedUiFramework1Version3).getPath());
    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.1.0",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
            componentType, managedUiFramework1Version4).getPath());
  }


  @Test
  public void testGetComponentUiFrameworkViewWhenManagedUiFramework()
      throws LoginException, InvalidComponentUiFrameworkViewException,
             InvalidComponentTypeException, ChildResourceNotFoundException,
             InvalidResourceTypeException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/managed-ui-framework-1/versions/1.0.0");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/managed-ui-framework-1/versions/1.0.0",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromManagedUiFramework(
            componentType, managedUiFramework1, new Version(2, 0, 0)).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewWhenManagedVendorLibrary()
      throws InvalidComponentTypeException, InvalidComponentUiFrameworkViewException,
             InvalidResourceTypeException, ChildResourceNotFoundException, LoginException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/1.0.0");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/1.0.0",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromManagedVendorLibrary(
            componentType, managedVendorLibrary1, new Version(2, 0, 0)).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewFromVendorLibraryList()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             LoginException, ChildResourceNotFoundException, InvalidResourceTypeException,
             ResourceNotFoundException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.1");
    context.create().resource(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.2");
    context.create().resource(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/1.0.0");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromVendorLibraryList(
            componentType, managedUiFramework1Version1).getPath());
    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.2",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromVendorLibraryList(
            componentType, managedUiFramework1Version2).getPath());
    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.2",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromVendorLibraryList(
            componentType, managedUiFramework1Version3).getPath());
    assertEquals(
        "/apps/component-type/etc/vendor-libraries/managed-vendor-library-1/versions/1.0.0",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewFromVendorLibraryList(
            componentType, managedUiFramework1Version4).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewFromVendorLibrary() {
  }

  @Test
  public void testGetManagedComponentUiFrameworkViewFromManagedUiFramework()
      throws ChildResourceNotFoundException, InvalidResourceTypeException, LoginException {
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/managed-ui-framework-1/versions/1.0.0");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/managed-ui-framework-1",
        componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkViewFromManagedUiFramework(
            componentType, managedUiFramework1).getPath());
  }

  @Test
  public void testTestGetManagedComponentUiFrameworkView() {
  }

  @Test
  @Ignore
  public void testGetComponentViews() throws LoginException {
    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework-1");
    resource = context.create().resource("/apps/component-type-2", componentTypeProperties);
    context.create().resource("/apps/component-type-2/framework-1");
    resource = context.create().resource("/apps/component-type-3", componentTypeProperties);
    context.create().resource("/apps/component-type-3/framework-1");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentTypeRetrievalService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals(3,
        componentUiFrameworkViewRetrievalService.getComponentViews(uiFramework1, true, true,
            true).size());
  }


  @Test
  public void testTestGetComponentViews() {
  }

  @Test
  public void testGetUiFrameworkViews() {
  }

  @Test
  public void testGetDisplayName() {
  }

  @Test
  public void testGetServiceUserName() {
  }

  @Test
  public void testDeactivate() {
  }

  @Test
  public void testRunAdditionalHealthChecks() {
  }

  @Test
  public void testGetRequiredResourcePaths() {
  }

  @Test
  public void testGetResourceResolverFactory() {
  }

  @Test
  public void testGetPerformanceTrackerService() {
  }
}