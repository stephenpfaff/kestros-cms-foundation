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
    assertEquals(7, validationService.getBasicValidators().size());
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