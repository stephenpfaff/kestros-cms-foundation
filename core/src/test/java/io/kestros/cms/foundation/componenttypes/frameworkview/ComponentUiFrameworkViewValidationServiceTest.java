package io.kestros.cms.foundation.componenttypes.frameworkview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import org.junit.Before;
import org.junit.Test;

public class ComponentUiFrameworkViewValidationServiceTest {

  private ComponentUiFrameworkViewValidationService validationService;

  private ComponentUiFrameworkView componentUiFrameworkView;

  @Before
  public void setUp() throws Exception {
    validationService = new ComponentUiFrameworkViewValidationService();
    validationService = spy(validationService);

    componentUiFrameworkView = mock(ComponentUiFrameworkView.class);

    when(validationService.getModel()).thenReturn(componentUiFrameworkView);
  }

  @Test
  public void testRegisterBasicValidators() {
    validationService.registerBasicValidators();
    assertEquals(6, validationService.getBasicValidators().size());
  }

  @Test
  public void testRegisterDetailedValidators() {
    validationService.registerDetailedValidators();
    assertEquals(0, validationService.getDetailedValidators().size());
  }

  @Test
  public void testHasValidContentScript() {
    assertTrue(validationService.hasValidContentScript().isValid());
  }

  @Test
  public void testHasValidContentScriptWhenScriptNotFound() throws InvalidScriptException {
    when(componentUiFrameworkView.getUiFrameworkViewScript("content.html")).thenThrow(
        InvalidScriptException.class);

    assertFalse(validationService.hasValidContentScript().isValid());
    assertEquals("Must have content.html script.",
        validationService.hasValidContentScript().getMessage());
    assertEquals(ModelValidationMessageType.ERROR,
        validationService.hasValidContentScript().getType());
  }

  @Test
  public void testHasValidContentScriptWhenInvalidScript() throws InvalidScriptException {
    when(componentUiFrameworkView.getUiFrameworkViewScript("content.html")).thenThrow(
        InvalidScriptException.class);

    assertFalse(validationService.hasValidContentScript().isValid());
  }

}