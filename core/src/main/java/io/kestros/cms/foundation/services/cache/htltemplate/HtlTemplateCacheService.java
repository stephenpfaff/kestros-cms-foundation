package io.kestros.cms.foundation.services.cache.htltemplate;

import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;

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

}
