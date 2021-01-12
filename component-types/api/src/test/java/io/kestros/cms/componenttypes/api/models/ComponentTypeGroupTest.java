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

package io.kestros.cms.componenttypes.api.models;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentTypeGroup;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentTypeGroupTest {

  @Rule
  public SlingContext context = new SlingContext();

  ComponentTypeGroup componentTypeGroup = new ComponentTypeGroup();

  private Map<String, Object> properties = new HashMap();

  @Before
  public void setup() {
    context.addModelsForPackage("io.kestros");
  }

  @Test
  public void testGetTitle() throws Exception {
    componentTypeGroup.setTitle("Group Title");

    assertEquals("Group Title", componentTypeGroup.getTitle());
  }

  @Test
  public void testGetComponentTypes() throws Exception {
    properties.put("jcr:title", "Component A");
    Resource componentType1Resource = context.create().resource("/apps/component-1", properties);
    properties.put("jcr:title", "Component B");
    Resource componentType2Resource = context.create().resource("/apps/component-2", properties);
    ComponentType componentType1 = componentType1Resource.adaptTo(ComponentType.class);
    ComponentType componentType2 = componentType2Resource.adaptTo(ComponentType.class);

    componentTypeGroup.addComponentType(componentType1);
    componentTypeGroup.addComponentType(componentType2);

    assertEquals(2, componentTypeGroup.getComponentTypes().size());
  }


    @Test
    public void testGetComponentTypesSorted() throws Exception {
        properties.put("jcr:title", "C");
        Resource componentType1Resource = context.create().resource("/apps/component-1", properties);
        properties.put("jcr:title", "A");
        Resource componentType2Resource = context.create().resource("/apps/component-2", properties);
        properties.put("jcr:title", "B");
        Resource componentType3Resource = context.create().resource("/apps/component-3", properties);
        ComponentType componentType1 = componentType1Resource.adaptTo(ComponentType.class);
        ComponentType componentType2 = componentType2Resource.adaptTo(ComponentType.class);
        ComponentType componentType3 = componentType3Resource.adaptTo(ComponentType.class);

        componentTypeGroup.addComponentType(componentType1);
        componentTypeGroup.addComponentType(componentType2);
        componentTypeGroup.addComponentType(componentType3);

        assertEquals(3, componentTypeGroup.getComponentTypes().size());
        assertEquals("A", componentTypeGroup.getComponentTypes().get(0).getTitle());
        assertEquals("B", componentTypeGroup.getComponentTypes().get(1).getTitle());
        assertEquals("C", componentTypeGroup.getComponentTypes().get(2).getTitle());
    }

  @Test
  public void testRemoveComponentType() throws Exception {
    properties.put("jcr:title", "C");
    Resource componentType1Resource = context.create().resource("/apps/component-1", properties);
    properties.put("jcr:title", "A");
    Resource componentType2Resource = context.create().resource("/apps/component-2", properties);
    properties.put("jcr:title", "B");
    Resource componentType3Resource = context.create().resource("/apps/component-3", properties);
    ComponentType componentType1 = componentType1Resource.adaptTo(ComponentType.class);
    ComponentType componentType2 = componentType2Resource.adaptTo(ComponentType.class);
    ComponentType componentType3 = componentType3Resource.adaptTo(ComponentType.class);

    componentTypeGroup.addComponentType(componentType1);
    componentTypeGroup.addComponentType(componentType2);
    componentTypeGroup.addComponentType(componentType3);

    assertEquals(3, componentTypeGroup.getComponentTypes().size());
    assertEquals("A", componentTypeGroup.getComponentTypes().get(0).getTitle());
    assertEquals("B", componentTypeGroup.getComponentTypes().get(1).getTitle());
    assertEquals("C", componentTypeGroup.getComponentTypes().get(2).getTitle());

    componentTypeGroup.removeComponentType(componentType1);
  }

}