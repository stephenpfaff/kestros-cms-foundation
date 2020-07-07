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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HtlTemplateTest {


  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplate htlTemplate;

  private HtlTemplateFile htlTemplateFile;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  private Map<String, Object> jcrContentProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    properties.put("jcr:primaryType", "nt:file");
    jcrContentProperties.put("jcr:mimeType", "text/html");

    InputStream templateFileInputStream = new ByteArrayInputStream(
        ("<template data-sly-template.testTemplate=\"${ @ text}\" "
         + "data-description=\"template-description\"><p>${text}</p></template>").getBytes());
    jcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/template-file", properties);
    context.create().resource("/template-file/jcr:content", jcrContentProperties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);

    htlTemplate = htlTemplateFile.getTemplates().get(0);
  }


  @Test
  public void getSourcePath() {
    assertEquals("/template-file", htlTemplate.getSourcePath());
  }

  @Test
  public void getName() {
    assertEquals("testtemplate", htlTemplate.getName());
  }

  @Test
  public void getTitle() {
    assertEquals("testtemplate", htlTemplate.getTitle());
  }

  @Test
  public void getDescription() {
    assertEquals("template-description", htlTemplate.getDescription());
  }

  @Test
  public void getOutput() {
    assertEquals("<template data-sly-template.testtemplate=\"${ @ text}\" "
                 + "data-description=\"template-description\"><p>${text}</p></template>",
        htlTemplate.getOutput());
  }

  @Test
  public void getHtmlOutput() {
    assertEquals("<p>${text}</p>", htlTemplate.getHtmlOutput());
  }

  @Test
  public void getHtmlOutputWhenStartsWithNewline() {
    InputStream templateFileInputStream = new ByteArrayInputStream(
        ("\n<template data-sly-template.testTemplate=\"${ @ text}\" "
         + "data-description=\"template-description\"><p>${text}</p></template>").getBytes());
    jcrContentProperties.put("jcr:data", templateFileInputStream);

    resource = context.create().resource("/template-file-newline", properties);
    context.create().resource("/template-file-newline/jcr:content", jcrContentProperties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);

    htlTemplate = htlTemplateFile.getTemplates().get(0);

    assertEquals("<p>${text}</p>", htlTemplate.getHtmlOutput());
  }

  @Test
  public void getVariables() {
    assertEquals(1, htlTemplate.getParameterNames().size());
  }

  @Test
  public void getSampleUsage() {
    assertEquals("<sly data-sly-call=\"${templates.testtemplate @\n" + "     text=myText}\" />",
        htlTemplate.getSampleUsage());
  }
}