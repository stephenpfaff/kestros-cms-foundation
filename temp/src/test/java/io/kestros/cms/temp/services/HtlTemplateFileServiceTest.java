package io.kestros.cms.temp.services;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

public class HtlTemplateFileServiceTest {


  @Test
  @Ignore
  public void getTemplates() {
    //    InputStream templateFileInputStream = new ByteArrayInputStream(
    //        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    //    jcrContentProperties.put("jcr:data", templateFileInputStream);
    //
    //    resource = context.create().resource("/template-file", properties);
    //    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    //    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    //
    //    assertEquals(1, htlTemplateFile.getTemplates().size());
  }

  @Test
  @Ignore
  public void getTemplatesWhenIOException() throws IOException {
    //    InputStream templateFileInputStream = new ByteArrayInputStream(
    //        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    //    jcrContentProperties.put("jcr:data", templateFileInputStream);
    //
    //    resource = context.create().resource("/template-file", properties);
    //    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    //    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    //    htlTemplateFile = Mockito.spy(htlTemplateFile);
    //
    //    doThrow(IOException.class).when(htlTemplateFile).getFileContent();
    //
    //    assertEquals(0, htlTemplateFile.getTemplates().size());
  }

  @Test
  @Ignore
  public void getTemplatesWhenInvalid() {
    //    InputStream templateFileInputStream = new ByteArrayInputStream("<p>123</p>".getBytes());
    //    jcrContentProperties.put("jcr:data", templateFileInputStream);
    //
    //    resource = context.create().resource("/template-file", properties);
    //    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    //    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    //
    //    assertEquals(0, htlTemplateFile.getTemplates().size());
  }




  @Test
  @Ignore
  public void testGetTemplates() {
    //    InputStream templateFileInputStream1 = new ByteArrayInputStream(
    //        ("<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>").getBytes
    //        ());
    //    InputStream templateFileInputStream2 = new ByteArrayInputStream(
    //        ("<template data-sly-template.testTemplateTwo=\"${ @ text}\"></template>").getBytes
    //        ());
    //    fileJcrContentProperties.put("jcr:data", templateFileInputStream1);
    //
    //    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates");
    //
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file
    //    -1.html",
    //        fileProperties);
    //    context.create().resource(
    //        "/etc/vendor-libraries/vendor-library/templates/template-file-1.html/jcr:content",
    //        fileJcrContentProperties);
    //
    //    fileJcrContentProperties.put("jcr:data", templateFileInputStream2);
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file
    //    -2.html",
    //        fileProperties);
    //    context.create().resource(
    //        "/etc/vendor-libraries/vendor-library/templates/template-file-2.html/jcr:content",
    //        fileJcrContentProperties);
    //
    //    vendorLibrary = resource.adaptTo(VendorLibrary.class);
    //
    //    assertEquals(2, vendorLibrary.getTemplates().size());
    //    assertEquals("testtemplateone", vendorLibrary.getTemplates().get(0).getTitle());
    //    assertEquals("testtemplatetwo", vendorLibrary.getTemplates().get(1).getTitle());
  }

  @Test
  @Ignore
  public void testGetTemplatesWhenTemplateIsNotValid() {
    //    InputStream templateFileInputStream = new ByteArrayInputStream("<p>123</p>".getBytes());
    //    fileJcrContentProperties.put("jcr:data", templateFileInputStream);
    //
    //    resource = context.create().resource("/etc/vendor-libraries/vendor-library", properties);
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates");
    //
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file
    //    -1",
    //        fileProperties);
    //    context.create().resource(
    //        "/etc/vendor-libraries/vendor-library/templates/template-file-1/jcr:content",
    //        fileJcrContentProperties);
    //    context.create().resource("/etc/vendor-libraries/vendor-library/templates/template-file
    //    -2",
    //        fileProperties);
    //    context.create().resource(
    //        "/etc/vendor-libraries/vendor-library/templates/template-file-2/jcr:content",
    //        fileJcrContentProperties);
    //
    //    vendorLibrary = resource.adaptTo(VendorLibrary.class);
    //
    //    assertEquals(0, vendorLibrary.getTemplates().size());
  }

}