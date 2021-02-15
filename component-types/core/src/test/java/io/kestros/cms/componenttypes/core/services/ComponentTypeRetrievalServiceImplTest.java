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

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class ComponentTypeRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentTypeRetrievalServiceImpl componentTypeRetrievalService;

  private ResourceResolverFactory resourceResolverFactory;

  private ComponentType componentType;

  private ComponentUiFrameworkView uiFrameworkView;

  private Resource resource;

  private Map<String, Object> componentTypeProperties = new HashMap<>();
  private Map<String, Object> viewProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentTypeRetrievalService = spy(new ComponentTypeRetrievalServiceImpl());
    resourceResolverFactory = mock(ResourceResolverFactory.class);

    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testGetComponentType() throws ComponentTypeRetrievalException, LoginException {
    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    context.create().resource("/apps/component-type", componentTypeProperties);
    assertEquals("/apps/component-type",
        componentTypeRetrievalService.getComponentType("/apps/component-type").getPath());
  }

  @Test
  public void testGetComponentTypeWhenNotFound() throws LoginException {
    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);

    Exception exception = null;
    try {
      componentTypeRetrievalService.getComponentType("/apps/component-type");
    } catch (ComponentTypeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ComponentTypeRetrievalException.class, exception.getClass());
    assertEquals(
        "Unable to adapt '/apps/component-type': Unable to adapt '/apps/component-type': Resource"
        + " not found.", exception.getMessage());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameter()
      throws ComponentTypeRetrievalException, LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    context.create().resource("/apps/component-type", componentTypeProperties);
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    uiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals("/apps/component-type",
        componentTypeRetrievalService.getComponentType(uiFrameworkView).getPath());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameterAndInheritingFromLibs()
      throws ComponentTypeRetrievalException, LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    context.create().resource("/libs/component-type", componentTypeProperties);
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    uiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals("/libs/component-type",
        componentTypeRetrievalService.getComponentType(uiFrameworkView).getPath());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameterAndInheritingFromLibsAndNotFound()
      throws LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    uiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    Exception exception = null;

    try {
      componentTypeRetrievalService.getComponentType(uiFrameworkView);
    } catch (ComponentTypeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ComponentTypeRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt '/apps/component-type/view': No parent component type.",
        exception.getMessage());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameterAndInheritingFromLibsAndResourceIsInvalid()
      throws LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    context.create().resource("/libs/component-type");
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    uiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);
    Exception exception = null;
    try {
      componentTypeRetrievalService.getComponentType(uiFrameworkView);
    } catch (ComponentTypeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ComponentTypeRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt '/apps/component-type/view': Invalid ResourceType.",
        exception.getMessage());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameterAndInheritingFromLibsAndNotFoundAndViewIsLibs()
      throws LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);
    resource = context.create().resource("/libs/component-type/view", viewProperties);

    uiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    Exception exception = null;

    try {
      componentTypeRetrievalService.getComponentType(uiFrameworkView);
    } catch (ComponentTypeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ComponentTypeRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt '/libs/component-type/view': Unable to find ComponentType for ComponentUiFrameworkView.",
        exception.getMessage());
  }

  @Test
  public void testGetComponentTypeWhenUsingUiFrameworkParameterAndParameterIsNotResourceModel()
      throws LoginException {

    doReturn(resourceResolverFactory).when(
        componentTypeRetrievalService).getResourceResolverFactory();
    when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(
        context.resourceResolver());

    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);

    uiFrameworkView = mock(ComponentUiFrameworkView.class);

    Exception exception = null;

    try {
      componentTypeRetrievalService.getComponentType(uiFrameworkView);
    } catch (ComponentTypeRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(ComponentTypeRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt 'null': No parent component type.",
        exception.getMessage());
  }

  @Test
  public void testGetServiceUserName() {
    assertEquals("component-type-retrieval", componentTypeRetrievalService.getServiceUserName());
  }

//  @Test
  //  public void testGetComponentTypeWhenFallsBackToLibsAndAppsResourceIsInvalid()
  //      throws InvalidComponentTypeException, ComponentTypeRetrievalException {
  //    context.registerService(ComponentTypeRetrievalService.class, componentTypeRetrievalService);
  //    Map<String, Object> componentTypeProperties = new HashMap<>();
  //    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  //
  //    properties.put("sling:resourceType", "component");
  //
  //    context.create().resource("/apps/component");
  //    ComponentTypeResource componentType = context.create().resource("/libs/component",
  //        componentTypeProperties).adaptTo(ComponentTypeResource.class);
  //    resource = context.create().resource("/resource", properties);
  //
  //    when(componentTypeRetrievalService.getComponentType("/apps/component")).thenReturn(
  //        componentType);
  //
  //    baseComponent = resource.adaptTo(BaseComponent.class);
  //
  //    assertNotNull(baseComponent.getComponentType());
  //    assertEquals("/libs/component", baseComponent.getComponentType().getPath());
  //  }

