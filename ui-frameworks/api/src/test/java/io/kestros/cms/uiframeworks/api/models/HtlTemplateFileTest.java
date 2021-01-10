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
  public void getPathWithoutExtension() {
    resource = context.create().resource("/template-file.html", properties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    assertEquals("/template-file", htlTemplateFile.getPathWithoutExtension());
  }

  @Test
  public void getFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/template-file", properties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    assertEquals("icon", htlTemplateFile.getFontAwesomeIcon());
  }

  @Test
  public void getFontAwesomeIconWhenDefault() {
    resource = context.create().resource("/template-file", properties);
    htlTemplateFile = resource.adaptTo(HtlTemplateFile.class);
    assertEquals("fa fa-file", htlTemplateFile.getFontAwesomeIcon());
  }
}