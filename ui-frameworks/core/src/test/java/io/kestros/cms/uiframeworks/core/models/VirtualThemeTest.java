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

package io.kestros.cms.uiframeworks.core.models;

import static org.junit.Assert.assertEquals;

import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VirtualThemeTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VirtualTheme virtualTheme;

  private UiFrameworkResource uiFramework;

  private ThemeResource theme;

  private Resource uiFrameworkResource;

  private Resource themeResource;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> cssFolderProperties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();

  private Map<String, Object> scriptFileProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    themeProperties.put("jcr:primaryType", "kes:Theme");

    context.create().resource("/ui-framework-1", uiFrameworkProperties);
    uiFrameworkResource = context.create().resource("/ui-framework-2", uiFrameworkProperties);
    fileProperties.put("jcr:primaryType", "nt:file");
    scriptFileProperties.put("jcr:mimeType", "text/css");
    uiFramework = uiFrameworkResource.adaptTo(UiFrameworkResource.class);
  }

  @Test
  public void testGetUiFramework() {
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);

    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("/ui-framework-2", virtualTheme.getUiFramework().getPath());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    themeProperties.put("fontAwesomeIcon", "icon");
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);

    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("icon", virtualTheme.getFontAwesomeIcon());
  }

  @Test
  public void testGetResource() {
    themeProperties.put("fontAwesomeIcon", "icon");
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);

    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals(theme.getResource(), virtualTheme.getResource());
  }

  @Test
  public void testGetTitle() {
    themeProperties.put("jcr:title", "Title");
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);

    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("Title", virtualTheme.getTitle());
  }

  @Test
  public void testGetDescription() {
    themeProperties.put("jcr:description", "Description");
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("Description", virtualTheme.getDescription());
  }

  @Test
  public void testGetName() {
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("theme", virtualTheme.getName());
  }

  @Test
  public void testGetPath() {
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("/ui-framework-2/themes/theme", virtualTheme.getPath());
  }

  @Test
  public void testGetCssPath() {
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("/ui-framework-2/themes/theme.css", virtualTheme.getCssPath());
  }

  @Test
  public void testGetJsPath() {
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals("/ui-framework-2/themes/theme.js", virtualTheme.getJsPath());
  }

  @Test
  public void testGetScriptFiles() {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css", "file-3.css"});
    themeResource = context.create().resource("/ui-framework-1/theme/theme", themeProperties);
    context.create().resource("/ui-framework-1/theme/theme/css", cssFolderProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-1.css", fileProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-1.css/jcr:content",
        scriptFileProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-2.css", fileProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-2.css/jcr:content",
        scriptFileProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-3.css", fileProperties);
    context.create().resource("/ui-framework-1/theme/theme/css/file-3.css/jcr:content",
        scriptFileProperties);
    theme = themeResource.adaptTo(ThemeResource.class);
    virtualTheme = new VirtualTheme(theme, uiFramework);

    assertEquals(3,
        virtualTheme.getScriptFiles(Collections.singletonList(ScriptTypes.CSS), "css").size());
  }

}