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

package io.kestros.cms.componenttypes.api.exceptions;

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Thrown when a ComponentType failed to be retrieved.
 */
public class ComponentTypeRetrievalException extends ModelAdaptionException {

  /**
   * Thrown when a ComponentType failed to be retrieved.
   *
   * @param resourcePath Resource path.
   * @param message Message.
   */
  public ComponentTypeRetrievalException(String resourcePath, String message) {
    super(resourcePath, message);
  }
}
