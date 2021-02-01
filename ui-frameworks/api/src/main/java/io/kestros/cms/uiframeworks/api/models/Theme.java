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

package io.kestros.cms.uiframeworks.api.models;

import io.kestros.cms.modeltypes.IconResource;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;

/**
 * Stylized implementation of a {@link UiFramework}.
 */
public interface Theme extends FrontendLibrary, IconResource {

  /**
   * Retrieves parent UiFramework.
   *
   * @return Parent UiFramework.
   */
  UiFramework getUiFramework();

}
