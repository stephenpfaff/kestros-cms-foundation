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

import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Service which providers {@link ComponentUiFrameworkView} instances for a given {@link
 * UiFramework}.
 */
public interface ComponentUiFrameworkViewService extends ManagedService {

  /**
   * Retrieves all {@link ComponentUiFrameworkView} instances for a given {@link UiFramework}.
   *
   * @param uiFramework UiFramework to retrieve views for.
   * @return All {@link ComponentUiFrameworkView} instances for a given {@link UiFramework}.
   */
  List<ComponentUiFrameworkView> getComponentViews(UiFramework uiFramework);


}
