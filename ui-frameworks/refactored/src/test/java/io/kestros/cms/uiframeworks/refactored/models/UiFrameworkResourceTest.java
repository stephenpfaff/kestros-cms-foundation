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

package io.kestros.cms.uiframeworks.refactored.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.exceptions.ThemeRetrievalException;
import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiFrameworkResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFrameworkResource uiFramework;

  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  private VendorLibraryRetrievalService vendorLibraryRetrievalService;

  private ThemeRetrievalService themeRetrievalService;

  private Resource resource;

  private Map<String, Object> managedUiFrameworkProperties = new HashMap<>();

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> vendorLibraryProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    vendorLibraryRetrievalService = mock(VendorLibraryRetrievalService.class);
    themeRetrievalService = mock(ThemeRetrievalService.class);

    htlTemplateFileRetrievalService = mock(HtlTemplateFileRetrievalService.class);

    managedUiFrameworkProperties.put("jcr:primaryType", "kes:ManagedUiFramework");
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
  }

  @Test
  public void testGetFrameworkCode() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/ui-framework", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    assertEquals("code", uiFramework.getFrameworkCode());
  }

  @Test
  public void testGetFrameworkCodeWhenInherited() {
    managedUiFrameworkProperties.put("kes:uiFrameworkCode", "inherited-code");
    context.create().resource("/etc/ui-frameworks/managed-framework", managedUiFrameworkProperties);
    resource = context.create().resource("/etc/ui-frameworks/managed-framework/versions/0.0.1",
        properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals("inherited-code", uiFramework.getFrameworkCode());
  }

  @Test
  public void testGetVendorLibraries()
      throws LoginException, VendorLibraryRetrievalException, VersionRetrievalException {
    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);

    VendorLibrary vendorLibrary1 = mock(VendorLibrary.class);
    VendorLibrary vendorLibrary2 = mock(VendorLibrary.class);
    VendorLibrary vendorLibrary3 = mock(VendorLibrary.class);

    when(vendorLibraryRetrievalService.getVendorLibrary("vendor-library-1", true, true)).thenReturn(
        vendorLibrary1);
    when(vendorLibraryRetrievalService.getVendorLibrary("vendor-library-2", true, true)).thenReturn(
        vendorLibrary2);
    when(vendorLibraryRetrievalService.getVendorLibrary("vendor-library-3", true, true)).thenReturn(
        vendorLibrary3);

    properties.put("includeEtcVendorLibraries", Boolean.TRUE);

    properties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-2", "vendor-library-3"});

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(3, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetVendorLibrariesWhenRetrievalException()
      throws LoginException, VendorLibraryRetrievalException, VersionRetrievalException {
    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);

    VendorLibrary vendorLibrary2 = mock(VendorLibrary.class);
    VendorLibrary vendorLibrary3 = mock(VendorLibrary.class);

    when(vendorLibraryRetrievalService.getVendorLibrary(eq("vendor-library-1"), eq(true),
        eq(false))).thenThrow(new VendorLibraryRetrievalException(""));
    when(vendorLibraryRetrievalService.getVendorLibrary(eq("vendor-library-2"), eq(true),
        eq(true))).thenReturn(vendorLibrary2);
    when(vendorLibraryRetrievalService.getVendorLibrary(eq("vendor-library-3"), eq(true),
        eq(true))).thenReturn(vendorLibrary3);

    properties.put("includeEtcVendorLibraries", Boolean.TRUE);

    properties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-2", "vendor-library-3"});

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(2, uiFramework.getVendorLibraries().size());
  }

  @Test
  public void testGetThemes() throws ThemeRetrievalException {
    context.registerService(ThemeRetrievalService.class, themeRetrievalService);

    Theme theme1 = mock(Theme.class);
    Theme theme2 = mock(Theme.class);
    Theme theme3 = mock(Theme.class);

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    when(themeRetrievalService.getThemes(uiFramework)).thenReturn(
        Arrays.asList(theme1, theme2, theme3));

    assertEquals(3, uiFramework.getThemes().size());
  }

  @Test
  public void testGetThemesWhenServiceIsNull() {
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(0, uiFramework.getThemes().size());
  }


  @Test
  public void testIsIncludeEtcVendorLibraries() {
    properties.put("includeEtcVendorLibraries", Boolean.TRUE);

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertTrue(uiFramework.isIncludeEtcVendorLibraries());
  }

  @Test
  public void testIsIncludeEtcVendorLibrariesWhenDefault() {
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertTrue(uiFramework.isIncludeEtcVendorLibraries());
  }

  @Test
  public void testIsIncludeLibsVendorLibraries() {
    properties.put("includeLibsVendorLibraries", Boolean.TRUE);

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertTrue(uiFramework.isIncludeEtcVendorLibraries());
  }

  @Test
  public void testIsIncludeLibsVendorLibrariesDefault() {
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertTrue(uiFramework.isIncludeEtcVendorLibraries());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    assertEquals("icon", uiFramework.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenInheritedFromVersionable() {
    managedUiFrameworkProperties.put("fontAwesomeIcon", "inherited-icon");
    context.create().resource("/etc/ui-frameworks/managed-framework", managedUiFrameworkProperties);

    resource = context.create().resource("/etc/ui-frameworks/managed-framework/versions/0.0.1",
        properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals("inherited-icon", uiFramework.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenNoValidAncestor() {
    managedUiFrameworkProperties.put("fontAwesomeIcon", "inherited-icon");
    context.create().resource("/etc/ui-frameworks/managed-framework");

    resource = context.create().resource("/etc/ui-frameworks/managed-framework/versions/0.0.1",
        properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals("fas fa-palette", uiFramework.getFontAwesomeIcon());
  }

  @Test
  public void testGetManagingResourceType() {
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);
    assertEquals(ManagedUiFrameworkResource.class, uiFramework.getManagingResourceType());
  }

  @Test
  public void testGetTemplateFiles()
      throws VendorLibraryRetrievalException, VersionRetrievalException,
             HtlTemplateFileRetrievalException {
    context.registerService(VendorLibraryRetrievalService.class, vendorLibraryRetrievalService);
    context.registerService(HtlTemplateFileRetrievalService.class, htlTemplateFileRetrievalService);

    HtlTemplateFile htlTemplateFile1 = mock(HtlTemplateFile.class);
    HtlTemplateFile htlTemplateFile2 = mock(HtlTemplateFile.class);
    HtlTemplateFile htlTemplateFile3 = mock(HtlTemplateFile.class);
    HtlTemplateFile htlTemplateFile4 = mock(HtlTemplateFile.class);

    VendorLibrary vendorLibrary1 = mock(VendorLibrary.class);
    VendorLibrary vendorLibrary2 = mock(VendorLibrary.class);

    when(
        vendorLibraryRetrievalService.getVendorLibrary("vendor-library-1", true, false)).thenReturn(
        vendorLibrary1);
    when(
        vendorLibraryRetrievalService.getVendorLibrary("vendor-library-2", true, false)).thenReturn(
        vendorLibrary2);

    when(vendorLibrary1.getTemplateFiles()).thenReturn(
        Arrays.asList(htlTemplateFile2, htlTemplateFile3));
    when(vendorLibrary2.getTemplateFiles()).thenReturn(Arrays.asList(htlTemplateFile4));

    when(
        htlTemplateFileRetrievalService.getHtlTemplates(any(UiFrameworkResource.class))).thenReturn(
        Arrays.asList(htlTemplateFile1));

    properties.put("kes:vendorLibraries", new String[]{"vendor-library-1", "vendor-library-2"});

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(4, uiFramework.getTemplateFiles().size());
  }

  @Test
  public void testGetHtlTemplateFileRetrievalService() {
    context.registerService(HtlTemplateFileRetrievalService.class, htlTemplateFileRetrievalService);

    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(htlTemplateFileRetrievalService, uiFramework.getHtlTemplateFileRetrievalService());
  }

  @Test
  public void testGetIncludedVendorLibraryNames() {
    properties.put("kes:vendorLibraries",
        new String[]{"vendor-library-1", "vendor-library-2", "vendor-library-3"});
    resource = context.create().resource("/ui-library", properties);
    uiFramework = resource.adaptTo(UiFrameworkResource.class);

    assertEquals(3, uiFramework.getIncludedVendorLibraryNames().size());
  }
}
