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

package io.kestros.cms.componenttypes.api.services;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.exceptions.ScriptRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import javax.annotation.Nonnull;

/**
 * Retrieves script files for ComponentTypes.
 */
public interface ScriptRetrievalService extends ManagedService {

  /**
   * Retrieves the content.html script for a given ComponentUiFrameworkView.
   *
   * @param componentType ComponentType.
   * @param uiFramework UiFramework.
   * @return The content.html script for a given ComponentUiFrameworkView.
   * @throws InvalidComponentUiFrameworkViewException View was found, but was invalid.
   * @throws InvalidComponentTypeException ComponentType was invalid.
   * @throws ScriptRetrievalException Failure while retrieving scripts from view.
   */
  @Nonnull
  HtmlFile getContentScript(@Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ScriptRetrievalException;

  /**
   * Retrieves a specified script for a given ComponentUiFrameworkView.
   *
   * @param scriptName Script name.
   * @param componentType ComponentType.
   * @param uiFramework UiFramework.
   * @return A specified script for a given ComponentUiFrameworkView.
   * @throws InvalidComponentUiFrameworkViewException View was found, but was invalid.
   * @throws InvalidComponentTypeException ComponentType was invalid.
   * @throws ScriptRetrievalException Failure while retrieving scripts from view.
   */
  @Nonnull
  HtmlFile getScript(@Nonnull String scriptName, @Nonnull ComponentType componentType,
      @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ScriptRetrievalException;

}
