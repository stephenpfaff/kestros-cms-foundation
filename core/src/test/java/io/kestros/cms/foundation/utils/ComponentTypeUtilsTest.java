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

package io.kestros.cms.foundation.utils;

import static io.kestros.cms.foundation.utils.ComponentTypeUtils.getAppsRootResource;
import static io.kestros.cms.foundation.utils.ComponentTypeUtils.getLibsRootResource;
import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentTypeUtilsTest {

  @Rule
  public SlingContext context = new SlingContext();

  private Resource resource;

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");

    context.create().resource("/apps");
    componentTypeProperties.put("componentGroup", "Group 1");
    context.create().resource("/apps/component-type-1", componentTypeProperties);
    context.create().resource("/apps/parent/component-type-2", componentTypeProperties);
    componentTypeProperties.put("componentGroup", "Group 2");
    context.create().resource("/apps/parent/parent/component-type-3", componentTypeProperties);
    context.create().resource("/apps/parent/parent/parent/component-type-4",
        componentTypeProperties);
    context.create().resource("/libs");
    componentTypeProperties.put("componentGroup", "Group 3");
    context.create().resource("/libs/component-type-1", componentTypeProperties);
    context.create().resource("/libs/parent/component-type-2", componentTypeProperties);
    componentTypeProperties.put("componentGroup", "");
    context.create().resource("/libs/parent/parent/component-type-3", componentTypeProperties);
    context.create().resource("/libs/parent/parent/parent/component-type-4",
        componentTypeProperties);
  }

  @Test
  public void testGetAllComponentTypeGroups() {
    assertEquals(4, ComponentTypeUtils.getAllComponentTypeGroups(true, true, true,
        context.resourceResolver()).size());

    assertEquals("Group 1", ComponentTypeUtils.getAllComponentTypeGroups(true, false, false,
        context.resourceResolver()).get(0).getTitle());
    assertEquals("Group 2", ComponentTypeUtils.getAllComponentTypeGroups(true, false, false,
        context.resourceResolver()).get(1).getTitle());

    assertEquals("Group 3", ComponentTypeUtils.getAllComponentTypeGroups(true, true, false,
        context.resourceResolver()).get(2).getTitle());
    assertEquals("No Group", ComponentTypeUtils.getAllComponentTypeGroups(true, true, false,
        context.resourceResolver()).get(3).getTitle());
  }

  @Test
  public void testGetAllComponentTypeGroupsWhenAppsOnly() {
    assertEquals(2, ComponentTypeUtils.getAllComponentTypeGroups(true, false, false,
        context.resourceResolver()).size());

    assertEquals("Group 1", ComponentTypeUtils.getAllComponentTypeGroups(true, false, false,
        context.resourceResolver()).get(0).getTitle());
    assertEquals("Group 2", ComponentTypeUtils.getAllComponentTypeGroups(true, false, false,
        context.resourceResolver()).get(1).getTitle());
  }

  @Test
  public void testGetAllComponentTypeGroupsWhenLibsOnly() {
    assertEquals(2, ComponentTypeUtils.getAllComponentTypeGroups(false, true, false,
        context.resourceResolver()).size());
    assertEquals("Group 3", ComponentTypeUtils.getAllComponentTypeGroups(false, true, false,
        context.resourceResolver()).get(0).getTitle());
    assertEquals("No Group", ComponentTypeUtils.getAllComponentTypeGroups(false, true, false,
        context.resourceResolver()).get(1).getTitle());
  }

  @Test
  public void testGetComponentTypeGroups() {
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        false, context.resourceResolver());

    assertEquals(4, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        null).size());

    assertEquals("Group 1", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        null, null).get(0).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        null).get(0).getComponentTypes().size());

    assertEquals("Group 2", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        null, null).get(1).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        null).get(1).getComponentTypes().size());

    assertEquals("Group 3", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        null, null).get(2).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        null).get(2).getComponentTypes().size());

    assertEquals("No Group", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        null, null, null).get(3).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        null).get(3).getComponentTypes().size());
  }

  @Test
  public void testGetComponentTypeGroupsWhenFilteringByAllowedComponentPath() {
    List<String> allowedComponentPaths = Arrays.asList("/apps/component-type-1",
        "/libs/component-type-1", "/apps/parent/parent/parent/component-type-4",
        "/libs/parent/parent/parent/component-type-4");
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        false, context.resourceResolver());

    assertEquals(4,
        ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, allowedComponentPaths, null,
            null, null).size());

    assertEquals("Group 1", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(0).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(0).getComponentTypes().size());

    assertEquals("Group 2", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(1).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(1).getComponentTypes().size());

    assertEquals("Group 3", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(2).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(2).getComponentTypes().size());

    assertEquals("No Group", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(3).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(3).getComponentTypes().size());
  }


  @Test
  public void testGetComponentTypeGroupsWhenFilteringByExcludedComponentPath() {
    List<String> excludedComponentPaths = Arrays.asList("/apps/component-type-1",
        "/libs/component-type-1", "/apps/parent/parent/parent/component-type-4",
        "/libs/parent/parent/parent/component-type-4");
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        false, context.resourceResolver());

    assertEquals(4,
        ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, excludedComponentPaths,
            null, null).size());

    assertEquals("Group 1", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(0).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(0).getComponentTypes().size());

    assertEquals("Group 2", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(1).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(1).getComponentTypes().size());

    assertEquals("Group 3", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(2).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(2).getComponentTypes().size());

    assertEquals("No Group", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(3).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        excludedComponentPaths, null, null).get(3).getComponentTypes().size());
  }

  @Test
  public void testGetComponentTypeGroupsWhenFilteringByAllowedComponentPathAndHasDuplicationGroups() {
    List<String> allowedComponentPaths = Arrays.asList("/apps/component-type-1",
        "/libs/component-type-1", "/apps/parent/parent/component-type-3",
        "/apps/parent/parent/parent/component-type-4", "/libs/parent/parent/component-type-3",
        "/libs/parent/parent/parent/component-type-4");
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        false, context.resourceResolver());

    assertEquals(4,
        ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, allowedComponentPaths, null,
            null, null).size());

    assertEquals("Group 1", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(0).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(0).getComponentTypes().size());

    assertEquals("Group 2", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(1).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(1).getComponentTypes().size());

    assertEquals("Group 3", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(2).getTitle());
    assertEquals(1, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(2).getComponentTypes().size());

    assertEquals("No Group", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(3).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes,
        allowedComponentPaths, null, null, null).get(3).getComponentTypes().size());
  }

  @Test
  public void testGetComponentTypeGroupsWhenFilteringByAllowedGroupsNames() {
    List<String> allowedGroupNames = Arrays.asList("Group 1", "Group 3");
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        true, context.resourceResolver());

    assertEquals(2,
        ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, allowedGroupNames,
            null).size());

    assertEquals("Group 1", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        allowedGroupNames, null).get(0).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        allowedGroupNames, null).get(0).getComponentTypes().size());

    assertEquals("Group 3", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        allowedGroupNames, null).get(1).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        allowedGroupNames, null).get(1).getComponentTypes().size());
  }

  @Test
  public void testGetComponentTypeGroupsWhenFilteringByExcludedGroups() {
    List<String> excludedGroupNames = Arrays.asList("Group 1", "Group 3");
    List<ComponentType> allComponentTypes = ComponentTypeUtils.getAllComponentTypes(true, true,
        false, context.resourceResolver());

    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        excludedGroupNames).size());
    assertEquals("Group 2", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null,
        null, excludedGroupNames).get(0).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        excludedGroupNames).get(0).getComponentTypes().size());

    assertEquals("No Group", ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null,
        null, null, excludedGroupNames).get(1).getTitle());
    assertEquals(2, ComponentTypeUtils.getComponentTypeGroups(allComponentTypes, null, null, null,
        excludedGroupNames).get(1).getComponentTypes().size());
  }

  @Test
  public void testGetAllComponentTypes() throws ResourceNotFoundException {
    assertEquals(8, ComponentTypeUtils.getAllComponentTypes(true, true, true,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllComponentTypesWhenAppsIsMissing()
      throws ResourceNotFoundException, PersistenceException {
    context.resourceResolver().delete(context.resourceResolver().getResource("/apps"));
    assertEquals(4, ComponentTypeUtils.getAllComponentTypes(true, true, true,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllComponentTypesWhenLibsIsMissing()
      throws ResourceNotFoundException, PersistenceException {
    context.resourceResolver().delete(context.resourceResolver().getResource("/libs"));
    assertEquals(4, ComponentTypeUtils.getAllComponentTypes(true, true, true,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllComponentTypesWhenAppsOnly() throws ResourceNotFoundException {
    assertEquals(4, ComponentTypeUtils.getAllComponentTypes(true, false, true,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllComponentTypesWhenLibsOnly() throws ResourceNotFoundException {
    assertEquals(4, ComponentTypeUtils.getAllComponentTypes(false, true, true,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllComponentTypesWhenAppsAndLibsAreExcluded()
      throws ResourceNotFoundException {
    assertEquals(0, ComponentTypeUtils.getAllComponentTypes(false, false, false,
        context.resourceResolver()).size());
  }

  @Test
  public void testGetAllDescendantComponentTypes() throws ResourceNotFoundException {
    assertEquals(4, ComponentTypeUtils.getAllDescendantComponentTypes(
        getAppsRootResource(context.resourceResolver())).size());

  }

  @Test
  public void testGetAppsRootResource() throws ResourceNotFoundException {
    assertEquals("/apps", getAppsRootResource(context.resourceResolver()).getPath());
  }

  @Test
  public void testGetLibsRootResource() throws ResourceNotFoundException {
    assertEquals("/libs", getLibsRootResource(context.resourceResolver()).getPath());
  }
}