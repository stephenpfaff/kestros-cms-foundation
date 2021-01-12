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
 * Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
 * missing.
 */
public class InvalidComponentTypeException extends ModelAdaptionException {

  private static final long serialVersionUID = -4673833087114663599L;

  /**
   * Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
   * missing.
   *
   * @param componentPath component that the ComponentType was requested from.
   * @param componentTypePath Expected path of ComponentType that could not be retrieved.
   */
  public InvalidComponentTypeException(final String componentPath, final String componentTypePath) {
    this(componentPath, componentTypePath, "Invalid or missing ComponentType resource.");
  }

  /**
   * Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
   * missing.
   *
   * @param componentPath component that the ComponentType was requested from.
   * @param componentTypePath Expected path of ComponentType that could not be retrieved.
   * @param message Cause message.
   */
  public InvalidComponentTypeException(final String componentPath, final String componentTypePath,
      final String message) {
    super(String.format("Unable to adapt '%s' to ComponentType for resource %s. %s",
        componentTypePath, componentPath, message));
  }
}
