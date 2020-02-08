package io.kestros.cms.foundation.componenttypes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HtmlFileTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetFileType() throws Exception {
    HtmlFile htmlFile = new HtmlFile();

    assertEquals("html", htmlFile.getFileType().getExtension());
    assertEquals("html", htmlFile.getFileType().getName());
    assertEquals("text/html", htmlFile.getFileType().getOutputContentType());
    assertEquals(1, htmlFile.getFileType().getReadableContentTypes().size());
    assertEquals("text/html", htmlFile.getFileType().getReadableContentTypes().get(0));
  }
}