//  @Test
//  public void testGetComponentType() throws InvalidComponentTypeException {
//    Map<String, Object> componentTypeProperties = new HashMap<>();
//    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
//
//    properties.put("sling:resourceType", "component");
//
//    context.create().resource("/apps/component", componentTypeProperties);
//    resource = context.create().resource("/resource", properties);
//
//    baseComponent = resource.adaptTo(BaseComponent.class);
//
//    assertNotNull(baseComponent.getComponentType());
//    assertEquals("/apps/component", baseComponent.getComponentType().getPath());
//  }
//
//  @Test
//  public void testGetComponentTypeWhenAbsoluteLibsPathAndLibsResourceDoesNotExist()
//      throws InvalidComponentTypeException {
//    Map<String, Object> componentTypeProperties = new HashMap<>();
//    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
//
//    properties.put("sling:resourceType", "/libs/component");
//
//    context.create().resource("/apps/component", componentTypeProperties);
//    resource = context.create().resource("/resource", properties);
//
//    baseComponent = resource.adaptTo(BaseComponent.class);
//
//    try {
//      baseComponent.getComponentType();
//    } catch (Exception e) {
//      exception = e;
//    }
//
//    assertEquals(InvalidComponentTypeException.class, exception.getClass());
//    assertEquals(
//        "Unable to adapt '/libs/component' to ComponentType for resource /resource. Invalid or "
//        + "missing ComponentType resource.", exception.getMessage());
//  }
//
//  @Test
//  public void testGetComponentTypeWhenFallsBackToLibs() throws InvalidComponentTypeException {
//    Map<String, Object> componentTypeProperties = new HashMap<>();
//    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
//
//    properties.put("sling:resourceType", "component");
//
//    context.create().resource("/libs/component", componentTypeProperties);
//    resource = context.create().resource("/resource", properties);
//
//    baseComponent = resource.adaptTo(BaseComponent.class);
//
//    assertNotNull(baseComponent.getComponentType());
//    assertEquals("/libs/component", baseComponent.getComponentType().getPath());
//  }
//
//
//  @Test
//  public void testGetComponentTypeWhenResourceResolverDoesNotFindApps()
//      throws InvalidComponentTypeException {
//    Map<String, Object> componentTypeProperties = new HashMap<>();
//    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
//
//    properties.put("sling:resourceType", "component");
//
//    Resource appsResource = context.create().resource("/apps/component", componentTypeProperties);
//    context.create().resource("/libs/component", componentTypeProperties);
//
//    Resource mockResource = mock(Resource.class);
//    ResourceResolver resourceResolver = mock(ResourceResolver.class);
//    baseComponent = spy(new BaseComponent());
//
//    doReturn(mockResource).when(baseComponent).getResource();
//    ValueMap valueMap = mock(ValueMap.class);
//    when(valueMap.get("sling:resourceType")).thenReturn("component");
//    doReturn(valueMap).when(baseComponent).getProperties();
//    doReturn("component").when(baseComponent).getSlingResourceType();
//    doReturn("/mock-component").when(baseComponent).getPath();
//    when(mockResource.getPath()).thenReturn("/mock-component");
//    when(mockResource.getResourceResolver()).thenReturn(resourceResolver);
//
//    when(resourceResolver.getResource("component")).thenReturn(null);
//    when(resourceResolver.getResource("/apps/component")).thenReturn(appsResource);
//
//    assertNotNull(baseComponent.getComponentType());
//    assertEquals("/apps/component", baseComponent.getComponentType().getPath());
//  }
//
//  @Test
//  public void testGetComponentTypeWhenComponentTypeIsInvalid() {
//    Map<String, Object> componentTypeProperties = new HashMap<>();
//
//    properties.put("sling:resourceType", "component");
//
//    context.create().resource("/libs/component", componentTypeProperties);
//    resource = context.create().resource("/resource", properties);
//
//    baseComponent = resource.adaptTo(BaseComponent.class);
//
//    try {
//      assertNotNull(baseComponent.getComponentType());
//    } catch (InvalidComponentTypeException e) {
//      exception = e;
//    }
//    assertEquals(
//        "Unable to adapt 'component' to ComponentType for resource /resource. Invalid or missing "
//        + "ComponentType resource.", exception.getMessage());
//  }

  @Test
  public void testGetRequiredResourcePaths() {
    assertEquals(2, componentTypeRetrievalService.getRequiredResourcePaths().size());
    assertEquals("/apps", componentTypeRetrievalService.getRequiredResourcePaths().get(0));
    assertEquals("/libs", componentTypeRetrievalService.getRequiredResourcePaths().get(1));
  }

  @Test
  public void testGetResourceResolverFactory() {
    context.registerService(resourceResolverFactory);
    context.registerInjectActivateService(componentTypeRetrievalService);

    assertNotNull(componentTypeRetrievalService.getResourceResolverFactory());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Component Type Retrieval Service",
        componentTypeRetrievalService.getDisplayName());
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    componentTypeRetrievalService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }
}