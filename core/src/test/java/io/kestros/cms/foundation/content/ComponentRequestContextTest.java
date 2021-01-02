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

package io.kestros.cms.foundation.content;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.services.impl.BaseThemeProviderService;
import io.kestros.cms.foundation.services.ThemeProviderService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentRequestContextTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();

  private Resource resource;

  private ComponentRequestContext componentRequestContext;

  private Map<String, Object> pageProperties = new HashMap<>();
  private Map<String, Object> pageContentProperties = new HashMap<>();

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> componentProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkViewProperties = new HashMap<>();

  private Map<String, Object> variationProperties = new HashMap<>();
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    context.registerService(ThemeProviderService.class, themeProviderService);

    properties.put("sling:resourceType", "my-app");

    pageProperties.put("jcr:primaryType", "kes:Page");

    componentProperties.put("jcr:primaryType", "kes:ComponentType");
    uiFrameworkViewProperties.put("jcr:primaryType", "kes:ComponentUiFrameworkView");
    variationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    uiFrameworkProperties.put("kes:uiFrameworkCode", "my-framework");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    themeProperties.put("jcr:primaryType", "kes:Theme");

    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/html");

    context.create().resource("/apps/my-app", componentProperties);
    context.create().resource("/etc/ui-frameworks/my-framework", uiFrameworkProperties);
    context.create().resource("/etc/ui-frameworks/my-framework/themes/my-theme", themeProperties);

  }

  @Test
  public void testGetAppliedVariations() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);
    context.create().resource("/apps/my-app/my-framework/variations/variation-2",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(2, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-1", componentRequestContext.getAppliedVariations().get(0).getName());
    assertEquals("variation-2", componentRequestContext.getAppliedVariations().get(1).getName());

    assertEquals("variation-1 variation-2",
        componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenInvalidResourceType() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework/variations/variation-1");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationsFolderDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenComponentTypeNotFound() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    properties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    properties.put("sling:resourceType", "invalid-resource-type");
    resource = context.create().resource("/content", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenFrameworkScriptRootDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(0, componentRequestContext.getAppliedVariations().size());
    assertEquals("", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetAppliedVariationsWhenVariationDoesNotExist() {
    context.create().resource("/etc/ui-libraries/my-ui", uiFrameworkProperties);
    context.create().resource("/etc/ui-libraries/my-ui/themes/theme", themeProperties);

    properties.put("variations", new String[]{"variation-1", "variation-2"});
    pageContentProperties.put("kes:theme", "/etc/ui-libraries/my-ui/themes/theme");

    context.create().resource("/apps/my-app/my-framework", uiFrameworkViewProperties);

    context.create().resource("/apps/my-app/my-framework/variations/variation-1",
        variationProperties);

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page/jcr:content/component", properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals(1, componentRequestContext.getAppliedVariations().size());
    assertEquals("variation-1", componentRequestContext.getAppliedVariations().get(0).getName());
    assertEquals("variation-1", componentRequestContext.getWrapperVariations());
  }

  @Test
  public void testGetTheme() throws Exception {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    context.request().setResource(resource);
    componentRequestContext = context.request().adaptTo(ComponentRequestContext.class);

    assertEquals("my-theme", componentRequestContext.getTheme().getName());
  }
}