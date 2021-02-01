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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import io.kestros.commons.uilibraries.basecompilers.services.CssCompilerService;
import io.kestros.commons.uilibraries.core.services.impl.UiLibraryCompilationServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class ThemeOutputCompilationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ThemeOutputCompilationServiceImpl service;

  private UiFrameworkOutputCompilationService uiFrameworkOutputCompilationService;

  private UiLibraryCompilationService uiLibraryCompilationService;

  private CssCompilerService cssCompilerService;

  private ThemeResource theme;

  private Resource resource;
  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();
  private Map<String, Object> cssFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> cssFileProperties = new HashMap<>();

  InputStream file1InputStream;
  InputStream file2InputStream;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    service = spy(new ThemeOutputCompilationServiceImpl());
    uiLibraryCompilationService = new UiLibraryCompilationServiceImpl();
    cssCompilerService = spy(new CssCompilerService());
    uiFrameworkOutputCompilationService = mock(UiFrameworkOutputCompilationService.class);

    themeProperties.put("jcr:primaryType", "kes:Theme");
    fileProperties.put("jcr:primaryType", "nt:file");
    cssFileProperties.put("jcr:mimeType", "text/css");

    file1InputStream = new ByteArrayInputStream("file-1".getBytes());
    file2InputStream = new ByteArrayInputStream("file-2".getBytes());
  }

  @Test
  public void testGetThemeScriptTypes() throws NoMatchingCompilerException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});
    context.create().resource("/ui-framework", uiFrameworkProperties);
    resource = context.create().resource("/ui-framework/themes/theme", themeProperties);
    context.create().resource("/ui-framework/themes/theme/css", cssFolderProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css/jcr:content",
        cssFileProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css/jcr:content",
        cssFileProperties);

    theme = resource.adaptTo(ThemeResource.class);

    when(uiFrameworkOutputCompilationService.getUiFrameworkSource(any(), any())).thenReturn(
        "ui-framework");

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);
    context.registerInjectActivateService(service);

    assertEquals(1, service.getThemeScriptTypes(theme, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetThemeOutput()
      throws InvalidResourceTypeException, NoMatchingCompilerException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});
    context.create().resource("/ui-framework", uiFrameworkProperties);
    resource = context.create().resource("/ui-framework/themes/theme", themeProperties);
    context.create().resource("/ui-framework/themes/theme/css", cssFolderProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css/jcr:content",
        cssFileProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css/jcr:content",
        cssFileProperties);

    theme = resource.adaptTo(ThemeResource.class);

    when(uiFrameworkOutputCompilationService.getUiFrameworkSource(any(), any())).thenReturn(
        "ui-framework");

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);
    context.registerInjectActivateService(service);

    assertEquals("ui-framework\nfile-1\nfile-2",
        service.getUiLibraryOutput(theme, ScriptTypes.CSS));
    verify(cssCompilerService, times(1)).getOutput("ui-framework\nfile-1\nfile-2");
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Theme Output Compilation Service", service.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    service.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    service.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    service.runAdditionalHealthChecks(log);
    verifyZeroInteractions(log);
  }

  @Test
  public void testGetThemeSource()
      throws InvalidResourceTypeException, NoMatchingCompilerException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});
    resource = context.create().resource("/theme", themeProperties);
    context.create().resource("/theme/css", cssFolderProperties);
    context.create().resource("/theme/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/theme/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/theme/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/theme/css/file-2.css/jcr:content", cssFileProperties);

    theme = resource.adaptTo(ThemeResource.class);

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);
    context.registerInjectActivateService(service);

    assertEquals("file-1\nfile-2", service.getThemeSource(theme, ScriptTypes.CSS));
  }

  @Test
  public void testGetThemeSourceWhenUiFrameworkHasSource()
      throws InvalidResourceTypeException, NoMatchingCompilerException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});
    context.create().resource("/ui-framework", uiFrameworkProperties);
    resource = context.create().resource("/ui-framework/themes/theme", themeProperties);
    context.create().resource("/ui-framework/themes/theme/css", cssFolderProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-1.css/jcr:content",
        cssFileProperties);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/themes/theme/css/file-2.css/jcr:content",
        cssFileProperties);

    theme = resource.adaptTo(ThemeResource.class);

    when(uiFrameworkOutputCompilationService.getUiFrameworkSource(any(), any())).thenReturn(
        "ui-framework");

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);
    context.registerInjectActivateService(service);

    assertEquals("ui-framework\nfile-1\nfile-2", service.getThemeSource(theme, ScriptTypes.CSS));
  }
}
