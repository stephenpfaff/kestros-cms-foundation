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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.services.ComponentTypeRetrievalService;
import io.kestros.cms.componenttypes.core.services.ComponentTypeRetrievalServiceImpl;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentUiFrameworkViewResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  private ComponentTypeRetrievalService componentTypeRetrievalService;

  private ComponentUiFrameworkViewResource componentUiFrameworkView;

  private UiFramework uiFramework;

  private Resource resource;

  private Map<String, Object> componentTypeProperties = new HashMap<>();
  private Map<String, Object> viewProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    componentTypeRetrievalService = new ComponentTypeRetrievalServiceImpl();
    uiFrameworkRetrievalService = mock(UiFrameworkRetrievalService.class);
    uiFramework = mock(UiFramework.class);

    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testGetTitle() throws UiFrameworkRetrievalException {
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);

    when(uiFrameworkRetrievalService.getUiFrameworkByCode(any(), any(), any(), any())).thenReturn(
        uiFramework);
    when(uiFramework.getTitle()).thenReturn("Framework Title");

    viewProperties.put("jcr:title", "Title");
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals("Title", componentUiFrameworkView.getTitle());
  }

  @Test
  public void testGetComponentType() throws ComponentTypeRetrievalException {
    context.create().resource("/apps/component-type", componentTypeProperties);
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    context.registerInjectActivateService(componentTypeRetrievalService);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals("/apps/component-type", componentUiFrameworkView.getComponentType().getPath());
  }

  @Test
  public void testGetUiFramework() throws ResourceNotFoundException, UiFrameworkRetrievalException {
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);

    when(uiFrameworkRetrievalService.getUiFrameworkByCode(any(), any(), any(), any())).thenReturn(
        uiFramework);
    when(uiFramework.getPath()).thenReturn("/ui-framework");

    resource = context.create().resource("/apps/component-type/view", viewProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals("/ui-framework", componentUiFrameworkView.getUiFramework().getPath());
  }

  @Test
  public void testIsInheritVariations() {
    viewProperties.put("inheritVariations", true);
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertTrue(componentUiFrameworkView.isInheritVariations());
  }

  @Test
  public void testGetFontAwesomeIcon() throws UiFrameworkRetrievalException {
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);

    when(uiFrameworkRetrievalService.getUiFrameworkByCode(any(), any(), any(), any())).thenReturn(
        uiFramework);
    when(uiFramework.getFontAwesomeIcon()).thenReturn("framework-icon");

    resource = context.create().resource("/apps/component-type/view", viewProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);
    assertEquals("framework-icon", componentUiFrameworkView.getFontAwesomeIcon());
  }

  @Test
  public void testGetManagingResourceType() {
    resource = context.create().resource("/apps/component-type/view", viewProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);
    assertEquals(ManagedComponentUiFrameworkViewResource.class,
        componentUiFrameworkView.getManagingResourceType());
  }
}