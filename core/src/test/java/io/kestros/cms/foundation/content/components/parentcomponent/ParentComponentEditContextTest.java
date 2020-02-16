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

package io.kestros.cms.foundation.content.components.parentcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.editmodeservice.EditModeService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ParentComponentEditContextTest {

  @Rule
  public SlingContext context = new SlingContext();

  private ParentComponentEditContext parentComponentEditContext;

  private EditModeService editModeService;

  private Theme editModeTheme;

  private Resource resource;

  private Map<String, Object> uiFrameworkProperties = new HashMap<>();

  private Exception exception = null;

  @Before
  public void setUp() throws Exception {
    context.addModelsForPackage("io.kestros");
    uiFrameworkProperties.put("jcr:primaryType", "kes:UiFramework");

    editModeService = mock(EditModeService.class);
    context.registerService(EditModeService.class, editModeService);

    editModeTheme = mock(Theme.class);
  }

  @Test
  public void testIsEditMode() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenFalse() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", false);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringTrue() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", "true");
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertTrue(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenStringFalse() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", "false");
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }


  @Test
  public void testIsEditModeWhenAttributeNotFound() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenNull() {
    when(editModeService.isEditModeActive()).thenReturn(true);
    context.request().setAttribute("editMode", null);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }


  @Test
  public void testIsEditModeWhenEditModeIsNotActive() {
    when(editModeService.isEditModeActive()).thenReturn(false);
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testIsEditModeWhenEditModeServiceIsNull() {
    editModeService = null;
    context.request().setAttribute("editMode", true);
    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertFalse(parentComponentEditContext.isEditMode());
  }

  @Test
  public void testGetEditTheme() throws InvalidThemeException {
    when(editModeService.isEditModeActive()).thenReturn(true);
    when(editModeService.getEditModeTheme(any())).thenReturn(editModeTheme);

    when(editModeTheme.getPath()).thenReturn("/edit-mode-theme");

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertEquals("/edit-mode-theme", parentComponentEditContext.getEditTheme().getPath());
  }

  @Test
  public void testGetEditThemeWhenEditModeIsNotActive() throws InvalidThemeException {
    when(editModeService.isEditModeActive()).thenReturn(false);

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertNull(parentComponentEditContext.getEditTheme());
  }

  @Test
  public void testGetEditThemeWhenEditModeServiceIsNull() throws InvalidThemeException {
    editModeService = null;

    parentComponentEditContext = context.request().adaptTo(ParentComponentEditContext.class);
    assertNull(parentComponentEditContext.getEditTheme());
  }

}