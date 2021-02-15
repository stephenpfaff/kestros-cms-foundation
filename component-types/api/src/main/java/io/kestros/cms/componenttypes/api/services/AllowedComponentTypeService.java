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

import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentTypeGroupModel;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Retrieves allowed child ComponentTypes for specified ComponentTypes.
 */
public interface AllowedComponentTypeService extends ManagedService {


  /**
   * List of allowed child ComponentType groups for a given ComponentType.
   *
   * @param componentType ComponentType to retrieve allowed children for.
   * @return List of allowed child ComponentType groups for a given ComponentType.
   */
  List<ComponentTypeGroupModel> getAllowedComponentTypeGroups(ComponentType componentType);

  /**
   * List of allowed child ComponentTypes for a given ComponentType.
   *
   * @param componentType ComponentType to retrieve allowed children for.
   * @return List of allowed child ComponentTypes for a given ComponentType.
   */
  List<ComponentType> getAllowedComponentTypes(ComponentType componentType);

}
