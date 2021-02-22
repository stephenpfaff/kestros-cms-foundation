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

import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Retrieves lists of allowed UiFrameworks for components.
 */
public interface AllowedUiFrameworkService extends ManagedService {

  /**
   * List of allowed ManagedUiFrameworks. All versions will also be allowed.
   *
   * @param page Page to retrieve alloed Frameworks for.
   * @return List of allowed ManagedUiFrameworks. All versions will also be allowed.
   */
  List<ManagedUiFramework> getAllowedManagedUiFrameworks(BaseContentPage page);

  /**
   * List of allowed UiFrameworks. List will not included versioned frameworks.
   *
   * @param page Page to retrieve alloed Frameworks for.
   * @return List of allowed UiFrameworks. List will not included versioned frameworks.
   */
  List<UiFramework> getAllowedUiFrameworks(BaseContentPage page);

}
