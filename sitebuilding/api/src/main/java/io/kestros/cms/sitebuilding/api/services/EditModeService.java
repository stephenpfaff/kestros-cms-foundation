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

package io.kestros.cms.sitebuilding.api.services;

import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Provides edit mode activation status, properties and {@link Theme}.
 */
public interface EditModeService extends ManagedService {

  /**
   * Whether the current request should render the page in Edit Mode. Looks to the editMode
   * parameter, I.E '/content/page.html?editMode=true'.
   *
   * @return Whether the current request should render the page in Edit Mode.
   */
  boolean isEditModeActive();

  /**
   * Current edit mode {@link Theme}.
   *
   * @param request Request to determine the edit mode status of.
   * @return Current edit mode Theme.
   * @throws InvalidThemeException Edit mode them was not found, or could not be adapted to
   *     {@link Theme}
   */
  Theme getEditModeTheme(SlingHttpServletRequest request) throws InvalidThemeException;

}
