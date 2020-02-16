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

package io.kestros.cms.foundation.exceptions;

import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when an expected Theme is invalid or missing.
 */
public class InvalidThemeException extends ModelAdaptionException {

  private static final long serialVersionUID = -1786191060331649630L;

  protected InvalidThemeException(final String message) {
    super(message);
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param uiFramework UiFramework the expected Theme should belong to.
   * @param themeName Expected Theme name.
   * @param message Cause message.
   */
  public InvalidThemeException(final UiFramework uiFramework, final String themeName, final String message) {
    this(uiFramework.getPath(), themeName, message);
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param uiFrameworkPath Path of UiFramework the expected Theme should belong to.
   * @param themeName Expected Theme name.
   * @param message Cause message.
   */
  public InvalidThemeException(final String uiFrameworkPath, final String themeName, final String message) {
    super(String.format("Unable to retrieve theme '%s' under UiFramework '%s'. %s", themeName,
        uiFrameworkPath, message));
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param themePath Absolute path to expected Theme.
   * @param message Cause message.
   */
  public InvalidThemeException(final String themePath, final String message) {
    super(String.format("Unable to retrieve theme '%s'. %s", themePath, message));
  }
}
