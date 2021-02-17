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

import io.kestros.cms.modeltypes.IconResource;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;

/**
 * Style variation types for ComponentUiFrameworkViews.
 */
public interface ComponentVariation extends FrontendLibrary, IconResource {

  /**
   * Whether the variation must be included in a componentTypes's content script. When false, the
   * variation's class will be added to a component's wrapper div.
   *
   * @return Whether the variation must be included in a componentTypes's content script. When
   *     false, the variation's class will be added to a component's wrapper div.
   */
  boolean isInlineVariation();

  /**
   * Components without variation properties will be assigned this (and possibly other) variation by
   * default.
   *
   * @return Components without variation properties will be assigned this (and possibly other)
   *     variation by default.
   */
  boolean isDefault();

}
