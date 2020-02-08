package io.kestros.cms.foundation.services.pagecacheservice;

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
