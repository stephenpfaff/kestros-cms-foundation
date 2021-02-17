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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import org.junit.Before;
import org.junit.Test;

public class InvalidThemeExceptionTest {

  private InvalidThemeException exception;
  private UiFramework uiFramework;

  @Before
  public void setUp() throws Exception {
    uiFramework = mock(UiFramework.class);
    when(uiFramework.getPath()).thenReturn("/ui-framework");
  }

  @Test
  public void testWhenPassingUiFramework() {
    exception = new InvalidThemeException(uiFramework, "theme", "message");
    assertEquals("Unable to retrieve theme 'theme' under UiFramework '/ui-framework'. message",
        exception.getMessage());
  }

  @Test
  public void testWhenPassingThemePathFramework() {
    exception = new InvalidThemeException("/theme", "message");
    assertEquals("Unable to retrieve theme '/theme'. message", exception.getMessage());
  }
}