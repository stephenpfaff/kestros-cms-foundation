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

import java.util.List;

/**
 * Titled list of ComponentTypes with the same 'componentGroup' property.
 */
public interface ComponentTypeGroupModel {

  /**
   * Title.
   *
   * @return Title.
   */
  String getTitle();

  /**
   * All componentTypes within the group.
   *
   * @return All componentTypes within the group.
   */
  List<ComponentType> getComponentTypes();

  /**
   * Add a componentType to the group.
   *
   * @param componentType ComponentType to add to the group.
   */
  void addComponentType(ComponentType componentType);

}
