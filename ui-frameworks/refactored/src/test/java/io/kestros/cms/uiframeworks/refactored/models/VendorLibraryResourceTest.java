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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VendorLibraryResourceTest {

  @Rule
  public SlingContext context = new SlingContext();
  private VendorLibraryResource vendorLibrary;
  private Resource resource;
  private Map<String, Object> properties = new HashMap<>();
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    htlTemplateFileRetrievalService = mock(HtlTemplateFileRetrievalService.class);
    context.registerService(HtlTemplateFileRetrievalService.class, htlTemplateFileRetrievalService);
  }

  @Test
  public void testGetHtlTemplateFileRetrievalService() {
    resource = context.create().resource("/vendor-library", properties);
    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);
    assertNotNull(vendorLibrary.getHtlTemplateFileRetrievalService());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/vendor-library", properties);
    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);
    assertEquals("icon", vendorLibrary.getFontAwesomeIcon());
  }

  @Test
  public void testGetDocumentationUrl() {
    properties.put("documentationUrl", "documentation-url");
    resource = context.create().resource("/vendor-library", properties);
    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);
    assertEquals("documentation-url", vendorLibrary.getDocumentationUrl());
  }
}