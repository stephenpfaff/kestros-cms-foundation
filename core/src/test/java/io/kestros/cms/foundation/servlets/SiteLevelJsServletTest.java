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

package io.kestros.cms.foundation.servlets;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SiteLevelJsServletTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SiteLevelJsServlet siteLevelJsServlet;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    siteLevelJsServlet = new SiteLevelJsServlet();
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

  @Test
  public void testDoGet() {
    context.requestPathInfo().setSelectorString("framework-code.default");
    context.request().setResource(resource);
    siteLevelJsServlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenThemeNotFound() {
    context.requestPathInfo().setSelectorString("framework-code.invalid");
    context.request().setResource(resource);
    siteLevelJsServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenUiFrameworkNotFound() {
    context.requestPathInfo().setSelectorString("invalid.default");
    context.request().setResource(resource);
    siteLevelJsServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenNotEnoughSelectors() {
    siteLevelJsServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenTooManySelectors() {
    context.request().setContextPath("/content/page.framework.theme.invalid.js");
    siteLevelJsServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }
}