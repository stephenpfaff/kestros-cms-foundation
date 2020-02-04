package io.kestros.cms.foundation.services.pagecacheservice;

import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import javax.annotation.Nonnull;

public interface PageCacheService extends CacheService {

  void cachePage(BaseContentPage page, String htmlResponse) throws CacheBuilderException;

  String getCachedOutput(@Nonnull BaseContentPage page) throws CacheRetrievalException;

}
