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

package io.kestros.cms.foundation.services.componenttypecache;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import java.util.List;
import java.util.Map;

/**
 * Maintains cache for ComponentTypes within Kestros.
 */
public interface ComponentTypeCache extends CacheService {

  /**
   * Retrieves full ComponentType path cache.
   *
   * @return full ComponentType path cache.
   * @throws CacheRetrievalException Failed to retrieve cached values.
   */
  Map<String, List<String>> getCachedComponentTypePaths() throws CacheRetrievalException;

  /**
   * Retrieves cached ComponentType paths under a specified root path.
   *
   * @param rootPath path to retrieve cached ComponentType paths for.
   * @return Paths of ComponentTypes that live under the specified root path.
   * @throws CacheRetrievalException Failed to retrieve cached values.
   */
  List<String> getCachedComponentTypes(String rootPath) throws CacheRetrievalException;

  /**
   * Caches componentTypes that live under a specified path.
   *
   * @param rootPath path to cache ComponentType path list for.
   * @param componentTypePathList paths to ComponentTypes that live under the root path.
   * @throws CacheBuilderException Failed cache values.
   */
  void cacheComponentTypePathList(String rootPath, List<String> componentTypePathList)
      throws CacheBuilderException;
}
