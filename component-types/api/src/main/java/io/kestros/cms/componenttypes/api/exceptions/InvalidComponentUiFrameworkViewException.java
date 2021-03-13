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

import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
 */
public class InvalidComponentUiFrameworkViewException extends ModelAdaptionException {

  private static final long serialVersionUID = 6294137605911848689L;

  /**
   * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
   *
   * @param componentTypePath ComponentType that the expected componentUiFrameworkView could not
   *     be retrieved from.
   * @param managedUiFramework ManagedUiFramework
   * @param version Framework version
   */
  public InvalidComponentUiFrameworkViewException(final String componentTypePath,
      final ManagedUiFramework managedUiFramework, Version version) {
    super(String.format(
        "Unable to retrieve ComponentUiFrameworkView for ComponentType '%s' and UiFramework '%s'.",
        componentTypePath, managedUiFramework.getPath()));
  }

  /**
   * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
   *
   * @param componentTypePath ComponentType that the expected componentUiFrameworkView could not
   *     be retrieved from.
   * @param managedVendorLibrary ManagedVendorLibrary.
   * @param version Library version
   */
  public InvalidComponentUiFrameworkViewException(final String componentTypePath,
      final ManagedVendorLibrary managedVendorLibrary, Version version) {
    super(String.format(
        "Unable to retrieve ComponentUiFrameworkView for ComponentType '%s' and UiFramework '%s'.",
        componentTypePath, managedVendorLibrary.getPath()));
  }

  /**
   * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
   *
   * @param componentTypePath ComponentType that the expected componentUiFrameworkView could not
   *     be retrieved from.
   * @param uiFramework UiFramework
   */
  public InvalidComponentUiFrameworkViewException(final String componentTypePath,
      final UiFramework uiFramework) {
    super(String.format(
        "Unable to retrieve ComponentUiFrameworkView for ComponentType '%s' and UiFramework '%s'.",
        componentTypePath, uiFramework.getPath()));
  }
}
