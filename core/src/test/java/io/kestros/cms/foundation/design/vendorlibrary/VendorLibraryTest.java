package io.kestros.cms.foundation.design.vendorlibrary;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
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
    fileProperties.put("jcr:primaryType", "nt:file");
    fileJcrContentProperties.put("jcr:mimeType", "text/html");
  }

  @Test
  public void testGetDocumentationUrl() throws Exception {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");
    properties.put("documentationUrl", "http://kestros.io");

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals("http://kestros.io", vendorLibrary.getDocumentationUrl());
  }

  @Test
  public void testGetTemplates() {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");

    InputStream templateFileInputStream1 = new ByteArrayInputStream(
        ("<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>").getBytes());
    InputStream templateFileInputStream2 = new ByteArrayInputStream(
        ("<template data-sly-template.testTemplateTwo=\"${ @ text}\"></template>").getBytes());
    fileJcrContentProperties.put("jcr:data", templateFileInputStream1);

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates");

    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-1",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-1/jcr:content",
        fileJcrContentProperties);

    fileJcrContentProperties.put("jcr:data", templateFileInputStream2);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-2",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-2/jcr:content",
        fileJcrContentProperties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(2, vendorLibrary.getTemplates().size());
    assertEquals("testtemplateone", vendorLibrary.getTemplates().get(0).getTitle());
    assertEquals("testtemplatetwo", vendorLibrary.getTemplates().get(1).getTitle());
  }

  @Test
  public void testGetTemplatesWhenTemplateIsNotValid() {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");

    InputStream templateFileInputStream = new ByteArrayInputStream("<p>123</p>".getBytes());
    fileJcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates");

    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-1",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-1/jcr:content",
        fileJcrContentProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-2",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-2/jcr:content",
        fileJcrContentProperties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(0, vendorLibrary.getTemplates().size());
  }

  @Test
  public void testGetTemplateFiles() {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates");

    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-1",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-1/jcr:content",
        fileJcrContentProperties);
    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file-2",
        fileProperties);
    context.create().resource(
        "/etc/vendor-libraries/vendor-library/templates/template-file-2/jcr:content",
        fileJcrContentProperties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(2, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  public void testGetTemplateFilesWhenTemplatesFolderNotFound() {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");

    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);

    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    assertEquals(0, vendorLibrary.getTemplateFiles().size());
  }

  @Test
  public void testGetTemplateFilesWhenFileTypesAreInvalid() {
    properties.put("sling:resourceType", "kestros/cms/vendor-library");

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

    assertEquals(0, vendorLibrary.getTemplateFiles().size());
  }


}