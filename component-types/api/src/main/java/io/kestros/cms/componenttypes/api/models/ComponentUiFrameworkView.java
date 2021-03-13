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

package io.kestros.cms.componenttypes.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;

/**
 * Component view that is specific to a single UiFramework.  Created as a child resource to
 * ComponentTypes.
 */
public interface ComponentUiFrameworkView extends VersionResource, IconResource, FrontendLibrary {

  /**
   * Component view path.
   *
   * @return Component view path.
   */
  String getPath();

  /**
   * Component view name.
   *
   * @return Component view name.
   */
  String getName();

  /**
   * Path to compiled HTL templates file.
   *
   * @return Path to compiled HTL templates file.
   */
  String getTemplatesPath();

  /**
   * UiFramework associated to the current view.
   *
   * @return UiFramework associated to the current view.
   * @throws ResourceNotFoundException No UiFramework found for the specified framework code.
   */
  @JsonIgnore
  UiFramework getUiFramework() throws ResourceNotFoundException;

}