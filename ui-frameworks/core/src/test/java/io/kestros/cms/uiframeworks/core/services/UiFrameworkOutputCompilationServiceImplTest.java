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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkCompilationAddonService;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.uiframeworks.core.models.UiFrameworkResource;
import io.kestros.cms.uiframeworks.core.models.VendorLibraryResource;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.basecompilers.filetypes.ScriptTypes;
import io.kestros.commons.uilibraries.basecompilers.filetypes.css.CssFile;
import io.kestros.commons.uilibraries.basecompilers.services.CssCompilerService;
import io.kestros.commons.uilibraries.core.services.impl.UiLibraryCompilationServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class UiFrameworkOutputCompilationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFrameworkOutputCompilationServiceImpl compilationService;
  private UiLibraryCompilationService uiLibraryCompilationService;

  private CssCompilerService cssCompilerService;

  private VendorLibraryRetrievalService vendorLibraryRetrievalService;
  private UiFrameworkCompilationAddonService addonService1;
  private UiFrameworkCompilationAddonService addonService2;
  private UiFrameworkCompilationAddonService addonService3;

  private UiFrameworkResource uiFramework;
  private VendorLibraryResource vendorLibrary1;
  private VendorLibraryResource vendorLibrary2;

  private Resource resource;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();
  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  private Map<String, Object> cssFolderProperties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();

  private Map<String, Object> cssFileProperties = new HashMap<>();

  InputStream file1InputStream;
  InputStream file2InputStream;
  InputStream file3InputStream;
  InputStream file4InputStream;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    compilationService = spy(new UiFrameworkOutputCompilationServiceImpl());
    uiLibraryCompilationService = spy(new UiLibraryCompilationServiceImpl());
    cssCompilerService = spy(new CssCompilerService());
    vendorLibraryRetrievalService = mock(VendorLibraryRetrievalService.class);

    addonService1 = mock(UiFrameworkCompilationAddonService.class);
    addonService2 = mock(UiFrameworkCompilationAddonService.class);
    addonService3 = mock(UiFrameworkCompilationAddonService.class);

    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
    fileProperties.put("jcr:primaryType", "nt:file");
    cssFileProperties.put("jcr:mimeType", "text/css");

    file1InputStream = new ByteArrayInputStream("file-1".getBytes());
    file2InputStream = new ByteArrayInputStream("file-2".getBytes());
    file3InputStream = new ByteArrayInputStream("file-3".getBytes());
    file4InputStream = new ByteArrayInputStream("file-4".getBytes());

    cssFolderProperties.put("include", "file.css");

    vendorLibrary1 = context.create().resource("/etc/vendor-libraries/vendor-library-1",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);
    context.create().resource("/etc/vendor-libraries/vendor-library-1/css", cssFolderProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-1/css/file.css",
        fileProperties);
    cssFileProperties.put("jcr:data", file3InputStream);
    context.create().resource("/etc/vendor-libraries/vendor-library-1/css/file.css/jcr:content",
        cssFileProperties);

    vendorLibrary2 = context.create().resource("/etc/vendor-libraries/vendor-library-2",
        vendorLibraryProperties).adaptTo(VendorLibraryResource.class);
    context.create().resource("/etc/vendor-libraries/vendor-library-2/css", cssFolderProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library-2/css/file.css",
        fileProperties);
    cssFileProperties.put("jcr:data", file4InputStream);
    context.create().resource("/etc/vendor-libraries/vendor-library-2/css/file.css/jcr:content",
        cssFileProperties);
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Framework Output Compilation Service", compilationService.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    compilationService.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    compilationService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    compilationService.runAdditionalHealthChecks(log);
    verifyZeroInteractions(log);
  }

  @Test
  public void testGetUiFrameworkSource()
      throws NoMatchingCompilerException, InvalidResourceTypeException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/css", cssFolderProperties);
    context.create().resource("/ui-framework/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-framework/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/css/file-2.css/jcr:content", cssFileProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    assertEquals("file-1\nfile-2",
        compilationService.getUiFrameworkSource(uiFramework, ScriptTypes.CSS));
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput( any(), any());
    verify(uiLibraryCompilationService, times(1)).getLibraryScriptTypes(any(), any());
    verify(uiLibraryCompilationService, times(1)).getUiLibrarySource(any(), any());
  }

  @Test
  public void testGetUiFrameworkSourceWhenHasVendorLibraries()
      throws NoMatchingCompilerException, InvalidResourceTypeException,
             VendorLibraryRetrievalException, VersionRetrievalException {
    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-1"});
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/css", cssFolderProperties);
    context.create().resource("/ui-framework/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-framework/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/css/file-2.css/jcr:content", cssFileProperties);

    when(vendorLibraryRetrievalService.getVendorLibrary(any(), anyBoolean(),
        anyBoolean())).thenReturn(vendorLibrary1).thenReturn(vendorLibrary2);

    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals("file-3\nfile-4\nfile-1\nfile-2",
        compilationService.getUiFrameworkSource(uiFramework, ScriptTypes.CSS));
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput( any(), any());
    verify(uiLibraryCompilationService, times(3)).getLibraryScriptTypes(any(), any());
    verify(uiLibraryCompilationService, times(3)).getUiLibrarySource(any(), any());
  }

  @Test
  public void testGetUiFrameworkSourceWhenCssFileIsInvalidResourceType()
      throws NoMatchingCompilerException, InvalidResourceTypeException,
             VendorLibraryRetrievalException, VersionRetrievalException {
    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-1"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);

    when(vendorLibraryRetrievalService.getVendorLibrary(any(), anyBoolean(),
        anyBoolean())).thenReturn(vendorLibrary1).thenReturn(vendorLibrary2);

    doThrow(new InvalidResourceTypeException("", CssFile.class)).when(
        uiLibraryCompilationService).getUiLibrarySource(any(), any());

    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals("", compilationService.getUiFrameworkSource(uiFramework, ScriptTypes.CSS));
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput( any(), any());
    verify(uiLibraryCompilationService, never()).getLibraryScriptTypes(any(), any());
    verify(uiLibraryCompilationService, times(3)).getUiLibrarySource(any(), any());
  }

  @Test
  public void testGetUiFrameworkSourceWhenHasAddonServices()
      throws NoMatchingCompilerException, InvalidResourceTypeException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/css", cssFolderProperties);
    context.create().resource("/ui-framework/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-framework/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/css/file-2.css/jcr:content", cssFileProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    when(addonService1.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-1");
    when(addonService2.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-2");
    when(addonService3.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-3");

    context.registerService(UiFrameworkCompilationAddonService.class, addonService1);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService2);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService3);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    assertEquals("file-1\nfile-2\naddon-1\naddon-2\naddon-3",
        compilationService.getUiFrameworkSource(uiFramework, ScriptTypes.CSS));
    verify(uiLibraryCompilationService, never()).getUiLibraryOutput( any(), any());
    verify(uiLibraryCompilationService, times(1)).getLibraryScriptTypes(any(), any());
    verify(uiLibraryCompilationService, times(1)).getUiLibrarySource(any(), any());
  }

  @Test
  public void testGetUiFrameworkScriptTypes()
      throws NoMatchingCompilerException, InvalidResourceTypeException {
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/css", cssFolderProperties);
    context.create().resource("/ui-framework/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-framework/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/css/file-2.css/jcr:content", cssFileProperties);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    assertEquals(1,
        compilationService.getUiFrameworkScriptTypes(uiFramework, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetUiFrameworkScriptTypesWhenHasVendorLibraries()
      throws NoMatchingCompilerException, InvalidResourceTypeException,
             VendorLibraryRetrievalException, VersionRetrievalException {
    uiFrameworkProperties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-1"});
    cssFolderProperties.put("include", new String[]{"file-1.css", "file-2.css"});

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);
    context.create().resource("/ui-framework/css", cssFolderProperties);
    context.create().resource("/ui-framework/css/file-1.css", fileProperties);
    cssFileProperties.put("jcr:data", file1InputStream);
    context.create().resource("/ui-framework/css/file-1.css/jcr:content", cssFileProperties);
    context.create().resource("/ui-framework/css/file-2.css", fileProperties);
    cssFileProperties.put("jcr:data", file2InputStream);
    context.create().resource("/ui-framework/css/file-2.css/jcr:content", cssFileProperties);

    when(vendorLibraryRetrievalService.getVendorLibrary(any(), anyBoolean(),
        anyBoolean())).thenReturn(vendorLibrary1).thenReturn(vendorLibrary2);

    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(1,
        compilationService.getUiFrameworkScriptTypes(uiFramework, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetUiFrameworkScriptTypesWhenHasAddonServices()
      throws NoMatchingCompilerException, InvalidResourceTypeException {

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);

    when(addonService1.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-1");
    when(addonService2.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-2");
    when(addonService3.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-3");

    when(addonService1.getAddonScriptTypes(any(),any())).thenReturn(Collections.singletonList(ScriptTypes.CSS));

    context.registerService(UiFrameworkCompilationAddonService.class, addonService1);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService2);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService3);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(1,
        compilationService.getUiFrameworkScriptTypes(uiFramework, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetUiFrameworkScriptTypesWhenHasWithInvalidResourceType()
      throws NoMatchingCompilerException, InvalidResourceTypeException {

    resource = context.create().resource("/ui-framework", uiFrameworkProperties);

    when(addonService1.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-1");
    when(addonService2.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-2");
    when(addonService3.getAppendedOutput(uiFramework, ScriptTypes.CSS)).thenReturn("addon-3");

    when(addonService1.getAddonScriptTypes(any(),any())).thenThrow(new InvalidResourceTypeException("", VendorLibraryResource.class));
    when(addonService2.getAddonScriptTypes(any(),any())).thenReturn(Collections.singletonList(ScriptTypes.CSS));

    context.registerService(UiFrameworkCompilationAddonService.class, addonService1);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService2);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService3);
    context.registerInjectActivateService(cssCompilerService);
    context.registerInjectActivateService(uiLibraryCompilationService);
    context.registerInjectActivateService(compilationService);

    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(1,
        compilationService.getUiFrameworkScriptTypes(uiFramework, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetUiFrameworkScriptTypesWhenUiLibraryCompilationServiceIsNull()
      throws NoMatchingCompilerException, InvalidResourceTypeException {
    context.registerInjectActivateService(compilationService);

    assertEquals(0,
        compilationService.getUiFrameworkScriptTypes(uiFramework, ScriptTypes.CSS).size());
  }

  @Test
  public void testGetAddonServices() {
    context.registerService(UiFrameworkCompilationAddonService.class, addonService1);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService2);
    context.registerService(UiFrameworkCompilationAddonService.class, addonService3);

    context.registerInjectActivateService(compilationService);

    assertEquals(3, compilationService.getAddonServices().size());
  }
}
