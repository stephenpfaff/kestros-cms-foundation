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

package io.kestros.cms.foundation.design.htltemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HtlTemplateFileTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateFile htlTemplateFile;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> jcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    properties.put("jcr:primaryType", "nt:file");
    jcrContentProperties.put("jcr:mimeType", "text/html");
  }

  @Test
  public void getTitle() {
    resource = context.create().resource("/template-file", properties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    assertEquals("Template File", htlTemplateFile.getTitle());
  }

  @Test
  public void getTemplates() {
    InputStream templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    jcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/template-file", properties);
    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);

    assertEquals(1, htlTemplateFile.getTemplates().size());
  }

  @Test
  public void getTemplatesWhenIOException() throws IOException {
    InputStream templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    jcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/template-file", properties);
    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    htlTemplateFile = spy(htlTemplateFile);

    doThrow(IOException.class).when(htlTemplateFile).getOutput();

    assertEquals(0, htlTemplateFile.getTemplates().size());
  }

  @Test
  public void getTemplatesWhenInvalid() {
    InputStream templateFileInputStream = new ByteArrayInputStream("<p>123</p>".getBytes());
    jcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/template-file", properties);
    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);

    assertEquals(0, htlTemplateFile.getTemplates().size());
  }
}