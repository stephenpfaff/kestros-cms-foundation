package io.kestros.cms.foundation.servlets;

import static org.junit.Assert.assertEquals;

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

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    siteLevelCssServlet = new SiteLevelCssServlet();
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
    //    context.request().setContextPath("/content/page.framework.theme.css");
    context.requestPathInfo().setSelectorString("framework-code.default");
    context.request().setResource(resource);
    siteLevelCssServlet.doGet(context.request(), context.response());
    assertEquals(200, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenThemeNotFound() {
    //    context.request().setContextPath("/content/page.framework.theme.css");
    context.requestPathInfo().setSelectorString("framework-code.invalid");
    context.request().setResource(resource);
    siteLevelCssServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenUiFrameworkNotFound() {
    //    context.request().setContextPath("/content/page.framework.theme.css");
    context.requestPathInfo().setSelectorString("invalid.default");
    context.request().setResource(resource);
    siteLevelCssServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenNotEnoughSelectors() {
    siteLevelCssServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }

  @Test
  public void testDoGetWhenTooManySelectors() {
    context.request().setContextPath("/content/page.framework.theme.invalid.css");
    siteLevelCssServlet.doGet(context.request(), context.response());
    assertEquals(400, context.response().getStatus());
  }
}