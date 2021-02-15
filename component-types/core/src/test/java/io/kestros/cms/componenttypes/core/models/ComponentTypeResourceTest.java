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

package io.kestros.cms.componenttypes.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentTypeResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentTypeResource componentType;

  private Resource resource;

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testGetResourceSuperType() {
    componentTypeProperties.put("sling:resourceSuperType", "super");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertEquals("super", componentType.getResourceSuperType());
  }

  @Test
  public void testGetProperties() {
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertNotNull(componentType.getProperties());
    assertEquals("kes:ComponentType", componentType.getProperties().get("jcr:primaryType"));
  }

  @Test
  public void testGetComponentGroup() {
    componentTypeProperties.put("componentGroup", "group-name");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertEquals("group-name", componentType.getComponentGroup());
  }

  @Test
  public void testGetComponentSuperType() throws InvalidComponentTypeException {
    context.create().resource("/apps/super", componentTypeProperties);
    componentTypeProperties.put("sling:resourceSuperType", "super");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertEquals("super", componentType.getComponentSuperType().getName());
  }

  @Test
  public void testIsBypassUiFrameworks() {
    componentTypeProperties.put("bypassUiFrameworks", Boolean.TRUE);
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertTrue(componentType.isBypassUiFrameworks());
  }

  @Test
  public void testIsAllowLibsCommonsComponents() {
    componentTypeProperties.put("allowLibsCommonsComponents", Boolean.TRUE);
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertTrue(componentType.isAllowLibsCommonsComponents());
  }


  @Test
  public void testIsEditable() {
    componentTypeProperties.put("editable", Boolean.TRUE);
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertTrue(componentType.isEditable());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    componentTypeProperties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    assertEquals("icon", componentType.getFontAwesomeIcon());
  }
}