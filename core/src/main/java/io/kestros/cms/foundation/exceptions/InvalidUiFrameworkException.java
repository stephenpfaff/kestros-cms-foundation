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

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;

/**
 * Exception throw when a UiFramework is invalid and cannot be retrieved.
 */
public class InvalidUiFrameworkException extends InvalidResourceTypeException {

  protected InvalidUiFrameworkException(final String message) {
    super(message);
  }

  /**
   * Exception throw when a UiFramework is invalid and cannot be retrieved.
   *
   * @param uiFrameworkPath Expected UiFramework path that could not be retrieved.
   * @param message Cause message.
   */
  public InvalidUiFrameworkException(final String uiFrameworkPath, final String message) {
    super(String.format("Unable to retrieve UiFramework '%s'. %s", uiFrameworkPath, message));
  }

  /**
   * Exception throw when a UiFramework is invalid and cannot be retrieved from a Theme.
   *
   * @param theme Theme that could not find a ancestor UiFramework.
   * @param message Cause message.
   */
  public InvalidUiFrameworkException(final Theme theme, final String message) {
    super(String.format("Unable to retrieve parent UiFramework for theme '%s'. %s", theme.getPath(),
        message));
  }
}
