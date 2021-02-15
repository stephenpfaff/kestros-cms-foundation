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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import io.kestros.cms.componenttypes.api.exceptions.ComponentVariationRetrievalException;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class ComponentVariationRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ComponentVariationRetrievalServiceImpl componentVariationRetrievalService;

  private ComponentUiFrameworkViewResource componentUiFrameworkView;

  private Resource resource;
  private Map<String, Object> componentViewProperties = new HashMap<>();
  private Map<String, Object> componentVariationProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentVariationRetrievalService = new ComponentVariationRetrievalServiceImpl();

    componentVariationProperties.put("jcr:primaryType", "kes:ComponentVariation");
  }

  @Test
  public void testGetComponentVariations() throws ComponentVariationRetrievalException {
    resource = context.create().resource("/component-view", componentViewProperties);
    context.create().resource("/component-view/variations/variation-1",
        componentVariationProperties);
    context.create().resource("/component-view/variations/variation-2",
        componentVariationProperties);
    context.create().resource("/component-view/variations/variation-3",
        componentVariationProperties);

    componentUiFrameworkView = resource.adaptTo(ComponentUiFrameworkViewResource.class);

    assertEquals(3,
        componentVariationRetrievalService.getComponentVariations(componentUiFrameworkView).size());
    assertEquals("/component-view/variations/variation-1",
        componentVariationRetrievalService.getComponentVariations(componentUiFrameworkView).get(
            0).getPath());
    assertEquals("/component-view/variations/variation-2",
        componentVariationRetrievalService.getComponentVariations(componentUiFrameworkView).get(
            1).getPath());
    assertEquals("/component-view/variations/variation-3",
        componentVariationRetrievalService.getComponentVariations(componentUiFrameworkView).get(
            2).getPath());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Component Variation Retrieval Service",
        componentVariationRetrievalService.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    componentVariationRetrievalService.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    componentVariationRetrievalService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = mock(FormattingResultLog.class);
    componentVariationRetrievalService.runAdditionalHealthChecks(log);
    verifyZeroInteractions(log);
  }
}
