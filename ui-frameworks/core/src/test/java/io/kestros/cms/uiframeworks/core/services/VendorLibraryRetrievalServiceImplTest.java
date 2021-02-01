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

import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class VendorLibraryRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VendorLibraryRetrievalServiceImpl vendorLibraryRetrievalService;

  private ResourceResolverFactory resourceResolverFactory;
  private VersionServiceImpl versionService;

  private Map<String, Object> managedVendorLibraryProperties = new HashMap<>();
  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  @Before
  public void setup() throws LoginException {
    context.addModelsForPackage("io.kestros");
    vendorLibraryRetrievalService = spy(new VendorLibraryRetrievalServiceImpl());
    resourceResolverFactory = mock(ResourceResolverFactory.class);
    versionService = new VersionServiceImpl();

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);

    managedVendorLibraryProperties.put("jcr:primaryType", "kes:ManagedVendorLibrary");
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");

    context.create().resource("/etc/vendor-libraries/managed-vendor-library-1",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.2",
        vendorLibraryProperties);

    context.create().resource("/etc/vendor-libraries/managed-vendor-library-2",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library-2/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library-2/versions/0.0.2",
        vendorLibraryProperties);

    context.create().resource("/libs/kestros/vendor-libraries/managed-vendor-library-3",
        managedVendorLibraryProperties);
    context.create().resource(
        "/libs/kestros/vendor-libraries/managed-vendor-library-3/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource(
        "/libs/kestros/vendor-libraries/managed-vendor-library-3/versions/0.0.2",
        vendorLibraryProperties);

    context.create().resource("/libs/kestros/vendor-libraries/managed-vendor-library-4",
        managedVendorLibraryProperties);
    context.create().resource(
        "/libs/kestros/vendor-libraries/managed-vendor-library-4/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource(
        "/libs/kestros/vendor-libraries/managed-vendor-library-4/versions/0.0.2",
        vendorLibraryProperties);

    context.create().resource("/etc/vendor-libraries/vendor-library-1", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-2", vendorLibraryProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibraryProperties);
    context.create().resource("/libs/kestros/vendor-libraries/vendor-library-4",
        vendorLibraryProperties);
  }

  @Test
  public void testGetManagedVendorLibraryWhenEtc()
      throws VendorLibraryRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals("/etc/vendor-libraries/managed-vendor-library-1",
        vendorLibraryRetrievalService.getManagedVendorLibrary("managed-vendor-library-1", true,
            false).getPath());
  }

  @Test
  public void testGetManagedVendorLibraryWhenLibs() throws VendorLibraryRetrievalException {
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals("/libs/kestros/vendor-libraries/managed-vendor-library-3",
        vendorLibraryRetrievalService.getManagedVendorLibrary("managed-vendor-library-3", false,
            true).getPath());
  }

  @Test
  public void testGetManagedVendorLibraryWhenLibraryNameIsDuplicatedInLibs()
      throws VendorLibraryRetrievalException {
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    context.create().resource("/libs/kestros/vendor-libraries/managed-vendor-library-1",
        managedVendorLibraryProperties);

    assertEquals("/etc/vendor-libraries/managed-vendor-library-1",
        vendorLibraryRetrievalService.getManagedVendorLibrary("managed-vendor-library-1", true,
            true).getPath());
  }

  @Test
  public void testGetManagedVendorLibraryWhenEtcAndLibsNotIncluded() {
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    Exception exception = null;

    try {
      vendorLibraryRetrievalService.getManagedVendorLibrary("managed-vendor-library-3", false,
          false);
    } catch (VendorLibraryRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(
        "Unable to adapt 'managed-vendor-library-3': Unable to adapt 'managed-vendor-library-3': "
        + "Neither /etc nor /libs/kestros were included in ManagedVendorLibrary lookup, no search"
        + " attempted.", exception.getMessage());
  }

  @Test
  public void testGetManagedVendorLibraryWhenLibraryNotFound() {
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    Exception exception = null;

    try {
      vendorLibraryRetrievalService.getManagedVendorLibrary("does-not-exist", true, true);
    } catch (VendorLibraryRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Unable to adapt 'does-not-exist': Unable to adapt 'does-not-exist' under "
                 + "'/etc/vendor-libraries': Child not found.", exception.getMessage());
  }

  @Test
  public void testGetManagedVendorLibraryWhenDuplicateInEtcAndLibs()
      throws VendorLibraryRetrievalException {
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    assertEquals("/etc/vendor-libraries/managed-vendor-library-1",
        vendorLibraryRetrievalService.getManagedVendorLibrary("managed-vendor-library-1", true,
            true).getPath());
  }


  @Test
  public void testGetVendorLibrary()
      throws VendorLibraryRetrievalException, VersionRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    assertEquals("/etc/vendor-libraries/vendor-library-1",
        vendorLibraryRetrievalService.getVendorLibrary("vendor-library-1", true, true).getPath());
  }

  @Test
  public void testGetVendorLibraryWhenLibs()
      throws VendorLibraryRetrievalException, VersionRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibraryRetrievalService.getVendorLibrary("vendor-library-3", true, true).getPath());
  }

  @Test
  public void testGetVendorLibraryWhenEtcAndLibsNotIncluded() throws LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    Exception exception = null;
    try {
      vendorLibraryRetrievalService.getVendorLibrary("vendor-library-3", false, false);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(VendorLibraryRetrievalException.class, exception.getClass());
    assertEquals(
        "Unable to adapt 'vendor-library-3': Neither /etc nor /libs/kestros were included in "
        + "VendorLibrary lookup, no search attempted.", exception.getMessage());
  }

  @Test
  public void testGetVendorLibraryWhenManagedVersion()
      throws VendorLibraryRetrievalException, VersionRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals("/etc/vendor-libraries/managed-vendor-library-1/versions/0.0.1",
        vendorLibraryRetrievalService.getVendorLibrary("managed-vendor-library-1/0.0.1", true,
            true).getPath());
  }

  @Test
  public void testGetVendorLibraryWhenManagedVersionDoesNotExist() {
    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    Exception exception = null;
    try {
      vendorLibraryRetrievalService.getVendorLibrary("managed-vendor-library-1/9.9.9", true, true);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(VersionRetrievalException.class, exception.getClass());
    assertEquals("Failed to find version 9.9.9 for ManagedVendorLibraryResource "
                 + "/etc/vendor-libraries/managed-vendor-library-1. Unable to adapt '9.9.9' under "
                 + "'/etc/vendor-libraries/managed-vendor-library-1/versions': Child not found.",
        exception.getMessage());
  }

  @Test
  public void testGetVendorLibraryWhenUnmanagedVersionDoesNotExist() {
    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    Exception exception = null;
    try {
      vendorLibraryRetrievalService.getVendorLibrary("missing-vendor-library", true, true);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(VendorLibraryRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve VendorLibrary missing-vendor-library. Resource was not found, or was "
        + "an invalid resourceType.", exception.getMessage());
  }

  @Test
  public void testGetVendorLibraryWhenVersionServiceIsNull() {
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    Exception exception = null;

    try {
      vendorLibraryRetrievalService.getVendorLibrary("managed-vendor-library-1/0.0.1", true, true);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(VersionRetrievalException.class, exception.getClass());
    assertEquals("Failed to find version 0.0.1 for ManagedVendorLibrary "
                 + "/etc/vendor-libraries/managed-vendor-library-1. Version Service was null.",
        exception.getMessage());
  }

  @Test
  public void testGetVendorLibraryWhenInvalidResourceType() throws LoginException {
    context.create().resource("/etc/vendor-libraries/invalid-vendor-library");
    context.create().resource("/libs/kestros/vendor-libraries/invalid-vendor-library");

    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    Exception exception = null;

    try {
      vendorLibraryRetrievalService.getVendorLibrary("invalid-vendor-library", true, true);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(VendorLibraryRetrievalException.class, exception.getClass());
    assertEquals(
        "Failed to retrieve VendorLibrary invalid-vendor-library. Resource was not found, or was "
        + "an invalid resourceType.", exception.getMessage());
  }

  @Test
  public void testGetAllManagedVendorLibraries() {
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    assertEquals(4, vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).size());
    assertEquals("/etc/vendor-libraries/managed-vendor-library-1",
        vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).get(0).getPath());
    assertEquals("/etc/vendor-libraries/managed-vendor-library-2",
        vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).get(1).getPath());
    assertEquals("/libs/kestros/vendor-libraries/managed-vendor-library-3",
        vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).get(2).getPath());
    assertEquals("/libs/kestros/vendor-libraries/managed-vendor-library-4",
        vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).get(3).getPath());
  }

  @Test
  public void testGetAllManagedVendorLibrariesWhenRootResourcesNotFound()
      throws LoginException, PersistenceException {
    context.resourceResolver().delete(context.resourceResolver().getResource("/etc/vendor-libraries"));
    context.resourceResolver().delete(context.resourceResolver().getResource("/libs/kestros/vendor-libraries"));
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals(0, vendorLibraryRetrievalService.getAllManagedVendorLibraries(true, true).size());
  }

  @Test
  public void testGetAllUnmanagedVendorLibrariesWhenEtc() throws LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    assertEquals(2,
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(true, false).size());
    assertEquals("/etc/vendor-libraries/vendor-library-1",
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(true, false).get(0).getPath());
  }

  @Test
  public void testGetAllUnmanagedVendorLibrariesWhenRootResourcesNotFound()
      throws LoginException, PersistenceException {
    context.resourceResolver().delete(context.resourceResolver().getResource("/etc/vendor-libraries"));
    context.resourceResolver().delete(context.resourceResolver().getResource("/libs/kestros/vendor-libraries"));
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals(0,
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(true, true).size());
  }

  @Test
  public void testGetAllUnmanagedVendorLibrariesWhenLibs() throws LoginException {
    doReturn(resourceResolverFactory).when(
        vendorLibraryRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());
    context.registerInjectActivateService(vendorLibraryRetrievalService);

    assertEquals(2,
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(false, true).size());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-3",
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(false, true).get(0).getPath());
    assertEquals("/libs/kestros/vendor-libraries/vendor-library-4",
        vendorLibraryRetrievalService.getAllUnmanagedVendorLibraries(false, true).get(1).getPath());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Vendor Library Retrieval Service",
        vendorLibraryRetrievalService.getDisplayName());
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = spy(context.componentContext());
    vendorLibraryRetrievalService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("vendor-library-retrieval", vendorLibraryRetrievalService.getServiceUserName());
  }

  @Test
  public void testGetRequiredResourcePaths() {
    assertEquals(2, vendorLibraryRetrievalService.getRequiredResourcePaths().size());
    assertEquals("/etc/vendor-libraries",
        vendorLibraryRetrievalService.getRequiredResourcePaths().get(0));
    assertEquals("/libs/kestros/vendor-libraries",
        vendorLibraryRetrievalService.getRequiredResourcePaths().get(1));
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerInjectActivateService(vendorLibraryRetrievalService);
    assertNotNull(vendorLibraryRetrievalService.getResourceResolverFactory());
  }
}