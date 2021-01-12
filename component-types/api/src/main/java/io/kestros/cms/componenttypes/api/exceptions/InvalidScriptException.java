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
 * Exception thrown when sightly scripts are invalid. Potentially caused by missing Resource, or
 * invalid file type.
 */
public class InvalidScriptException extends ModelAdaptionException {

  private static final long serialVersionUID = 6184696106043393068L;

  protected InvalidScriptException(final String message) {
    super(message);
  }

  /**
   * Exception thrown when sightly scripts are invalid. Potentially caused by missing Resource, or *
   * invalid file type.
   *
   * @param scriptName Resource name of script that was invalid.
   * @param uiFrameworkViewPath Path of UiFramework that script was being retrieved from.
   */
  public InvalidScriptException(final String scriptName, final String uiFrameworkViewPath) {
    this(String.format("Unable to adapt '%s' for ComponentUiFrameworkView '%s': %s", scriptName,
        uiFrameworkViewPath, "Script not found."));
  }

}
