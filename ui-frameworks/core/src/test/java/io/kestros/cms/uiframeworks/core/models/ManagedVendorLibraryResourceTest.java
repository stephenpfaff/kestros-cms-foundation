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

package io.kestros.cms.uiframeworks.refactored.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ManagedVendorLibraryResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ManagedVendorLibraryResource managedVendorLibrary;

  private VersionServiceImpl versionService;

  private Resource resource;

  private Map<String, Object> managedVendorLibraryProperties = new HashMap<>();

  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    versionService = new VersionServiceImpl();

    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
  }

  @Test
  public void testGetFontAwesomeIcon() {
    managedVendorLibraryProperties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals("icon", managedVendorLibrary.getFontAwesomeIcon());
  }

  @Test
  public void testGetDocumentationUrl() {
    managedVendorLibraryProperties.put("documentationUrl", "url");
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals("url", managedVendorLibrary.getDocumentationUrl());
  }

  @Test
  public void testGetVersionResourceType() {
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals(VendorLibraryResource.class, managedVendorLibrary.getVersionResourceType());
  }

  @Test
  public void testGetVersions() {
    context.registerInjectActivateService(versionService);
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.2",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.3",
        vendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals(3, managedVendorLibrary.getVersions().size());
  }

  @Test
  public void testGetVersionsWhenVersionServiceIsNull() {
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.2",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.3",
        vendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals(0, managedVendorLibrary.getVersions().size());
  }

  @Test
  public void testGetCurrentVersion() {
    context.registerInjectActivateService(versionService);
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.2",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/3.0.0",
        vendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertEquals("3.0.0", managedVendorLibrary.getCurrentVersion().getResource().getName());
  }

  @Test
  public void testGetCurrentVersionWhenVersionServiceIsNull() {
    resource = context.create().resource("/etc/vendor-libraries/managed-vendor-library",
        managedVendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.1",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/0.0.2",
        vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/managed-vendor-library/versions/3.0.0",
        vendorLibraryProperties);

    managedVendorLibrary = resource.adaptTo(ManagedVendorLibraryResource.class);

    assertNull(managedVendorLibrary.getCurrentVersion());
  }
}