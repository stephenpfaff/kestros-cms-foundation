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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.componenttypes.api.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.models.CommonUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ManagedComponentUiFrameworkView;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Service which providers {@link ComponentUiFrameworkView} instances for a given {@link
 * UiFramework}.
 */
public interface ComponentUiFrameworkViewRetrievalService extends ManagedService {

  /**
   * The common ComponentUiFrameworkView, to be shown when no other valid views are found.
   *
   * @param componentType ComponentType to retrieve view for.
   * @return The common ComponentUiFrameworkView,to be shown when no other valid views are found.
   * @throws InvalidCommonUiFrameworkException Common ComponentUiFramework could not be found,
   *     or failed adaption.
   * @throws InvalidComponentTypeException ComponentType was invalid.
   */
  @Nonnull
  CommonUiFrameworkView getCommonUiFrameworkView(@Nonnull ComponentType componentType)
      throws InvalidCommonUiFrameworkException, InvalidComponentTypeException;

  /**
   * Retrieves a UiFramework's view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param uiFramework UiFramework.
   * @return A UiFramework's view from a ComponentType.
   * @throws InvalidComponentUiFrameworkViewException ComponentUiFrameworkView failed to be
   *     retrieved.
   * @throws InvalidComponentTypeException Specified componentType was invalid.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkView(@Nonnull ComponentType componentType,
      @Nonnull final UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException;

  /**
   * Retrieves a UiFramework's managed view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param uiFramework UiFramework.
   * @return A UiFramework's managed view from a ComponentType.
   * @throws ChildResourceNotFoundException ComponentUiFrameworkView failed to be retrieved.
   * @throws InvalidResourceTypeException Specified componentType was invalid.
   */
  @Nonnull
  ManagedComponentUiFrameworkView getManagedComponentUiFrameworkView(
      @Nonnull ComponentType componentType, @Nonnull UiFramework uiFramework)
      throws ChildResourceNotFoundException, InvalidResourceTypeException;

  /**
   * Retrieves all {@link ComponentUiFrameworkView} instances for a given {@link UiFramework}.
   *
   * @param uiFramework UiFramework to retrieve views for.
   * @param includeApps Include views from /apps
   * @param includeLibsCommons Include views from /libs/kestros/commons
   * @param includeAllLibs Include views from /libs
   * @return All {@link ComponentUiFrameworkView} instances for a given {@link UiFramework}.
   */
  @Nonnull
  List<ComponentUiFrameworkView> getComponentViews(UiFramework uiFramework, Boolean includeApps,
      Boolean includeLibsCommons, Boolean includeAllLibs);


  /**
   * All UI Framework view that belong to the specified ComponentType.
   *
   * @param componentType ComponentType to retrieve view for.
   * @param includeEtcFrameworks Include views from UiFrameworks that live under /etc.
   * @param includeLibsFrameworks Include views from UiFrameworks that live under /libs.
   * @return All UI Framework view that belong to the specified ComponentType.
   */
  @JsonIgnore
  @Nonnull
  List<ComponentUiFrameworkView> getUiFrameworkViews(@Nonnull ComponentType componentType,
      Boolean includeEtcFrameworks, Boolean includeLibsFrameworks);
}