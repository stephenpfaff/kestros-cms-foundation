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

import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.ManagedUiFramework;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;

/**
 * Retrieves {@link UiFramework} instances.
 */
public interface UiFrameworkRetrievalService extends ManagedService {


  /**
   * Retrieves all ManagedUiFrameworks.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All ManagedUiFrameworks.
   */
  List<ManagedUiFramework> getAllManagedUiFrameworks(Boolean includeEtc, Boolean includeLibs);

  /**
   * Retrieves all unmanaged UiFrameworks.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All UiFrameworks.
   */
  List<UiFramework> getAllUnmanagedUiFrameworks(Boolean includeEtc, Boolean includeLibs);

  /**
   * Retrieves all managed and unmanaged UiFrameworks.
   *
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @return All ManagedUiFrameworks and UiFrameworks.
   */
  List<UiFramework> getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(Boolean includeEtc,
      Boolean includeLibs);

  /**
   * Retrieves a UiFramework for a given Theme.
   *
   * @param theme Theme.
   * @return UiFramework for a given Theme.
   * @throws UiFrameworkRetrievalException Failed to retrieve a UiFramework.
   */
  UiFramework getUiFramework(Theme theme) throws UiFrameworkRetrievalException;

  /**
   * Retrieves a given UiFramework.
   *
   * @param path UiFramework path.
   * @return UiFramework.
   * @throws UiFrameworkRetrievalException Failed to retrieve a UiFramework.
   */
  UiFramework getUiFramework(String path) throws UiFrameworkRetrievalException;

  /**
   * Retrieves a specified UiFramework by the UiFramework code.
   *
   * @param code UiFramework code.
   * @param includeEtc Whether to search in /etc.
   * @param includeLibs Whether to search in /libs.
   * @param version Closest matching version to find.
   * @return All ManagedUiFrameworks.
   * @throws UiFrameworkRetrievalException Failed to retrieve a UiFramework.
   */
  UiFramework getUiFrameworkByCode(String code, Boolean includeEtc, Boolean includeLibs,
      String version) throws UiFrameworkRetrievalException;

  /**
   * Retrieves a given ManagedUiFramework.
   *
   * @param path ManagedUiFramework path.
   * @return ManagedUiFramework.
   * @throws UiFrameworkRetrievalException Failed to retrieve a ManagedUiFramework.
   */
  ManagedUiFramework getManagedUiFramework(String path) throws UiFrameworkRetrievalException;
}
