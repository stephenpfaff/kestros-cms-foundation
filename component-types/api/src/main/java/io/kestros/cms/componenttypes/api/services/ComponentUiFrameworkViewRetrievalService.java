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
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
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
   * @throws ChildResourceNotFoundException child resource not found.
   * @throws InvalidComponentTypeException Specified componentType was invalid.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewFromStandaloneUiFramework(
      @Nonnull ComponentType componentType, @Nonnull final UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, ChildResourceNotFoundException;

  /**
   * Retrieves a VendorLibrary's view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param vendorLibrary VendorLibrary.
   * @return A UiFramework's view from a ComponentType.
   * @throws InvalidComponentUiFrameworkViewException ComponentUiFrameworkView failed to be
   *     retrieved.
   * @throws ResourceNotFoundException Resource not found.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewFromStandaloneVendorLibrary(
      @Nonnull ComponentType componentType, @Nonnull final VendorLibrary vendorLibrary)
      throws InvalidComponentUiFrameworkViewException, ResourceNotFoundException;

  /**
   * Retrieves the component view for a given UiFramework, and falls back to previous versions or
   * VendorLibraries if needed. If no views are found, the common view will be used.
   *
   * @param componentType ComponentType
   * @param uiFramework UiFramework
   * @return Component view for a given UiFramework, and falls back to previous versions or
   *     VendorLibraries if needed. If no views are found, the common view will be used.
   * @throws InvalidComponentUiFrameworkViewException InvalidComponentUiFrameworkViewException
   * @throws InvalidComponentTypeException InvalidComponentTypeException
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewWithFallback(
      @Nonnull ComponentType componentType, @Nonnull final UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException;

  /**
   * Retrieves the component view for a given UiFramework, and falls back to previous versions if
   * needed.
   *
   * @param componentType ComponentType
   * @param managedUiFramework ManagedUiFramework
   * @param maxVersion Max allowed version.
   * @return Component view for a given UiFramework, and falls back to previous versions if needed.
   * @throws InvalidComponentUiFrameworkViewException InvalidComponentUiFrameworkViewException.
   * @throws InvalidComponentTypeException InvalidComponentTypeException.
   * @throws ChildResourceNotFoundException ChildResourceNotFoundException.
   * @throws VersionRetrievalException VersionRetrievalException.
   * @throws InvalidResourceTypeException InvalidResourceTypeException.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewFromManagedUiFramework(
      @Nonnull ComponentType componentType, @Nonnull final ManagedUiFramework managedUiFramework,
      @Nonnull final Version maxVersion)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ChildResourceNotFoundException, VersionRetrievalException,
             InvalidResourceTypeException;

  /**
   * Retrieves the component view for a given VendorLibrary, and falls back to previous versions if
   * needed.
   *
   * @param componentType ComponentType
   * @param managedVendorLibrary ManagedVendorLibrary
   * @param maxVersion Max allowed version.
   * @return Component view for a given UiFramework, and falls back to previous versions if needed.
   * @throws InvalidComponentUiFrameworkViewException InvalidComponentUiFrameworkViewException.
   * @throws InvalidComponentTypeException InvalidComponentTypeException.
   * @throws ChildResourceNotFoundException ChildResourceNotFoundException.
   * @throws VersionRetrievalException VersionRetrievalException.
   * @throws InvalidResourceTypeException InvalidResourceTypeException.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewFromManagedVendorLibrary(
      @Nonnull final ComponentType componentType,
      @Nonnull final ManagedVendorLibrary managedVendorLibrary, @Nonnull final Version maxVersion)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ChildResourceNotFoundException, InvalidResourceTypeException;

  /**
   * Retrieves a VendorLibrary's view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param uiFramework UiFramework.
   * @return A VendorLibrary's view from a ComponentType.
   * @throws ResourceNotFoundException ResourceNotFoundException.
   */
  @Nonnull
  ComponentUiFrameworkView getComponentUiFrameworkViewFromVendorLibraryList(
      @Nonnull ComponentType componentType, @Nonnull final UiFramework uiFramework)
      throws ResourceNotFoundException;


  /**
   * Retrieves a UiFramework's managed view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param managedUiFramework UiFramework.
   * @return A UiFramework's managed view from a ComponentType.
   * @throws ChildResourceNotFoundException ComponentUiFrameworkView failed to be retrieved.
   * @throws InvalidResourceTypeException Specified componentType was invalid.
   */
  @Nonnull
  ManagedComponentUiFrameworkView getManagedComponentUiFrameworkViewFromManagedUiFramework(
      @Nonnull ComponentType componentType, @Nonnull ManagedUiFramework managedUiFramework)
      throws ChildResourceNotFoundException, InvalidResourceTypeException;

  /**
   * Retrieves a VendorLibrary's managed view from a ComponentType.
   *
   * @param componentType ComponentType.
   * @param managedVendorLibrary VendorLibrary.
   * @return A UiFramework's managed view from a ComponentType.
   * @throws ResourceNotFoundException ResourceNotFoundException
   * @throws ChildResourceNotFoundException ComponentUiFrameworkView failed to be retrieved.
   * @throws InvalidResourceTypeException Specified componentType was invalid.
   */
  @Nonnull
  ManagedComponentUiFrameworkView getManagedComponentUiFrameworkViewFromManagedVendorLibrary(
      @Nonnull ComponentType componentType, @Nonnull ManagedVendorLibrary managedVendorLibrary)
      throws ChildResourceNotFoundException, InvalidResourceTypeException,
             ResourceNotFoundException;


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
   * Retrieves all {@link ComponentUiFrameworkView} instances for a given {@link VendorLibrary}.
   *
   * @param vendorLibrary VendorLibrary to retrieve views for.
   * @param includeApps Include views from /apps
   * @param includeLibsCommons Include views from /libs/kestros/commons
   * @param includeAllLibs Include views from /libs
   * @return All {@link ComponentUiFrameworkView} instances for a given {@link UiFramework}.
   */
  @Nonnull
  List<ComponentUiFrameworkView> getComponentViews(VendorLibrary vendorLibrary, Boolean includeApps,
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