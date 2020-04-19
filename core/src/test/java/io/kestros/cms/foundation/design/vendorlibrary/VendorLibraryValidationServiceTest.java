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

package io.kestros.cms.foundation.design.vendorlibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class VendorLibraryValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private VendorLibraryValidationService vendorLibraryValidationService;

  private VendorLibrary vendorLibrary;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    vendorLibraryValidationService = spy(new VendorLibraryValidationService());
  }

  @Test
  public void testGetModel() {
    doReturn(vendorLibrary).when(vendorLibraryValidationService).getGenericModel();
    assertEquals(vendorLibrary, vendorLibraryValidationService.getModel());
  }

  @Test
  public void testRegisterBasicValidators() {
    vendorLibraryValidationService.registerBasicValidators();
    assertEquals(6, vendorLibraryValidationService.getBasicValidators().size());
  }

  @Test
  public void testHasDocumentationUrl() {
    properties.put("documentationUrl", "documentation");
    resource = context.create().resource("/vendor-library", properties);
    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    doReturn(vendorLibrary).when(vendorLibraryValidationService).getGenericModel();

    vendorLibraryValidationService.registerBasicValidators();
    assertTrue(vendorLibraryValidationService.hasDocumentationUrl().isValid());
  }

  @Test
  public void testHasDocumentationUrlWhenInvalid() {
    resource = context.create().resource("/vendor-library", properties);
    vendorLibrary = resource.adaptTo(VendorLibrary.class);

    doReturn(vendorLibrary).when(vendorLibraryValidationService).getGenericModel();

    vendorLibraryValidationService.registerBasicValidators();
    assertFalse(vendorLibraryValidationService.hasDocumentationUrl().isValid());
    assertEquals("Has documentation URL.",
        vendorLibraryValidationService.hasDocumentationUrl().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        vendorLibraryValidationService.hasDocumentationUrl().getType());
  }
}