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

package io.kestros.cms.uiframeworks.refactored.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeResource theme;

  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiFrameworkRetrievalService = mock(UiFrameworkRetrievalService.class);
  }

  @Test
  public void testGetUiFramework() throws UiFrameworkRetrievalException {
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);

    resource = context.create().resource("/theme", properties);
    theme = resource.adaptTo(ThemeResource.class);

    UiFramework uiFramework = mock(UiFramework.class);
    when(uiFrameworkRetrievalService.getUiFramework(theme)).thenReturn(uiFramework);

    assertEquals(uiFramework, theme.getUiFramework());
  }

  @Test
  public void testGetUiFrameworkWhenUiFrameworkRetrievalException()
      throws UiFrameworkRetrievalException {
    context.registerService(UiFrameworkRetrievalService.class, uiFrameworkRetrievalService);

    resource = context.create().resource("/theme", properties);
    theme = resource.adaptTo(ThemeResource.class);

    when(uiFrameworkRetrievalService.getUiFramework(theme)).thenThrow(
        new UiFrameworkRetrievalException(""));

    assertNull(theme.getUiFramework());
  }

  @Test
  public void testGetUiFrameworkWhenFrameworkIsNull() {
    resource = context.create().resource("/theme", properties);
    theme = resource.adaptTo(ThemeResource.class);

    assertNull(theme.getUiFramework());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");

    resource = context.create().resource("/theme", properties);
    theme = resource.adaptTo(ThemeResource.class);

    assertEquals("icon", theme.getFontAwesomeIcon());
  }
}