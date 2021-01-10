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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateCacheService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiFrameworkTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFramework uiFramework;
  private UiFrameworkOutputCompilationService uiFrameworkOutputCompilationService;
  private HtlTemplateCacheService htlTemplateCacheService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> vendorLibraryProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();

  private Map<String, Object> componentTypeProperties = new HashMap<>();

  private Map<String, Object> componentTypeUiFrameworkViewProperties = new HashMap<>();
  private Map<String, Object> componentVariationProperties = new HashMap<>();


  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  private Exception exception;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    htlTemplateCacheService = mock(HtlTemplateCacheService.class);
    uiFrameworkOutputCompilationService = mock(UiFrameworkOutputCompilationService.class);
    context.registerService(HtlTemplateCacheService.class, htlTemplateCacheService);

    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
    themeProperties.put("jcr:primaryType", "kes:Theme");
    componentTypeProperties.put("jcr:primaryType", "kes:ComponentType");
    componentVariationProperties.put("jcr:primaryType", "kes:ComponentVariation");
    componentTypeUiFrameworkViewProperties.put("jcr:primaryType", "kes:ComponentUiFrameworkView");

    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/css");
  }

  @Test
  public void testGetFrameworkCode() throws Exception {
    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);
    assertEquals("test-code", uiFramework.getFrameworkCode());
  }

  @Test
  public void testGetVendorLibraries() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-2", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-3", vendorLibraryProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2", "library-3"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(3, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenMissingVendorLibrariesRoot() throws Exception {
    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2", "library-3"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenVendorLibraryIsInvalid() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    context.create().resource("/etc/vendor-libraries/library-1");

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenVendorLibraryIsMissing() throws Exception {
    context.create().resource("/etc/vendor-libraries");

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetThemes() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);
    context.create().resource("/ui-framework/themes/theme-2", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getThemes().size());
    assertEquals("theme-1", uiFramework.getThemes().get(0).getName());
    assertEquals("theme-2", uiFramework.getThemes().get(1).getName());
  }

  @Test
  public void testGetThemesThemesRootResourceNotFound() {
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(0, uiFramework.getThemes().size());
  }

  @Test
  public void testGetTheme() throws ChildResourceNotFoundException, InvalidResourceTypeException {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default", themeProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("/ui-framework/themes/theme-1", uiFramework.getTheme("theme-1").getPath());
  }

  @Test
  public void testGetDefaultTheme() throws InvalidResourceTypeException, ResourceNotFoundException,
                                           ChildResourceNotFoundException, InvalidThemeException {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default", themeProperties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("default", uiFramework.getDefaultTheme().getName());
  }

  @Test
  public void testGetDefaultThemeWhenInvalidResourceType() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/default");
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    try {
      uiFramework.getDefaultTheme();
    } catch (ChildResourceNotFoundException e) {
    } catch (InvalidThemeException e) {
      exception = e;
    }
    assertEquals(
        "Unable to retrieve theme 'default' under UiFramework '/ui-framework'. Could not adapt to "
        + "Theme. Resource must have jcr:primaryType 'kes:Theme'.", exception.getMessage());
  }

  @Test
  public void testGetDefaultThemeWhenNotFound() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    try {
      uiFramework.getDefaultTheme();
    } catch (ChildResourceNotFoundException e) {
      exception = e;
    } catch (InvalidThemeException e) {
      e.printStackTrace();
    }
    assertEquals("Unable to adapt 'default' under '/ui-framework/themes': Child not found.",
        exception.getMessage());
  }

  @Test
  public void testGetOutput() throws InvalidResourceTypeException {
    context.registerService(UiFrameworkOutputCompilationService.class,
        uiFrameworkOutputCompilationService);

    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(uiFrameworkOutputCompilationService.getUiFrameworkOutput(any(), any())).thenReturn("css");

    assertEquals("css", uiFramework.getOutput(ScriptType.CSS, false));
  }

  @Test
  public void testGetOutputWhenCompilationServiceIsNull() throws InvalidResourceTypeException {
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("", uiFramework.getOutput(ScriptType.CSS, false));
  }

  @Test
  public void testGetIncludedCdnCssScripts() {
    String[] scripts = new String[]{"script1.css", "script2.css"};
    properties.put("includedCdnCssScripts", scripts);
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getIncludedCdnCssScripts().size());
    assertEquals("script1.css", uiFramework.getIncludedCdnCssScripts().get(0));
    assertEquals("script2.css", uiFramework.getIncludedCdnCssScripts().get(1));
  }

  @Test
  public void testGetIncludedCdnCssScriptsWhenInheritingFromVendorLibraries() {
    vendorLibraryProperties.put("includedCdnCssScripts", new String[]{"script-3.css", "script-4.css"});
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    String[] scripts = new String[]{"script1.css", "script2.css"};
    properties.put("includedCdnCssScripts", scripts);
    properties.put("kes:vendorLibraries", new String[] {"library-1"});
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(4, uiFramework.getIncludedCdnCssScripts().size());
    assertEquals("script-3.css", uiFramework.getIncludedCdnCssScripts().get(0));
    assertEquals("script-4.css", uiFramework.getIncludedCdnCssScripts().get(1));
    assertEquals("script1.css", uiFramework.getIncludedCdnCssScripts().get(2));
    assertEquals("script2.css", uiFramework.getIncludedCdnCssScripts().get(3));
  }

  @Test
  public void testGetIncludedCdnJsScripts() {
    String[] scripts = new String[]{"script1.js", "script2.js"};
    properties.put("includedCdnJsScripts", scripts);
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/themes/theme-1", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getIncludedCdnJsScripts().size());
    assertEquals("script1.js", uiFramework.getIncludedCdnJsScripts().get(0));
    assertEquals("script2.js", uiFramework.getIncludedCdnJsScripts().get(1));
  }

  @Test
  public void testGetIncludedCdnJsScriptsWhenInheritingFromVendorLibraries() {
    vendorLibraryProperties.put("includedCdnJsScripts", new String[]{"script-3.js", "script-4.js"});
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    String[] scripts = new String[]{"script1.js", "script2.js"};
    properties.put("includedCdnJsScripts", scripts);
    properties.put("kes:vendorLibraries", new String[] {"library-1"});
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(4, uiFramework.getIncludedCdnJsScripts().size());
    assertEquals("script-3.js", uiFramework.getIncludedCdnJsScripts().get(0));
    assertEquals("script-4.js", uiFramework.getIncludedCdnJsScripts().get(1));
    assertEquals("script1.js", uiFramework.getIncludedCdnJsScripts().get(2));
    assertEquals("script2.js", uiFramework.getIncludedCdnJsScripts().get(3));
  }

  @Test
  public void testGetExternalizedFiles() {
    context.create().resource("/files/file-1", fileProperties);
    context.create().resource("/files/file-2", fileProperties);
    context.create().resource("/files/file-3", fileProperties);

    vendorLibraryProperties.put("externalizedFiles", new String[]{"/files/file-2"});
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);
    vendorLibraryProperties.put("externalizedFiles", new String[]{"/files/file-3"});
    context.create().resource("/etc/vendor-libraries/library-2", vendorLibraryProperties);

    properties.put("externalizedFiles", new String[]{"/files/file-1"});
    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(2, uiFramework.getVendorLibraries().size());
    assertEquals(3, uiFramework.getExternalizedFiles().size());
    assertEquals("file-2", uiFramework.getExternalizedFiles().get(0).getName());
    assertEquals("file-3", uiFramework.getExternalizedFiles().get(1).getName());
    assertEquals("file-1", uiFramework.getExternalizedFiles().get(2).getName());
  }

  @Test
  public void testGetTemplateFiles() {
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/templates/file-1.html", fileProperties);
    context.create().resource("/ui-framework/templates/file-2.html", fileProperties);
    context.create().resource("/ui-framework/templates/file-3.html", fileProperties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(3, uiFramework.getTemplateFiles().size());
    assertEquals("file-1.html", uiFramework.getTemplateFiles().get(0).getName());
    assertEquals("file-2.html", uiFramework.getTemplateFiles().get(1).getName());
    assertEquals("file-3.html", uiFramework.getTemplateFiles().get(2).getName());
  }

  @Test
  public void testGetTemplateFilesWhenInheritingFromVendorLibrary() {
    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2"});
    resource = context.create().resource("/ui-framework", properties);
    context.create().resource("/ui-framework/templates/file-1.html", fileProperties);
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-1/templates/file-2.html",
        fileProperties);
    context.create().resource("/etc/vendor-libraries/library-2", vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-2/templates/file-3.html",
        fileProperties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(3, uiFramework.getTemplateFiles().size());
    assertEquals("file-1.html", uiFramework.getTemplateFiles().get(0).getName());
    assertEquals("file-2.html", uiFramework.getTemplateFiles().get(1).getName());
    assertEquals("file-3.html", uiFramework.getTemplateFiles().get(2).getName());
  }

  @Test
  public void testGetTemplatesPath() throws ResourceNotFoundException {
    context.create().resource(
        "/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework.html");
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);
    when(htlTemplateCacheService.getCompiledTemplateFilePath(uiFramework)).thenReturn(
        "/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework.html");

    assertEquals("/apps/kestros/cache/compiled-htl-templates/etc/ui-frameworks/ui-framework.html",
        uiFramework.getTemplatesPath());
  }

  @Test
  public void testGetFontAwesomeIcon() throws ResourceNotFoundException {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("icon", uiFramework.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenDefault() throws ResourceNotFoundException {
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("fas fa-palette", uiFramework.getFontAwesomeIcon());
  }
}
