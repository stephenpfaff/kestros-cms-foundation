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

package io.kestros.cms.sitebuilding.core.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.services.UiLibraryCacheService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SiteLevelCssServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SiteLevelCssServlet siteLevelCssServlet;
  private UiLibraryCacheService uiLibraryCacheService;
  private ThemeOutputCompilationService themeOutputCompilationService;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiLibraryCacheService = mock(UiLibraryCacheService.class);
    themeOutputCompilationService = mock(ThemeOutputCompilationService.class);

    context.registerService(UiLibraryCacheService.class, uiLibraryCacheService);
    context.registerService(ThemeOutputCompilationService.class, themeOutputCompilationService);

    siteLevelCssServlet = new SiteLevelCssServlet();
    context.registerInjectActivateService(siteLevelCssServlet);

    pageProperties.put("jcr:primaryType", "kes:Page");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    uiFrameworkProperties.put("kes:uiFrameworkCode", "framework-code");
    themeProperties.put("jcr:primaryType", "kes:Theme");

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/ui-framework-1/themes/default");
    context.create().resource("/etc/ui-frameworks/ui-framework-1", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/ui-framework-1/themes/default", themeProperties);
    resource = context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageProperties);
  }
//
//  @Test
//  public void testDoGet()
//      throws CacheRetrievalException, InvalidResourceTypeException, NoMatchingCompilerException {
//    when(uiLibraryCacheService.getCachedOutput(anyString(), any(), anyBoolean())).thenThrow(
//        CacheRetrievalException.class);
//    when(uiLibraryCacheService.getCachedOutput(any(FrontendLibrary.class), any(),
//        anyBoolean())).thenThrow(CacheRetrievalException.class);
//    when(themeOutputCompilationService.getThemeSource(any(), any())).thenReturn(
//        "ui-framework-source");
//    //    context.request().setContextPath("/content/page.framework.theme.css");
//    context.requestPathInfo().setSelectorString("framework-code.default");
//    context.request().setResource(resource);
//    siteLevelCssServlet.doGet(context.request(), context.response());
//    assertEquals(200, context.response().getStatus());
//  }
//
//  @Test
//  public void testDoGetWhenThemeNotFound() {
//    //    context.request().setContextPath("/content/page.framework.theme.css");
//    context.requestPathInfo().setSelectorString("framework-code.invalid");
//    context.request().setResource(resource);
//    siteLevelCssServlet.doGet(context.request(), context.response());
//    assertEquals(400, context.response().getStatus());
//  }
//
//  @Test
//  public void testDoGetWhenUiFrameworkNotFound() {
//    //    context.request().setContextPath("/content/page.framework.theme.css");
//    context.requestPathInfo().setSelectorString("invalid.default");
//    context.request().setResource(resource);
//    siteLevelCssServlet.doGet(context.request(), context.response());
//    assertEquals(400, context.response().getStatus());
//  }
//
//  @Test
//  public void testDoGetWhenNotEnoughSelectors() {
//    siteLevelCssServlet.doGet(context.request(), context.response());
//    assertEquals(400, context.response().getStatus());
//  }
//
//  @Test
//  public void testDoGetWhenTooManySelectors() {
//    context.request().setContextPath("/content/page.framework.theme.invalid.css");
//    siteLevelCssServlet.doGet(context.request(), context.response());
//    assertEquals(400, context.response().getStatus());
//  }
}