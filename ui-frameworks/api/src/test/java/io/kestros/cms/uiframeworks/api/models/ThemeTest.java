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

package io.kestros.cms.uiframeworks.api.models;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidUiFrameworkException;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeOutputCompilationService themeOutputCompilationService;
  private Theme theme;

  private Resource resource;

  private Map<String, String> properties = new HashMap<>();
  private Map<String, String> frameworkProperties = new HashMap<>();

  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");
    themeOutputCompilationService = mock(ThemeOutputCompilationService.class);
    context.registerService(ThemeOutputCompilationService.class,
        themeOutputCompilationService);

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    fileJcrContentProperties.put("jcr:mimeType", "text/css");
  }

  @Test
  public void testGetUiFramework() throws InvalidThemeException, InvalidUiFrameworkException {
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    context.create().resource("/etc/ui-frameworks/my-ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/my-ui/themes/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("my-ui", theme.getUiFramework().getName());
    assertEquals("my-ui", theme.getUiFramework().getName());
  }

  @Test
  public void testGetUiFrameworkWhenFrameworkIsInvalid() {
    resource = context.create().resource("/etc/themes/theme", properties);

    theme = resource.adaptTo(Theme.class);

    try {
      theme.getUiFramework();
    } catch (InvalidUiFrameworkException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve parent UiFramework for theme '/etc/themes/theme'. Unable to adapt "
        + "'/etc' to UiFramework: Invalid resource type.", exception.getMessage());
  }


  @Test
  public void testGetUiFrameworkWhenInvalidResourceType() {
    context.create().resource("/etc/ui-frameworks/my-ui");
    resource = context.create().resource("/etc/ui-frameworks/my-ui/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    try {
      theme.getUiFramework();
    } catch (InvalidUiFrameworkException e) {
      exception = e;
    }
    assertEquals("Unable to retrieve parent UiFramework for theme "
                 + "'/etc/ui-frameworks/my-ui/theme-with-ui-framework'. Unable to adapt "
                 + "'/etc/ui-frameworks' to UiFramework: Invalid resource type.",
        exception.getMessage());
  }

  @Test
  public void testGetOutput() throws InvalidResourceTypeException {
    when(themeOutputCompilationService.getThemeOutput(any(Theme.class), any(ScriptType.class),
        any(Boolean.class))).thenReturn("123");

    resource = context.create().resource("/etc/ui-frameworks/my-ui/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);
    assertEquals("123", theme.getOutput(CSS, true));
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/etc/ui-frameworks/my-ui/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("icon", theme.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenDefault() {
    resource = context.create().resource("/etc/ui-frameworks/my-ui/theme-with-ui-framework",
        properties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("fas fa-paint-brush", theme.getFontAwesomeIcon());
  }

}