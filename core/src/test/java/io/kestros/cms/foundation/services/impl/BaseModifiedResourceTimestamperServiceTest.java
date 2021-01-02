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

package io.kestros.cms.foundation.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.services.impl.BaseModifiedResourceTimestamperService;
import io.kestros.cms.user.KestrosUser;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseModifiedResourceTimestamperServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseModifiedResourceTimestamperService timestamperService;

  private KestrosUser kestrosUser;

  private BaseComponent component;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    kestrosUser = mock(KestrosUser.class);
    timestamperService = new BaseModifiedResourceTimestamperService();

    resource = context.create().resource("/component", properties);
    component = resource.adaptTo(BaseComponent.class);
  }

  @Test
  public void testHandleComponentCreationProperties() throws PersistenceException {
    timestamperService.handleComponentCreationProperties(component, "user",
        context.resourceResolver());
    assertEquals("user", component.getProperty("kes:createdBy", ""));
    assertNotNull(component.getProperty("kes:created", null));
    assertEquals("", component.getProperty("kes:lastModifiedBy", ""));
    assertNull(component.getProperty("kes:lastModified", null));
  }

  @Test
  public void testTestHandleComponentCreationProperties() throws PersistenceException {
    when(kestrosUser.getId()).thenReturn("user");
    timestamperService.handleComponentCreationProperties(component, kestrosUser,
        context.resourceResolver());
    assertEquals("user", component.getProperty("kes:createdBy", ""));
    assertNotNull(component.getProperty("kes:created", null));
    assertEquals("", component.getProperty("kes:lastModifiedBy", ""));
    assertNull(component.getProperty("kes:lastModified", null));
  }

  @Test
  public void testUpdateComponentLastModified() throws PersistenceException {
    timestamperService.updateComponentLastModified(component, "user", context.resourceResolver());
    assertEquals("", component.getProperty("kes:createdBy", ""));
    assertNull(component.getProperty("kes:created", null));
    assertEquals("user", component.getProperty("kes:lastModifiedBy", ""));
    assertNotNull(component.getProperty("kes:lastModified", null));
  }

  @Test
  public void testTestUpdateComponentLastModified() throws PersistenceException {
    when(kestrosUser.getId()).thenReturn("user");
    timestamperService.updateComponentLastModified(component, kestrosUser,
        context.resourceResolver());
    assertEquals("", component.getProperty("kes:createdBy", ""));
    assertNull(component.getProperty("kes:created", null));
    assertEquals("user", component.getProperty("kes:lastModifiedBy", ""));
    assertNotNull(component.getProperty("kes:lastModified", null));
  }
}