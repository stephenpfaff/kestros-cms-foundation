package io.kestros.cms.foundation.services.pagecacheservice.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.componenttypes.HtmlFileType;
import io.kestros.cms.foundation.content.pages.BaseContentPage;
import io.kestros.cms.foundation.services.pagecacheservice.GeneralPageCacheService;
import io.kestros.cms.foundation.services.pagecacheservice.PageCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Caches pages by storing their HTML output as a new nt:file under /var/cache/pages.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, GeneralPageCacheService.class,
               PageCacheService.class},
           property = "service.ranking:Integer=1")
public class JcrFilePageCacheService extends JcrFileCacheService
    implements GeneralPageCacheService {

  // todo clean up user name
  public static final String KESTROS_PAGE_CACHE_PURGE_SERVICE_USER = "kestros-page-cache-purge";
  private static final long serialVersionUID = 7298277513481005750L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient JobManager jobManager;

  @Override
  public String getServiceCacheRootPath() {
    return "/var/cache/pages";
  }

  @Override
  protected String getServiceUserName() {
    return KESTROS_PAGE_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  public String getDisplayName() {
    return "Page Cache Service";
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 1000;
  }

  @Override
  protected String getCacheCreationJobName() {
    return null;
  }

  @Override
  protected JobManager getJobManager() {
    return jobManager;
  }

  @Override
  public void cachePage(final BaseContentPage page, final String htmlResponse)
      throws CacheBuilderException {
    createCacheFile(htmlResponse, page.getPath() + ".html", new HtmlFileType());
  }

  @Override
  public String getCachedOutput(@Nonnull final BaseContentPage page)
      throws CacheRetrievalException {
    try {
      return getCachedFile(page.getPath() + ".html",
          new HtmlFileType().getFileModelClass()).getOutput();
    } catch (final IOException | ResourceNotFoundException | InvalidResourceTypeException exception) {
      throw new CacheRetrievalException(exception.getMessage());
    }
  }
}
