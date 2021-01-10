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

package io.kestros.cms.uiframeworks.api.exceptions;

import static org.junit.Assert.assertEquals;

import io.kestros.cms.uiframeworks.api.models.Theme;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class InvalidUiFrameworkExceptionTest {

  @Rule
  public SlingContext context = new SlingContext();

  private InvalidUiFrameworkException exception;
  private Theme theme;

  @Before
  public void setUp() throws Exception {
    theme = context.create().resource("/theme").adaptTo(Theme.class);
  }

  @Test
  public void testWhenMessage() {
    exception = new InvalidUiFrameworkException("message");
    assertEquals("message", exception.getMessage());
  }

  @Test
  public void testWhenPathAndMessage() {
    exception = new InvalidUiFrameworkException("/path", "message");
    assertEquals("Unable to retrieve UiFramework '/path'. message",
        exception.getMessage());
  }

  @Test
  public void testWhenThemeAndMessage() {
    exception = new InvalidUiFrameworkException(theme, "message");
    assertEquals("Unable to retrieve parent UiFramework for theme '/theme'. message", exception.getMessage());
  }
}