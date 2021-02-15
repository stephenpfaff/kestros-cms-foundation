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

import io.kestros.cms.componenttypes.api.exceptions.ComponentTypeRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Retrieves {@link ComponentType} objects.
 */
public interface ComponentTypeRetrievalService extends ManagedService {

  /**
   * Retrieves a ComponentType at a specified path.
   *
   * @param path Resource path.
   * @return ComponentType.
   * @throws ComponentTypeRetrievalException Failed to retrieve the componentType.
   */
  @Nonnull
  ComponentType getComponentType(@Nonnull String path) throws ComponentTypeRetrievalException;

  /**
   * Retrieves a ComponentType for a specified view.
   *
   * @param componentUiFrameworkView view.
   * @return ComponentType for a specified view.
   * @throws ComponentTypeRetrievalException Failed to retrieve a ComponentType.
   */
  @Nonnull
  ComponentType getComponentType(@Nonnull ComponentUiFrameworkView componentUiFrameworkView)
      throws ComponentTypeRetrievalException;

  /**
   * Retrieves all component types.
   *
   * @param includeApps Include /apps.
   * @param includeLibsCommons Include /libs/kestros/commons.
   * @param includeAllLibs Include /libs.
   * @return All component types.
   */
  @Nonnull
  List<ComponentType> getAllComponentTypes(Boolean includeApps, Boolean includeLibsCommons,
      Boolean includeAllLibs);

  /**
   * Retrieves all ComponentTypes within a given root path.
   *
   * @param rootPath Root path.
   * @return All ComponentTypes within a given root path.
   */
  @Nonnull
  List<ComponentType> getAllComponentTypesInDirectory(String rootPath);
}