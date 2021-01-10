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

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class VendorLibraryTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VendorLibrary vendorLibrary;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> fileJcrContentProperties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");

    properties.put("jcr:primaryType", "kes:VendorLibrary");

    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/html");
  }

  @Test
  public void testGetDocumentationUrl() throws Exception {
    properties.put("documentationUrl", "http://kestros.io");

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals("http://kestros.io", vendorLibrary.getDocumentationUrl());
  }

  @Test
  public void testGetDependencies() {
    context.create().resource("/etc/vendor-libraries/library1", properties);
    context.create().resource("/etc/vendor-libraries/library2", properties);

    properties.put("dependencies",
        new String[]{"/etc/vendor-libraries/library1", "/etc/vendor-libraries/library2"});

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(2, vendorLibrary.getDependencies().size());
    assertEquals("library1", vendorLibrary.getDependencies().get(0).getName());
    assertEquals("library2", vendorLibrary.getDependencies().get(1).getName());
  }


  @Test
  public void testGetTemplateFiles() {
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates");

    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-1.html",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-1.html/jcr:content",
        fileJcrContentProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-2.html",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-2.html/jcr:content",
        fileJcrContentProperties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(2, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  public void testGetTemplateFilesWhenTemplatesFolderNotFound() {
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(0, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  @Ignore
  public void testGetTemplateFilesWhenFileTypesAreInvalid() {
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates");

    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-1",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-1/jcr:content");
    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-2",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-2/jcr:content");

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(2, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  public void testGetIncludedCdnJsScripts() {
    properties.put("includedCdnJsScripts", new String[]{"test.js"});
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(1, vendorLibrary.getIncludedCdnJsScripts().size());
  }

  @Test
  public void testGetFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals("icon", vendorLibrary.getFontAwesomeIcon());
  }

  @Test
  public void testGetFontAwesomeIconWhenDefault() {
    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);
    assertEquals("fas fa-shapes", vendorLibrary.getFontAwesomeIcon());
  }
}