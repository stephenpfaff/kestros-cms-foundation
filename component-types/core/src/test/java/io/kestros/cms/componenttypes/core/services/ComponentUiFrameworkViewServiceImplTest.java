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

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentUiFrameworkViewServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFramework uiFramework;
  private Resource resource;
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> componentTypeProperties = new HashMap<>();
  private Map<String, Object> componentViewProperties = new HashMap<>();

  private ComponentUiFrameworkViewServiceImpl componentUiFrameworkViewService;


  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    componentUiFrameworkViewService = new ComponentUiFrameworkViewServiceImpl();

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
  }

  @Test
  public void testGetComponentViews() {
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework");
    resource = context.create().resource("/etc/ui-frameworks/framework", uiFrameworkProperties);
    context.create().resource("/apps/component", componentTypeProperties);
    context.create().resource("/apps/component/framework", componentTypeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(1, componentUiFrameworkViewService.getComponentViews(uiFramework).size());
  }

  @Test
  public void testGetAllComponentUiFrameworkViewsInADirectory() {
  }
}