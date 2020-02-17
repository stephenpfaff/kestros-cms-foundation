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

package io.kestros.cms.foundation.design.theme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import io.kestros.commons.uilibraries.config.UiLibraryConfigurationService;
import io.kestros.commons.uilibraries.services.cache.UiLibraryCacheService;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeCssServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeCssServlet servlet;

  private UiLibraryCacheService uiLibraryCacheService;
  private UiLibraryConfigurationService uiLibraryConfigurationService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    uiLibraryConfigurationService = mock(UiLibraryConfigurationService.class);
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    context.registerService(UiLibraryConfigurationService.class, uiLibraryConfigurationService);
    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);

    servlet = new ThemeCssServlet();
    context.registerInjectActivateService(servlet);
  }

  @Test
  public void testGetUiLibraryCacheService() {
    assertEquals(uiLibraryCacheService, servlet.getUiLibraryCacheService());
  }

  @Test
  public void testGetUiLibraryConfigurationService() {
    assertEquals(uiLibraryConfigurationService, servlet.getUiLibraryConfigurationService());
  }

  @Test
  public void testGetUiLibraryClass() {
    assertEquals(Theme.class, servlet.getUiLibraryClass());
  }
}