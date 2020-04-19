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

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when the 'common' ComponentUiFrameworkView cannot be retrieved.
 */
public class InvalidCommonUiFrameworkException extends ModelAdaptionException {

  private static final long serialVersionUID = -2417421095024869920L;

  /**
   * Exception thrown when the 'common' ComponentUiFrameworkView cannot be retrieved.
   *
   * @param componentTypePath ComponentType where 'common' could not be retrieved.
   */
  public InvalidCommonUiFrameworkException(final String componentTypePath) {
    super("Unable to retrieve 'common' ComponentUiFrameworkView for '" + componentTypePath + "'.");
  }
}
