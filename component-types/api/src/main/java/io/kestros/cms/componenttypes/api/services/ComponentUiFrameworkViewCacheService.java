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

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import java.util.List;

/**
 * Caches ComponentUiFramework view paths for UiFrameworks.
 */
public interface ComponentUiFrameworkViewCacheService extends CacheService {

  /**
   * Retrieves cached View paths for a specified UiFramework.
   *
   * @param uiFramework path to retrieve cached ComponentType paths for.
   * @param includeApps Whether to include views for that live under /apps.
   * @param includeAllLibs Whether to include views for that live under /libs.
   * @param includeLibsCommons Whether to include views for that live under /libs/commons.
   * @return Paths of ComponentTypes that live under the specified root path.
   * @throws CacheRetrievalException Failed to retrieve cached values.
   */
  List<String> getCachedComponentUiFrameworkViewPaths(UiFramework uiFramework, Boolean includeApps,
      Boolean includeLibsCommons, Boolean includeAllLibs) throws CacheRetrievalException;

  /**
   * Caches componentTypes that live under a specified path.
   *
   * @param uiFramework UiFramework
   * @param appsPaths Views which live under /apps
   * @param libsPaths Views which live under /libs
   * @param libsCommonsPaths Views which live under /libs/commons
   * @throws CacheBuilderException Failed cache values.
   */
  void cacheComponentUiFrameworkViewPathList(UiFramework uiFramework, List<String> appsPaths,
      List<String> libsPaths, List<String> libsCommonsPaths) throws CacheBuilderException;

}
