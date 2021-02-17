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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.core.models.ComponentTypeResource;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.cms.versioning.core.services.VersionServiceImpl;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class ComponentUiFrameworkViewRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentUiFrameworkViewRetrievalServiceImpl componentUiFrameworkViewRetrievalService;

  private VersionService versionService;

  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  private ComponentTypeRetrievalServiceImpl componentTypeRetrievalService;

  private ResourceResolverFactory resourceResolverFactory;

  private ComponentType componentType;

  private List<UiFramework> uiFrameworkList = new ArrayList<>();
  private UiFramework uiFramework1;
  private UiFramework uiFramework2;
  private UiFramework uiFramework3;

  private Resource resource;

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    versionService = new VersionServiceImpl();
    uiFrameworkRetrievalService = mock(UiFrameworkRetrievalService.class);
    componentTypeRetrievalService = spy(new ComponentTypeRetrievalServiceImpl());
    resourceResolverFactory = mock(ResourceResolverFactory.class);
    uiFramework1 = mock(UiFramework.class);
    uiFramework2 = mock(UiFramework.class);
    uiFramework3 = mock(UiFramework.class);
    componentUiFrameworkViewRetrievalService = spy(new ComponentUiFrameworkViewRetrievalServiceImpl());

    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");

    when(uiFramework1.getFrameworkCode()).thenReturn("framework-1");
    when(uiFramework2.getFrameworkCode()).thenReturn("framework-2");
    when(uiFramework3.getFrameworkCode()).thenReturn("framework-3");
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
  public void testGetComponentUiFrameworkView()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework-1");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework-1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewWhenUsingFrameworkName()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {

    when(uiFramework1.getName()).thenReturn("framework-1-name");

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework-1-name");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework-1-name",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewWhenNotFound() {
    when(uiFramework1.getPath()).thenReturn("/framework-1");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    Exception exception = null;

    try {
      componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(componentType,
          uiFramework1);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(InvalidComponentUiFrameworkViewException.class, exception.getClass());
    assertEquals(
        "Unable to retrieve ComponentUiFrameworkView for ComponentType '/apps/component-type' and"
        + " UiFramework '/framework-1'.", exception.getMessage());
  }


  @Test
  public void testGetComponentUiFrameworkViewWhenVersioned()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             VersionFormatException {

    Version version = new Version(0, 0, 1);
    when(uiFramework1.getVersion()).thenReturn(version);

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework-1/versions/0.0.1");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(versionService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework-1/versions/0.0.1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetComponentUiFrameworkViewWhenInheritingFromSuperType()
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException {

    context.create().resource("/apps/super-type", componentTypeProperties);
    context.create().resource("/apps/super-type/framework-1");
    componentTypeProperties.put("sling:resourceSuperType", "super-type");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/super-type/framework-1",
        componentUiFrameworkViewRetrievalService.getComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }


  @Test
  public void testGetManagedComponentUiFrameworkView()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {

    when(uiFramework1.getFrameworkCode()).thenReturn("framework");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework");
    context.create().resource("/apps/component-type/framework/versions");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework",
        componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetManagedComponentUiFrameworkViewWhenLibsComponentType()
      throws ChildResourceNotFoundException, InvalidResourceTypeException, LoginException {
    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    doReturn(resourceResolverFactory).when(
        componentUiFrameworkViewRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    when(uiFramework1.getFrameworkCode()).thenReturn("framework");
    resource = context.create().resource("/libs/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework");
    context.create().resource("/apps/component-type/framework/versions");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework",
        componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetManagedComponentUiFrameworkViewWhenFrameworkNameMatches()
      throws ChildResourceNotFoundException, InvalidResourceTypeException {

    when(uiFramework1.getName()).thenReturn("framework");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework");
    context.create().resource("/apps/component-type/framework/versions");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals("/apps/component-type/framework",
        componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkView(componentType,
            uiFramework1).getPath());
  }

  @Test
  public void testGetManagedComponentUiFrameworkViewWhenNoVersionsFolder() {

    when(uiFramework1.getName()).thenReturn("framework");
    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/framework");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    Exception exception = null;

    try {
      componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkView(componentType,
          uiFramework1);
    } catch (Exception e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(InvalidResourceTypeException.class, exception.getClass());
    assertEquals("Unable to adapt '/apps/component-type/framework-1' to "
                 + "ManagedComponentUiFrameworkViewResource: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetManagedComponentUiFrameworkViewWhenComponentTypeIsNotResourceModel() {
    componentType = mock(ComponentType.class);

    when(componentType.getPath()).thenReturn("/component-type");
    when(uiFramework1.getFrameworkCode()).thenReturn("framework");
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    Exception exception = null;

    try {
      componentUiFrameworkViewRetrievalService.getManagedComponentUiFrameworkView(componentType,
          uiFramework1);
    } catch (Exception e) {
      exception = e;
    }

    assertNotNull(exception);
    assertEquals(InvalidResourceTypeException.class, exception.getClass());
    assertEquals(
        "Unable to adapt '/component-type/framework' to ManagedComponentUiFrameworkViewResource: "
        + "Invalid resource" + " type.", exception.getMessage());
  }

  @Test
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
  public void testGetUiFrameworkViews() {
    uiFrameworkList.add(uiFramework1);
    uiFrameworkList.add(uiFramework2);
    uiFrameworkList.add(uiFramework3);

    when(uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(any(),
        any())).thenReturn(uiFrameworkList);

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/common");
    context.create().resource("/apps/component-type/framework-1");
    context.create().resource("/apps/component-type/framework-2");
    context.create().resource("/apps/component-type/not-a-framework");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals(3,
        componentUiFrameworkViewRetrievalService.getUiFrameworkViews(componentType, true,
            true).size());
    assertEquals("common", componentUiFrameworkViewRetrievalService.getUiFrameworkViews(componentType,
        true, true).get(0).getName());
    assertEquals("framework-1", componentUiFrameworkViewRetrievalService.getUiFrameworkViews(componentType,
        true, true).get(1).getName());
    assertEquals("framework-2", componentUiFrameworkViewRetrievalService.getUiFrameworkViews(componentType,
        true, true).get(2).getName());
  }

  @Test
  public void testGetUiFrameworkViewsWhenFrameworkDoesNotExist() {
    uiFrameworkList.add(uiFramework1);
    uiFrameworkList.add(uiFramework2);
    uiFrameworkList.add(uiFramework3);

    when(uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(any(),
        any())).thenReturn(uiFrameworkList);

    resource = context.create().resource("/apps/component-type", componentTypeProperties);
    context.create().resource("/apps/component-type/not-a-framework");

    componentType = resource.adaptTo(ComponentTypeResource.class);

    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);
    context.registerInjectActivateService(componentUiFrameworkViewRetrievalService);

    assertEquals(0,
        componentUiFrameworkViewRetrievalService.getUiFrameworkViews(componentType, true,
            true).size());
  }


  @Test
  public void testGetDisplayName() {
    assertEquals("Component UI Framework View Retrieval Service",
        componentUiFrameworkViewRetrievalService.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    componentUiFrameworkViewRetrievalService.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    componentUiFrameworkViewRetrievalService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
  }
}