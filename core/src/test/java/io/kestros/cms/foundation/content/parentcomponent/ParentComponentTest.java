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

package io.kestros.cms.foundation.content.parentcomponent;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.themeprovider.BaseThemeProviderService;
import io.kestros.cms.foundation.services.themeprovider.ThemeProviderService;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParentComponentTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeProviderService themeProviderService = new BaseThemeProviderService();


  private Resource resource;

  private ParentComponent parentComponent;

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
  public void testGetId() {
    properties.put("id", "my-id");
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-id", parentComponent.getId());
  }

  @Test
  public void testGetIdWhenMissing() {
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("", parentComponent.getId());
  }

  @Test
  public void testCssGetClass() {
    properties.put("class", "my-class");
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-class", parentComponent.getCssClass());
  }

  @Test
  public void testGetCssClassWhenMissing() {
    resource = context.create().resource("/component", properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("", parentComponent.getCssClass());
  }


  @Test
  public void testGetTheme() throws Exception {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");

    context.create().resource("/content/page-with-framework", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-theme", parentComponent.getTheme().getName());
  }

  @Test
  public void testGetThemeWhenInvalid() {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme");

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");

    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);
    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      parentComponent.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme '/etc/ui-frameworks/my-ui/themes/my-theme'. Unable to adapt "
        + "'/etc/ui-frameworks/my-ui/themes/my-theme' to Theme: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeWhenInheritedFromPage() throws Exception {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    context.create().resource("/content/page-with-framework", pageProperties);

    pageContentProperties.put("kes:theme", "/etc/ui-frameworks/my-ui/themes/my-theme");
    context.create().resource("/content/page-with-framework/jcr:content", pageContentProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    assertEquals("my-theme", parentComponent.getTheme().getName());
  }

  @Test
  public void testGetThemeWhenNoContainingPage() {
    context.create().resource("/etc/ui-frameworks/my-ui/themes/my-theme", themeProperties);

    resource = context.create().resource("/content/page-with-framework/jcr:content/component",
        properties);

    parentComponent = resource.adaptTo(ParentComponent.class);

    try {
      parentComponent.getTheme();
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve theme ''. No ancestor resource with configured Theme found.",
        exception.getMessage());
  }


}