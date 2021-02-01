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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BaseUiFrameworkLibraryResourceTest {

  @Rule
  public SlingContext context = new SlingContext();
  private VendorLibraryResource vendorLibrary;
  private Resource resource;
  private Map<String, Object> managedLibraryProperties = new HashMap<>();
  private Map<String, Object> properties = new HashMap<>();
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    htlTemplateFileRetrievalService = mock(HtlTemplateFileRetrievalService.class);
    context.registerService(HtlTemplateFileRetrievalService.class, htlTemplateFileRetrievalService);
    managedLibraryProperties.put("jcr:primaryType", "kes:ManagedVendorLibrary");
  }

  @Test
  public void testGetHtlTemplateFileRetrievalService() {
    resource = context.create().resource("/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    assertEquals(htlTemplateFileRetrievalService,
        vendorLibrary.getHtlTemplateFileRetrievalService());
  }

  @Test
  public void testGetTemplateFiles() throws HtlTemplateFileRetrievalException {
    resource = context.create().resource("/vendor-library", properties);
    List<HtlTemplateFile> htlTemplateFileList = new ArrayList<>();
    HtlTemplateFile htlTemplateFile1 = mock(HtlTemplateFile.class);
    when(htlTemplateFile1.getTitle()).thenReturn("zz_template");
    HtlTemplateFile htlTemplateFile2 = mock(HtlTemplateFile.class);
    when(htlTemplateFile2.getTitle()).thenReturn("aa_template");

    htlTemplateFileList.add(htlTemplateFile1);
    htlTemplateFileList.add(htlTemplateFile2);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    when(htlTemplateFileRetrievalService.getHtlTemplates(vendorLibrary)).thenReturn(
        htlTemplateFileList);

    assertEquals(2, vendorLibrary.getTemplateFiles().size());
    assertEquals("aa_template", vendorLibrary.getTemplateFiles().get(0).getTitle());
    assertEquals("zz_template", vendorLibrary.getTemplateFiles().get(1).getTitle());
  }

  @Test
  public void testGetTemplateFilesWhenRetrievalException() throws HtlTemplateFileRetrievalException {
    resource = context.create().resource("/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    when(htlTemplateFileRetrievalService.getHtlTemplates(vendorLibrary)).thenThrow(
        new HtlTemplateFileRetrievalException(""));

    assertEquals(0, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  public void testGetIncludedCdnJsScripts() {
    properties.put("includedCdnJsScripts", new String[]{"script-1", "script-2", "script-3"});
    resource = context.create().resource("/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    assertEquals(3, vendorLibrary.getIncludedCdnJsScripts().size());
  }

  @Test
  public void testGetIncludedCdnCssScripts() {
    properties.put("includedCdnCssScripts", new String[]{"script-1", "script-2", "script-3"});
    resource = context.create().resource("/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    assertEquals(3, vendorLibrary.getIncludedCdnCssScripts().size());
  }

  @Test
  public void testGetExternalizedFiles() {
    properties.put("externalizedFiles",
        new String[]{"/vendor-library/assets/file-1", "/vendor-library/assets/file-2",
            "/vendor-library/assets/file-3"});
    resource = context.create().resource("/vendor-library", properties);
    context.create().resource("/vendor-library/assets/file-1");
    context.create().resource("/vendor-library/assets/file-2");
    context.create().resource("/vendor-library/assets/file-3");

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    assertEquals(3, vendorLibrary.getExternalizedFiles().size());
  }

  @Test
  public void testGetRootResource() throws NoValidAncestorException {
    context.create().resource("/etc/vendor-libraries/managed-library", managedLibraryProperties);
    resource = context.create().resource("/etc/vendor-libraries/managed-library/versions/0.0.1", properties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);

    assertEquals("/etc/vendor-libraries/managed-library",
        vendorLibrary.getRootResource().getPath());
  }
}