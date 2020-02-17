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

package io.kestros.cms.foundation.design.uiframework;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class UiFrameworkValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private UiFrameworkValidationService validationService;

  private UiFramework uiFramework;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> vendorLibraryProperties = new HashMap<>();
  private Map<String, Object> themeProperties = new HashMap<>();


  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros");

    validationService = new UiFrameworkValidationService();
    validationService = spy(validationService);

    properties.put("jcr:primaryType", "kes:UiFramework");
    vendorLibraryProperties.put("jcr:primaryType", "kes:VendorLibrary");
    themeProperties.put("jcr:primaryType", "kes:Theme");
  }

  @Test
  public void testRegisterBasicValidators() {
    validationService.registerBasicValidators();
    assertEquals(9, validationService.getBasicValidators().size());
  }

  @Test
  public void testHasFrameworkCode() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertTrue(validationService.hasFrameworkCode().isValid());
    assertEquals("UiFramework code is configured.",
        validationService.hasFrameworkCode().getMessage());
    assertEquals(WARNING, validationService.hasFrameworkCode().getType());
  }

  @Test
  public void testHasFrameworkCodeWhenNoCode() {
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.hasFrameworkCode().isValid());
  }

  @Test
  public void testIsAllVendorLibrariesExist() {
    context.create().resource("/etc/vendor-libraries/library-1", vendorLibraryProperties);

    properties.put("kes:vendorLibraries", "library-1");
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertEquals(1, uiFramework.getVendorLibraries().size());
    assertTrue(validationService.isAllVendorLibrariesExist().isValid());

    assertEquals("All included vendor libraries exist and are valid.",
        validationService.isAllVendorLibrariesExist().getMessage());
    assertEquals(ERROR, validationService.isAllVendorLibrariesExist().getType());
  }

  @Test
  public void testIsAllVendorLibrariesExistWhenFalse() {
    context.create().resource("/etc/vendor-libraries/library-1");

    properties.put("kes:vendorLibraries", "library-1");
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertEquals(0, uiFramework.getVendorLibraries().size());
    assertFalse(validationService.isAllVendorLibrariesExist().isValid());
  }

  @Test
  public void testHasValidDefaultTheme() {
    resource = context.create().resource("/ui-framework", properties);

    context.create().resource("/ui-framework/themes/default", themeProperties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertTrue(validationService.hasValidDefaultTheme().isValid());
    assertTrue(validationService.hasDefaultThemeResource().isValid());
    assertTrue(validationService.isDefaultThemeValidResourceType().isValid());

    assertEquals("All of the following are true:",
        validationService.hasValidDefaultTheme().getMessage());
    assertEquals(ERROR, validationService.hasValidDefaultTheme().getType());
  }

  @Test
  public void testHasValidDefaultThemeWhenFalse() {
    resource = context.create().resource("/ui-framework", properties);

    context.create().resource("/ui-framework/themes/default");

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.hasValidDefaultTheme().isValid());
    assertTrue(validationService.hasDefaultThemeResource().isValid());
    assertFalse(validationService.isDefaultThemeValidResourceType().isValid());
  }

  @Test
  public void testHasValidDefaultThemeWhenMissing() {
    resource = context.create().resource("/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);

    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.hasDefaultThemeResource().isValid());
    assertEquals("Has Default Theme resource.",
        validationService.hasDefaultThemeResource().getMessage());
    assertEquals(ERROR, validationService.hasDefaultThemeResource().getType());

    assertFalse(validationService.isDefaultThemeValidResourceType().isValid());
    assertEquals("Default Theme has jcr:primaryType kes:Theme.",
        validationService.isDefaultThemeValidResourceType().getMessage());
    assertEquals(ERROR, validationService.isDefaultThemeValidResourceType().getType());

    assertFalse(validationService.hasValidDefaultTheme().isValid());
    assertEquals("Has valid default Theme.",
        validationService.hasValidDefaultTheme().getBundleMessage());
    assertEquals("All of the following are true:",
        validationService.hasValidDefaultTheme().getMessage());
    assertEquals(ERROR, validationService.hasValidDefaultTheme().getType());
  }

  @Test
  public void testIsFrameworkCodeUnique() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/etc/ui-frameworks/ui-frameworks", properties);

    properties.put("kes:uiFrameworkCode", "code-2");
    context.create().resource("/etc/ui-frameworks/ui-frameworks-2", properties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertTrue(validationService.isFrameworkCodeUnique().isValid());
  }

  @Test
  public void testIsFrameworkCodeUniqueWhenCodeMatchesName() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/etc/ui-frameworks/ui-frameworks", properties);

    properties.put("kes:uiFrameworkCode", "code-2");
    context.create().resource("/etc/ui-frameworks/code", properties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.isFrameworkCodeUnique().isValid());
    assertEquals("Framework code and name are unique.",
        validationService.isFrameworkCodeUnique().getMessage());
    assertEquals(ERROR, validationService.isFrameworkCodeUnique().getType());
  }

  @Test
  public void testIsFrameworkCodeUniqueWhenNameMatchesName() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/ui-framework", properties);

    properties.put("kes:uiFrameworkCode", "code-2");
    context.create().resource("/etc/ui-frameworks/ui-framework", properties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.isFrameworkCodeUnique().isValid());
    assertEquals("Framework code and name are unique.",
        validationService.isFrameworkCodeUnique().getMessage());
    assertEquals(ERROR, validationService.isFrameworkCodeUnique().getType());
  }

  @Test
  public void testIsFrameworkCodeUniqueWhenCodeMatchesCode() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);

    properties.put("kes:uiFrameworkCode", "code");
    context.create().resource("/etc/ui-frameworks/ui-framework-2", properties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.isFrameworkCodeUnique().isValid());
  }

  @Test
  public void testIsFrameworkCodeUniqueWhenNameMatchesCode() {
    properties.put("kes:uiFrameworkCode", "code");
    resource = context.create().resource("/etc/ui-frameworks/ui-framework", properties);

    properties.put("kes:uiFrameworkCode", "ui-framework");
    context.create().resource("/etc/ui-frameworks/ui-framework-2", properties);

    uiFramework = resource.adaptTo(UiFramework.class);
    when(validationService.getGenericModel()).thenReturn(uiFramework);

    assertFalse(validationService.isFrameworkCodeUnique().isValid());
  }

}