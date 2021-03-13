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

package io.kestros.cms.uiframeworks.api.services;

import io.kestros.cms.uiframeworks.api.exceptions.VendorLibraryRetrievalException;
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Retrieves VendorLibrary and ManagedVendorLibrary objects.
 */
public interface VendorLibraryRetrievalService extends ManagedService {

  /**
   * Retrieves all managed and unmanaged UiFrameworks.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All ManagedUiFrameworks and UiFrameworks.
   */
  List<VendorLibrary> getAllUnmanagedUiFrameworksAndManagedVendorLibraryVersions(Boolean includeEtc,
      Boolean includeLibs);

  /**
   * Retrieves a specified ManagedVendorLibrary.
   *
   * @param name Library name.
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return A specified ManagedVendorLibrary.
   * @throws VendorLibraryRetrievalException Failed to find the specified ManagedVendorLibrary.
   */
  ManagedVendorLibrary getManagedVendorLibrary(String name, boolean includeEtc, boolean includeLibs)
      throws VendorLibraryRetrievalException;

  /**
   * Retrieves a specified VendorLibrary.
   *
   * @param name Library name.
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return A specified VendorLibrary.
   * @throws VendorLibraryRetrievalException Failed to find the specified ManagedVendorLibrary.
   * @throws VersionRetrievalException Failed to find the specified VendorLibrary version.
   */
  VendorLibrary getVendorLibrary(String name, boolean includeEtc, boolean includeLibs)
      throws VendorLibraryRetrievalException, VersionRetrievalException;

  /**
   * Retrieves a specified VendorLibrary.
   *
   * @param path Library path.
   * @return A specified VendorLibrary.
   * @throws VendorLibraryRetrievalException Failed to find the specified ManagedVendorLibrary.
   * @throws VersionRetrievalException Failed to find the specified VendorLibrary version.
   */
  VendorLibrary getVendorLibrary(String path)
      throws VendorLibraryRetrievalException, VersionRetrievalException;

  /**
   * Retrieves all ManagedVendorLibraries.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All ManagedVendorLibraries.
   */
  List<ManagedVendorLibrary> getAllManagedVendorLibraries(boolean includeEtc, boolean includeLibs);

  /**
   * Retrieves all VendorLibraries.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All VendorLibraries.
   */
  List<VendorLibrary> getAllUnmanagedVendorLibraries(boolean includeEtc, boolean includeLibs);
}