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

package io.kestros.cms.uiframeworks.refactored.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.refactored.models.HtlTemplateFileResource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class HtlTemplateCompilationServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateCompilationServiceImpl htlTemplateCompilationService;

  private List<HtlTemplateFile> templateFileList = new ArrayList<>();
  private HtlTemplateFile templateFile1;
  private HtlTemplateFile templateFile2;
  private HtlTemplateFile templateFile3;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> templateFileJcrContentProperties1 = new HashMap<>();
  private Map<String, Object> templateFileJcrContentProperties2 = new HashMap<>();
  private Map<String, Object> templateFileJcrContentProperties3 = new HashMap<>();


  @Before
  public void setup() {
    context.addModelsForPackage("io.kestros");
    htlTemplateCompilationService = new HtlTemplateCompilationServiceImpl();
    context.registerInjectActivateService(htlTemplateCompilationService);

    properties.put("jcr:primaryType", "nt:file");
    templateFileJcrContentProperties1.put("jcr:mimeType", "text/html");
    templateFileJcrContentProperties2.put("jcr:mimeType", "text/html");
    templateFileJcrContentProperties3.put("jcr:mimeType", "text/html");

    InputStream templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>".getBytes());
    templateFileJcrContentProperties1.put("jcr:data", templateFileInputStream);

    templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateTwo=\"${ @ text}\"></template>".getBytes());
    templateFileJcrContentProperties2.put("jcr:data", templateFileInputStream);

    templateFileInputStream = new ByteArrayInputStream(
        "<template data-sly-template.testTemplateThree=\"${ @ text}\"></template>".getBytes());
    templateFileJcrContentProperties3.put("jcr:data", templateFileInputStream);
  }

  @Test
  public void testGetCompiledHtlTemplateFileOutput() throws IOException {
    templateFile1 = context.create().resource("/file-1", properties).adaptTo(
        HtlTemplateFileResource.class);
    templateFile2 = context.create().resource("/file-2", properties).adaptTo(
        HtlTemplateFileResource.class);
    templateFile3 = context.create().resource("/file-3", properties).adaptTo(
        HtlTemplateFileResource.class);
    context.create().resource("/file-1/jcr:content", templateFileJcrContentProperties1);
    context.create().resource("/file-2/jcr:content", templateFileJcrContentProperties2);
    context.create().resource("/file-3/jcr:content", templateFileJcrContentProperties3);

    templateFileList.add(templateFile1);
    templateFileList.add(templateFile2);
    templateFileList.add(templateFile3);

    assertEquals("<template data-sly-template.testTemplateOne=\"${ @ text}\"></template>\n"
                 + "<template data-sly-template.testTemplateTwo=\"${ @ text}\"></template>\n"
                 + "<template data-sly-template.testTemplateThree=\"${ @ text}\"></template>\n",
        htlTemplateCompilationService.getCompiledHtlTemplateFileOutput(templateFileList));
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("HTL Template Compilation Service", htlTemplateCompilationService.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    htlTemplateCompilationService.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    htlTemplateCompilationService.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = mock(FormattingResultLog.class);
    htlTemplateCompilationService.runAdditionalHealthChecks(log);
    verifyZeroInteractions(log);
  }

}