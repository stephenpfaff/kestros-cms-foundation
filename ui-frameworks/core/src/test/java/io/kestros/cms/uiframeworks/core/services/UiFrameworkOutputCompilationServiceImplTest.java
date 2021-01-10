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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkCompilationAddonService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiFrameworkOutputCompilationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private Resource resource;
  private UiFramework uiFramework;
  private UiLibraryCompilationService uiLibraryCompilationService;
  private UiFrameworkOutputCompilationServiceImpl uiFrameworkOutputCompilationService;
  private UiFrameworkCompilationAddonService uiFrameworkCompilationAddonService;
  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  private Map<String, Object> scriptTypeFolderProperties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    uiLibraryCompilationService = mock(UiLibraryCompilationService.class);
    uiFrameworkCompilationAddonService = mock(UiFrameworkCompilationAddonService.class);

    context.registerService(UiLibraryCompilationService.class, uiLibraryCompilationService);

    uiFrameworkOutputCompilationService = new UiFrameworkOutputCompilationServiceImpl();
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("UI Framework Output Compilation Service",
        uiFrameworkOutputCompilationService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = spy(new FormattingResultLog());
    uiFrameworkOutputCompilationService.runAdditionalHealthChecks(log);

    verify(log, never()).debug(any());
    verify(log, never()).info(any());
    verify(log, never()).warn(any());
    verify(log, never()).critical(any());
    verify(log, never()).healthCheckError(any());
  }

  @Test
  public void testGetOutputWhenStandAloneAndCss() throws InvalidResourceTypeException {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{}");

    resource = context.create().resource("/ui-framework", properties);

    scriptTypeFolderProperties.put("include", "file.css");
    context.create().resource("/ui-framework/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/ui-framework/css/file.css", fileProperties);

    context.create().resource("/ui-framework/css/file.css/jcr:content", fileJcrContentProperties);

    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasVendorLibrary() throws Exception {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{}").thenReturn(".test-output{}").thenReturn("");

    context.create().resource("/etc/vendor-libraries",vendorLibraryProperties);
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);
    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/etc/vendor-libraries/library-1/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css", fileProperties);

    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    context.create().resource("/etc/vendor-libraries/library-2", vendorLibraryProperties);
    scriptTypeFolderProperties.put("include", "library-2.css");
    context.create().resource("/etc/vendor-libraries/library-2/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-2.css", fileProperties);

    context.create().resource("/etc/vendor-libraries/library-1/css/library-2.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1", "library-2"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}\n.test-output{}",
        uiFrameworkOutputCompilationService.getUiFrameworkOutput(uiFramework, CSS));
  }

  @Test
  public void testGetCssOutputWhenHasVendorLibraryAndVendorLibraryIsInvalid() throws Exception {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenThrow(InvalidResourceTypeException.class);

    context.create().resource("/etc/vendor-libraries");
    context.create().resource("/etc/vendor-libraries/library-1",vendorLibraryProperties);
    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/etc/vendor-libraries/library-1/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css", fileProperties);

    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("",
        uiFrameworkOutputCompilationService.getUiFrameworkOutput(uiFramework, CSS));
  }

  @Test
  public void testGetCssOutputWhenHasVendorLibraryAndUiFrameworkCss() throws Exception {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(".vendor-library{}").thenReturn(".ui-framework{}");

    context.create().resource("/etc/vendor-libraries");
    context.create().resource("/etc/vendor-libraries/library-1",vendorLibraryProperties);
    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/etc/vendor-libraries/library-1/css", scriptTypeFolderProperties);

    fileProperties.put("jcr:primaryType", "nt:file");
    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css", fileProperties);

    context.create().resource("/etc/vendor-libraries/library-1/css/library-1.css/jcr:content",
        fileJcrContentProperties);

    properties.put("kes:vendorLibraries", new String[]{"library-1"});

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".vendor-library{}\n.ui-framework{}",
        uiFrameworkOutputCompilationService.getUiFrameworkOutput(uiFramework, CSS));
  }

  @Test
  public void testGetCssOutputWhenUiFrameworkThrowsInvalidResourceType() throws Exception {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenThrow(InvalidResourceTypeException.class);

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals("",
        uiFrameworkOutputCompilationService.getUiFrameworkOutput(uiFramework, CSS));
  }

  @Test
  public void testGetCssOutputWhenHasComponentsWithUiFrameworkView() throws Exception {
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{}");

    context.create().resource("/apps/components");

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/css",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/css/library-1.css",
        fileProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasAddonService() throws Exception {
    when(uiFrameworkCompilationAddonService.getAppendedOutput(any(), any())).thenReturn(
        ".addon-css{}");
    context.registerService(UiFrameworkCompilationAddonService.class,
        uiFrameworkCompilationAddonService);
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{}");

    context.create().resource("/apps/components");

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/css",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/css/library-1.css",
        fileProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}\n.addon-css{}", uiFramework.getOutput(CSS));
  }

  @Test
  public void testGetCssOutputWhenHasAddonServiceAndThrowInvalidResourceTypeException() throws Exception {
    when(uiFrameworkCompilationAddonService.getAppendedOutput(any(), any())).thenThrow(InvalidResourceTypeException.class);
    context.registerService(UiFrameworkCompilationAddonService.class,
        uiFrameworkCompilationAddonService);
    context.registerInjectActivateService(uiFrameworkOutputCompilationService);

    when(uiLibraryCompilationService.getUiLibraryOutput(any(), any(), any())).thenReturn(
        ".test-output{}");

    context.create().resource("/apps/components");

    scriptTypeFolderProperties.put("include", "library-1.css");
    context.create().resource("/apps/components/component/test-code/css",
        scriptTypeFolderProperties);

    context.create().resource("/apps/components/component/test-code/css/library-1.css",
        fileProperties);

    properties.put("kes:uiFrameworkCode", "test-code");

    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFramework.class);

    assertEquals(".test-output{}", uiFramework.getOutput(CSS));
  }
}