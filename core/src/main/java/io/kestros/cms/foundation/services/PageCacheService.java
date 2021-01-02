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

package io.kestros.cms.foundation.services;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import javax.annotation.Nonnull;

/**
 * Service for managing, building, retrieving and purging Page output caches.
 */
public interface PageCacheService extends CacheService {

  /**
   * Caches a page's HTML output.
   *
   * @param page Page to cache.
   * @param htmlResponse HTML content to cache.
   * @throws CacheBuilderException Failed to cache page output.
   */
  void cachePage(BaseContentPage page, String htmlResponse) throws CacheBuilderException;

  /**
   * Retrieves the cached HTML output for a given page.
   *
   * @param page Page to retrieve cache for.
   * @return The cached HTML output for a given page.
   * @throws CacheRetrievalException Failed to retrieve a cached HTML output value for the given
   *     page.
   */
  String getCachedOutput(@Nonnull BaseContentPage page) throws CacheRetrievalException;

}
