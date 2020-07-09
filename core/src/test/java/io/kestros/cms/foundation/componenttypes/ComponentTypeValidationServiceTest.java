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

package io.kestros.cms.foundation.componenttypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ComponentTypeValidationServiceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private Resource resource;
  private Resource etcUiFrameworksResource;

  private ComponentTypeValidationService validationService;

  private ComponentType componentType;

  private Map<String, Object> properties = new HashMap<>();
  private Map<String, Object> frameworkProperties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");

    validationService = new ComponentTypeValidationService();
    validationService = spy(validationService);

    properties.put("jcr:primaryType", "kes:ComponentType");
    context.create().resource("/libs/kestros/commons/components/kestros-parent", properties);

    etcUiFrameworksResource = context.create().resource("/etc/ui-frameworks");
    context.create().resource("/libs/kestros/ui-frameworks");
    frameworkProperties.put("jcr:primaryType", "kes:UiFramework");
    frameworkProperties.put("kes:uiFrameworkCode", "framework-1");
    context.create().resource("/etc/ui-frameworks/ui-framework-1", frameworkProperties);
    frameworkProperties.put("kes:uiFrameworkCode", "framework-2");
    context.create().resource("/etc/ui-frameworks/ui-framework-2", frameworkProperties);
  }

  @Test
  public void testRegisterBasicValidators() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    validationService.registerBasicValidators();
    assertEquals(9, validationService.getBasicValidators().size());
  }

  @Test
  public void testRegisterDetailedValidators() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    validationService.registerDetailedValidators();
    assertEquals(0, validationService.getDetailedValidators().size());
  }

  @Test
  public void testHasComponentGroup() {
    properties.put("componentGroup", "group");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.hasComponentGroup().isValid());
  }

  @Test
  public void testHasComponentGroupWhenMissingGroup() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.hasComponentGroup().isValid());
    assertEquals("Component Group must be configured.",
        validationService.hasComponentGroup().getMessage());
    assertEquals(ModelValidationMessageType.ERROR, validationService.hasComponentGroup().getType());
  }

  @Test
  public void testDoesSuperTypeKestrosParentComponent() {
    properties.put("sling:resourceSuperType", "kestros/commons/components/kestros-parent");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.doesSuperTypeKestrosParentComponent().isValid());
  }

  @Test
  public void testDoesSuperTypeKestrosParentComponentWhenNestedSuperType() {
    properties.put("sling:resourceSuperType", "kestros/commons/components/kestros-parent");
    context.create().resource("/component-type-parent", properties);

    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.doesSuperTypeKestrosParentComponent().isValid());
  }

  @Test
  public void testDoesSuperTypeKestrosParentComponentWhenDoesNotInheritKestrosParentSuperType() {
    context.create().resource("/component-type-parent", properties);

    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.doesSuperTypeKestrosParentComponent().isValid());
    assertEquals("SuperTypes Kestros Parent Component.",
        validationService.doesSuperTypeKestrosParentComponent().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.doesSuperTypeKestrosParentComponent().getType());
  }

  @Test
  public void testIsValidAcrossAllUiFrameworksOrBypassUiFrameworkValidationWhenHasFrameworkViews() {
    context.create().resource("/component-type-parent", properties);

    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    context.create().resource("/component-type/framework-1", properties);
    context.create().resource("/component-type/framework-2", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().isValid());
  }

  @Test
  public void testIsValidAcrossAllUiFrameworksOrBypassUiFrameworkValidationWhenBypassesFramework() {
    properties.put("bypassUiFrameworks", true);

    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().isValid());
  }

  @Test
  public void testIsValidAcrossAllUiFrameworksOrBypassUiFrameworkValidationWhenHasCommonFrameworkView() {
    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    context.create().resource("/component-type/common", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().isValid());
  }

  @Test
  public void testIsValidAcrossAllUiFrameworksOrBypassUiFrameworkValidationWhenMissingViews() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertEquals(3,
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().getValidators().size());
    assertFalse(
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().isValid());
    assertEquals("Must be implemented across all UI Frameworks, or set to bypass UI Frameworks.",
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().getBundleMessage());
    assertEquals("One of the following is true:",
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation().getType());
  }

  @Test
  public void testIsBypassUiFrameworks() {
    properties.put("bypassUiFrameworks", true);
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.isBypassUiFrameworks().isValid());
    assertEquals("Bypasses UiFrameworks Validation checks.",
        validationService.isBypassUiFrameworks().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.isBypassUiFrameworks().getType());
  }

  @Test
  public void testIsBypassUiFrameworksWhenFalse() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.isBypassUiFrameworks().isValid());
    assertEquals("Bypasses UiFrameworks Validation checks.",
        validationService.isBypassUiFrameworks().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.isBypassUiFrameworks().getType());
  }


  @Test
  public void testHasCommonUiFrameworkView() {
    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    context.create().resource("/component-type/common", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.hasCommonUiFrameworkView().isValid());
  }

  @Test
  public void testHasCommonUiFrameworkViewWhenFalse() {
    properties.put("sling:resourceSuperType", "/component-type-parent");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.hasCommonUiFrameworkView().isValid());
    assertEquals("Has Common UiFramework view.",
        validationService.hasCommonUiFrameworkView().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.hasCommonUiFrameworkView().getType());
  }

  @Test
  public void testHasUiFrameworkViews() {
    resource = context.create().resource("/component-type", properties);
    context.create().resource("/component-type/framework-1", properties);
    context.create().resource("/component-type/framework-2", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertEquals(2, validationService.hasUiFrameworkViews().getValidators().size());
    assertTrue(validationService.hasUiFrameworkViews().getValidators().get(0).isValid());
    assertEquals("UiFramework view framework-1 must be configured.",
        validationService.hasUiFrameworkViews().getValidators().get(0).getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.hasUiFrameworkViews().getValidators().get(0).getType());

    assertEquals("UiFramework view framework-2 must be configured.",
        validationService.hasUiFrameworkViews().getValidators().get(1).getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.hasUiFrameworkViews().getValidators().get(1).getType());

    assertTrue(validationService.hasUiFrameworkViews().getValidators().get(1).isValid());

    assertTrue(validationService.hasUiFrameworkViews().isValid());
    assertEquals("All of the following are true:",
        validationService.hasUiFrameworkViews().getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.hasUiFrameworkViews().getType());
  }

  @Test
  public void testHasUiFrameworkViewsWhenMissingViews() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertEquals(2, validationService.hasUiFrameworkViews().getValidators().size());
    assertFalse(validationService.hasUiFrameworkViews().getValidators().get(0).isValid());
    assertFalse(validationService.hasUiFrameworkViews().getValidators().get(1).isValid());
    assertFalse(validationService.hasUiFrameworkViews().isValid());
    assertEquals("All of the following are true:",
        validationService.hasUiFrameworkViews().getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.hasUiFrameworkViews().getType());
  }

  @Test
  public void testDoesNotSuperTypeItself() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.doesNotSuperTypeItself().isValid());
  }

  @Test
  public void testDoesNotSuperTypeItselfWhenComponentSuperTypesItself() {
    properties.put("sling:resourceSuperType", "/component-type");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.doesNotSuperTypeItself().isValid());
    assertEquals("Does not SuperType itself.",
        validationService.doesNotSuperTypeItself().getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.doesNotSuperTypeItself().getType());
  }

  @Test
  public void testHasFontAwesomeIcon() {
    properties.put("fontAwesomeIcon", "icon");
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertTrue(validationService.hasFontAwesomeIcon().isValid());
  }

  @Test
  public void testHasFontAwesomeIconWhenNotConfigured() {
    resource = context.create().resource("/component-type", properties);
    componentType = resource.adaptTo(ComponentType.class);

    when(validationService.getModel()).thenReturn(componentType);

    assertFalse(validationService.hasFontAwesomeIcon().isValid());
    assertEquals("Has FontAwesome icon.", validationService.hasFontAwesomeIcon().getMessage());
    assertEquals(ModelValidationMessageType.WARNING,
        validationService.hasFontAwesomeIcon().getType());
  }
}