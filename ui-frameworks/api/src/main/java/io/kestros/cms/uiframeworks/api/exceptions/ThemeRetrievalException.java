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

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Thrown when a Theme could not be retrieved.
 */
public class ThemeRetrievalException extends ModelAdaptionException {

  /**
   * Thrown when a Theme could not be retrieved.
   * @param themePath Theme path.
   */
  public ThemeRetrievalException(String themePath) {
    super(String.format("Failed to retrieve themes %s. Resource was not found, or was an invalid "
                        + "resourceType.", themePath));
  }

  /**
   * Thrown when a Theme could not be retrieved.
   * @param themeName Theme name.
   * @param uiFramework Parent framework.
   */
  public ThemeRetrievalException(String themeName, UiFramework uiFramework) {
    super(String.format(
        "Failed to retrieve theme %s for UiFramework %s. Resource was not found, or was an invalid "
        + "resourceType.", themeName, uiFramework.getPath()));
  }

  /**
   * Thrown when a Theme could not be retrieved.
   * @param uiFramework UiFramework.
   */
  public ThemeRetrievalException(UiFramework uiFramework) {
    super(String.format(
        "Failed to retrieve themes for UiFramework %s. Resource was not found, or was an invalid "
        + "resourceType.", uiFramework.getPath()));
  }
}
