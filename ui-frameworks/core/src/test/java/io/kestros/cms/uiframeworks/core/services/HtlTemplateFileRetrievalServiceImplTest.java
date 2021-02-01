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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.refactored.models.VendorLibraryResource;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.component.ComponentContext;

public class HtlTemplateFileRetrievalServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private HtlTemplateFileRetrievalServiceImpl service;

  private VendorLibraryResource vendorLibrary;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> fileProperties = new HashMap<>();
  private Map<String, Object> htmlFileProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    service = new HtlTemplateFileRetrievalServiceImpl();

    fileProperties.put("jcr:primaryType", "nt:file");
    htmlFileProperties.put("jcr:mimeType", "text/html");
  }

  @Test
  public void testGetHtlTemplates() throws HtlTemplateFileRetrievalException {
    resource = context.create().resource("/vendor-library");
    context.create().resource("/vendor-library/templates");
    context.create().resource("/vendor-library/templates/template-1.html", fileProperties);
    context.create().resource("/vendor-library/templates/template-1.html/jcr:content",
        htmlFileProperties);
    context.create().resource("/vendor-library/templates/template-2.html", fileProperties);
    context.create().resource("/vendor-library/templates/template-2.html/jcr:content",
        htmlFileProperties);

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);
    context.registerInjectActivateService(service);

    assertEquals(2, service.getHtlTemplates(vendorLibrary).size());
  }

  @Test
  public void testGetHtlTemplatesWhenChildResourceNotFound() {
    resource = context.create().resource("/vendor-library");

    vendorLibrary = resource.adaptTo(VendorLibraryResource.class);
    context.registerInjectActivateService(service);

    Exception exception = null;
    try {
      assertEquals(3, service.getHtlTemplates(vendorLibrary));
    } catch (HtlTemplateFileRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals(HtlTemplateFileRetrievalException.class, exception.getClass());
    assertEquals("Unable to adapt 'Failed to retrieve HTL Template for VendorLibraryResource "
                 + "/vendor-library. Templates folder not found.': Resource not found.",
        exception.getMessage());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("HTL Template File Retrieval Service", service.getDisplayName());
  }

  @Test
  public void testActivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    service.activate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testDeactivate() {
    ComponentContext componentContext = mock(ComponentContext.class);
    service.deactivate(componentContext);
    verifyZeroInteractions(componentContext);
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = mock(FormattingResultLog.class);
    service.runAdditionalHealthChecks(log);
    verifyZeroInteractions(log);
  }
}