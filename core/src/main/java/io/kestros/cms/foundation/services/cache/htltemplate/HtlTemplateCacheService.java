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

package io.kestros.cms.foundation.services.cache.htltemplate;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service for building and purging HTL Template caches.
 */
public interface HtlTemplateCacheService extends CacheService {

  /**
   * Path where cached compiled HtlTemplate files are stored.
   *
   * @return Path where cached compiled HtlTemplate files are stored.
   */
  String getServiceCacheRootPath();

  /**
   * Cache all CompiledHtlTemplate files for all UiFrameworks.
   *
   * @throws CacheBuilderException Failed to build HTL Template cache.
   */
  void cacheAllUiFrameworkCompiledHtlTemplates() throws CacheBuilderException;

  /**
   * Service ResourceResolver.
   *
   * @return Service ResourceResolver.
   */
  ResourceResolver getServiceResourceResolver();

}
