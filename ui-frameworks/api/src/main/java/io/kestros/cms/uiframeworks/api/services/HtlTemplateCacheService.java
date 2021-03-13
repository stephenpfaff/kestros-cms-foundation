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

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nonnull;

/**
 * Stores and maintains HTL Template caches.
 */
public interface HtlTemplateCacheService extends ManagedCacheService, ManagedService {

  /**
   * Compiled template file path for a given {@link UiFramework}.
   *
   * @param uiFramework UiFramework to lookup HTL Template file path for.
   * @return Compiled template file path for a given {@link UiFramework}.
   * @throws ResourceNotFoundException UiFramework templates root not found.
   */
  String getCompiledTemplateFilePath(UiFramework uiFramework) throws ResourceNotFoundException;

  /**
   * Compiled template file path for a given {@link VendorLibrary}.
   *
   * @param vendorLibrary Vendor Library to lookup HTL Template file path for.
   * @return Compiled template file path for a given {@link UiFramework}.
   * @throws ResourceNotFoundException UiFramework templates root not found.
   */
  @Nonnull
  String getCompiledTemplateFilePath(@Nonnull VendorLibrary vendorLibrary)
      throws ResourceNotFoundException;

  /**
   * Cache compiled HtlTemplate files for a specified UiFramework.
   *
   * @param uiFramework UiFramework to cache compiled HtlTemplateFile output for.
   * @throws CacheBuilderException Failed to build HTL Template cache.
   */
  void cacheCompiledHtlTemplates(UiFramework uiFramework) throws CacheBuilderException;

  /**
   * Cache compiled HtlTemplate files for a specified UiFramework.
   *
   * @param vendorLibrary VendorLibrary to cache compiled HtlTemplateFile output for.
   * @throws CacheBuilderException Failed to build HTL Template cache.
   */
  void cacheCompiledHtlTemplates(VendorLibrary vendorLibrary) throws CacheBuilderException;

  /**
   * Cache all CompiledHtlTemplate files for all UiFrameworks.
   *
   * @throws CacheBuilderException Failed to build HTL Template cache.
   */
  void cacheAllUiFrameworkCompiledHtlTemplates() throws CacheBuilderException;

  /**
   * Cache all CompiledHtlTemplate files for all UiFrameworks.
   *
   * @throws CacheBuilderException Failed to build HTL Template cache.
   */
  void cacheAllVendorLibraryCompiledHtlTemplates() throws CacheBuilderException;

}
