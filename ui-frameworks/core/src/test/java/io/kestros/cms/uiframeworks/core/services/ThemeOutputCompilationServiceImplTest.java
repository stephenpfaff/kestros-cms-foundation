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

package io.kestros.cms.uiframeworks.core.services;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.JAVASCRIPT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ThemeOutputCompilationServiceImplTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private ThemeOutputCompilationServiceImpl themeOutputCompilationService;
  private UiFrameworkOutputCompilationService uiFrameworkOutputCompilationService;
  private UiLibraryCompilationService uiLibraryCompilationService;
  private UiLibraryMinificationService uiLibraryMinificationService;

  private Theme theme;
  private Resource resource;

  private Map<String, String> properties = new HashMap<>();
  private Map<String, String> frameworkProperties = new HashMap<>();

  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    fileJcrContentProperties.put("jcr:mimeType", "text/css");
    themeOutputCompilationService = new ThemeOutputCompilationServiceImpl();
    uiLibraryCompilationService = mock(UiLibraryCompilationService.class);
    uiLibraryMinificationService = mock(UiLibraryMinificationService.class);
    uiFrameworkOutputCompilationService = mock(UiFrameworkOutputCompilationService.class);
    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Theme Output Compilation Service",
        themeOutputCompilationService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    themeOutputCompilationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, never()).warn(any());
    verify(log, never()).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testGetOutput() throws InvalidResourceTypeException, ScriptCompressionException {
    context.registerInjectActivateService(themeOutputCompilationService);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{ color: red;}");

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n",
        themeOutputCompilationService.getThemeOutput(theme, CSS, false));
    verify(uiLibraryMinificationService, never()).getMinifiedOutput(any(), any());
  }

  @Test
  public void testGetOutputWhenCssMinified()
      throws InvalidResourceTypeException, ScriptCompressionException {
    context.registerService(UiLibraryMinificationService.class, uiLibraryMinificationService);
    context.registerInjectActivateService(themeOutputCompilationService);

    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{ color: red;}");

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output{color:#f00}",
        themeOutputCompilationService.getThemeOutput(theme, CSS, true));
    verify(uiLibraryMinificationService, times(0)).getMinifiedOutput(any(), any());
  }


  @Test
  public void testGetOutputWhenJsMinified()
      throws InvalidResourceTypeException, ScriptCompressionException {
    context.registerService(UiLibraryMinificationService.class, uiLibraryMinificationService);
    context.registerInjectActivateService(themeOutputCompilationService);

    when(uiLibraryMinificationService.getMinifiedOutput(any(), any())).thenReturn("minified-js");
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "console.log('test');");

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("minified-js",
        themeOutputCompilationService.getThemeOutput(theme, JAVASCRIPT, true));
    verify(uiLibraryMinificationService, times(1)).getMinifiedOutput(any(), any());
  }


  @Test
  public void testGetOutputWhenScriptCompressionException()
      throws InvalidResourceTypeException, ScriptCompressionException {
    context.registerService(UiLibraryMinificationService.class, uiLibraryMinificationService);
    context.registerInjectActivateService(themeOutputCompilationService);

    when(uiLibraryMinificationService.getMinifiedOutput(any(), any())).thenThrow(
        ScriptCompressionException.class);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "console.log('test');");

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("console.log('test');", themeOutputCompilationService.getThemeOutput(theme, JAVASCRIPT, true));
    verify(uiLibraryMinificationService, times(1)).getMinifiedOutput(any(), any());
  }


  @Test
  public void testGetOutputWhenHasUiFramework() throws InvalidResourceTypeException {
    context.registerInjectActivateService(themeOutputCompilationService);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn(
        ".test-output{ color: blue;}");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{ color: red;}");

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: blue;\n" + "  color: red;\n" + "}\n",
        themeOutputCompilationService.getThemeOutput(theme, CSS, false));
  }

  @Test
  public void testGetOutputWhenHasUiFrameworkAndUsingLess() throws InvalidResourceTypeException {
    context.registerInjectActivateService(themeOutputCompilationService);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn(
        ".test-output{ color: @theme-color;}");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "@theme-color: red;");

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n",
        themeOutputCompilationService.getThemeOutput(theme, CSS, false));
  }

  @Test
  public void testGetOutputWhenHasUiFrameworkAndUsingLessAndThemeOverridesValue()
      throws InvalidResourceTypeException {
    context.registerInjectActivateService(themeOutputCompilationService);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn(
        "@theme-color:blue; .test-output{ color: @theme-color;}");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "@theme-color: red;");

    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/css/file.css", fileProperties);

    context.create().resource("/etc/ui-frameworks/ui/css/file.css/jcr:content",
        fileJcrContentProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css", fileProperties);

    context.create().resource("/etc/ui-frameworks/ui/themes/theme/css/file.css/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals(".test-output {\n" + "  color: red;\n" + "}\n",
        themeOutputCompilationService.getThemeOutput(theme, CSS, false));
  }

  @Test
  public void testGetOutputWhenJavaScript() throws InvalidResourceTypeException {
    context.registerInjectActivateService(themeOutputCompilationService);
    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("");
    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        "console.log('test');");
    fileJcrContentProperties.put("jcr:mimeType", "application/javascript");

    context.create().resource("/etc/ui-frameworks/ui", frameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/ui/themes/theme", properties);

    scriptTypeFolderProperties.put("include", "file.js");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js/file.js", fileProperties);

    context.create().resource("/etc/ui-frameworks/ui/themes/theme/js/file.js/jcr:content",
        fileJcrContentProperties);

    theme = resource.adaptTo(Theme.class);

    assertEquals("console.log('test');",
        themeOutputCompilationService.getThemeOutput(theme, JAVASCRIPT, false));
  }

}