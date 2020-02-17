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

package io.kestros.cms.foundation.services.themeprovider;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseThemeProviderServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private BaseThemeProviderService themeProviderService;

  private BaseContentPage page;

  private BaseComponent component;

  private Resource resource;

  private Map<String, Object> pageProperties = new HashMap<>();

  private Map<String, Object> pageJcrContentProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    themeProviderService = new BaseThemeProviderService();

    pageProperties.put("jcr:primaryType", "kes:Page");
    themeProperties.put("jcr:primaryType", "kes:Theme");
    exception = null;
  }

  @Test
  public void testGetThemeForPage()
      throws ResourceNotFoundException, InvalidResourceTypeException, InvalidThemeException {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/default");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);

    page = resource.adaptTo(BaseContentPage.class);

    assertEquals("/etc/ui-frameworks/framework/themes/default",
        themeProviderService.getThemeForPage(page).getPath());
  }

  @Test
  public void testGetThemeForPageWhenInherited()
      throws ResourceNotFoundException, InvalidResourceTypeException, InvalidThemeException {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/default");
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    resource = context.create().resource("/page/child", pageProperties);

    page = resource.adaptTo(BaseContentPage.class);

    assertEquals("/etc/ui-frameworks/framework/themes/default",
        themeProviderService.getThemeForPage(page).getPath());
  }

  @Test
  public void testGetThemeForPageWhenInheritedAndNoThemeFound() {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    resource = context.create().resource("/page/child", pageProperties);

    page = resource.adaptTo(BaseContentPage.class);

    try {
      themeProviderService.getThemeForPage(page).getPath();
    } catch (ResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
      e.printStackTrace();
    }
    assertEquals("Unable to adapt '': Theme reference resource missing or invalid.",
        exception.getMessage());
  }


  @Test
  public void testGetThemeForPageWhenThemeIsInvalid() {
    context.create().resource("/etc/ui-frameworks/framework/themes/invalid-theme");

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/invalid-theme");
    resource = context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);

    page = resource.adaptTo(BaseContentPage.class);
    try {
      themeProviderService.getThemeForPage(page);
    } catch (ResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme '/etc/ui-frameworks/framework/themes/invalid-theme'. Could not "
        + "adapt to Theme. Resource must have jcr:primaryType 'kes:Theme'.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeForComponent() throws ResourceNotFoundException, InvalidThemeException {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/default");
    context.create().resource("/page", pageProperties);
    context.create().resource("/page/jcr:content", pageJcrContentProperties);
    resource = context.create().resource("/page/jcr:content/component");

    component = resource.adaptTo(BaseComponent.class);

    assertEquals("/etc/ui-frameworks/framework/themes/default",
        themeProviderService.getThemeForComponent(component).getPath());

  }

  @Test
  public void testGetThemeForComponentWhenNoContainingPage() {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    resource = context.create().resource("/component/component/component");

    component = resource.adaptTo(BaseComponent.class);

    try {
      assertEquals("/etc/ui-frameworks/framework/themes/default",
          themeProviderService.getThemeForComponent(component).getPath());
    } catch (InvalidThemeException e) {
      exception = e;
    } catch (ResourceNotFoundException e) {
    }
    assertEquals("Unable to retrieve theme ''. No ancestor resource with configured Theme found.",
        exception.getMessage());
  }

  @Test
  public void testGetThemeForComponentWhenNoContainingPageAndParentComponentHasTheme()
      throws ResourceNotFoundException, InvalidThemeException {
    context.create().resource("/etc/ui-frameworks/framework/themes/default", themeProperties);

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/default");
    context.create().resource("/component/component", pageJcrContentProperties);
    resource = context.create().resource("/component/component/component");

    component = resource.adaptTo(BaseComponent.class);

    assertEquals("/etc/ui-frameworks/framework/themes/default",
        themeProviderService.getThemeForComponent(component).getPath());
  }

  @Test
  public void testGetThemeForComponentWhenNoContainingPageAndParentComponentHasThemeAndThemeIsInvalid() {
    context.create().resource("/etc/ui-frameworks/framework/themes/invalid-theme");

    pageJcrContentProperties.put("kes:theme", "/etc/ui-frameworks/framework/themes/invalid-theme");
    context.create().resource("/component/component", pageJcrContentProperties);
    resource = context.create().resource("/component/component/component");

    component = resource.adaptTo(BaseComponent.class);

    try {
      assertEquals("/etc/ui-frameworks/framework/themes/default",
          themeProviderService.getThemeForComponent(component).getPath());
    } catch (InvalidThemeException e) {
      exception = e;
    } catch (ResourceNotFoundException e) {
    }
    assertEquals(
        "Unable to retrieve theme '/etc/ui-frameworks/framework/themes/invalid-theme'. Unable to "
        + "adapt '/etc/ui-frameworks/framework/themes/invalid-theme' to Theme: Invalid resource "
        + "type.", exception.getMessage());
  }
}