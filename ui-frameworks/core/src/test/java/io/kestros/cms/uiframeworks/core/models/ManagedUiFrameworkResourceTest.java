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

package io.kestros.cms.uiframeworks.core.models;

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

public class ManagedUiFrameworkResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ManagedUiFrameworkResource managedUiFramework;

  private VersionServiceImpl versionService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    versionService = new VersionServiceImpl();

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
  }

  @Test
  public void testGetFrameworkCode() {
    properties.put("kes:uiFrameworkCode", "code");

    resource = context.create().resource("/managed-framework", properties);
    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals("code", managedUiFramework.getFrameworkCode());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");

    resource = context.create().resource("/managed-framework", properties);
    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals("icon", managedUiFramework.getFontAwesomeIcon());
  }

  @Test
  public void testGetVersionResourceType() {
    resource = context.create().resource("/managed-framework", properties);
    resource = context.create().resource("/managed-framework/versions/0.0.1",
        uiFrameworkProperties);
    resource = context.create().resource("/managed-framework/versions/0.0.2",
        uiFrameworkProperties);
    resource = context.create().resource("/managed-framework/versions/0.0.3",
        uiFrameworkProperties);

    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals(UiFrameworkResource.class, managedUiFramework.getVersionResourceType());
  }

  @Test
  public void testGetVersions() {
    context.registerInjectActivateService(versionService);

    resource = context.create().resource("/managed-framework", properties);
    context.create().resource("/managed-framework/versions/0.0.1", uiFrameworkProperties);
    context.create().resource("/managed-framework/versions/0.0.2", uiFrameworkProperties);
    context.create().resource("/managed-framework/versions/3.0.0", uiFrameworkProperties);

    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals(3, managedUiFramework.getVersions().size());
  }

  @Test
  public void testGetVersionsWhenServiceIsNull() {
    resource = context.create().resource("/managed-framework", properties);

    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals(0, managedUiFramework.getVersions().size());
  }


  @Test
  public void testGetCurrentVersion() {
    context.registerInjectActivateService(versionService);
    resource = context.create().resource("/etc/ui-frameworks/managed-framework", properties);
    context.create().resource("/etc/ui-frameworks/managed-framework/versions/0.0.1",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework/versions/0.0.2",
        uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/managed-framework/versions/3.0.0",
        uiFrameworkProperties);

    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertEquals("3.0.0", managedUiFramework.getCurrentVersion().getResource().getName());
  }

  @Test
  public void testGetCurrentVersionWhenServiceIsNull() {
    resource = context.create().resource("/managed-framework", properties);

    managedUiFramework = resource.adaptTo(ManagedUiFrameworkResource.class);

    assertNull(managedUiFramework.getCurrentVersion());
  }
}