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

package io.kestros.cms.sitebuilding.core.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.filetypes.HtmlFileType;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.sitebuilding.api.services.GeneralPageCacheService;
import io.kestros.cms.sitebuilding.api.services.PageCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ResourceResolver;
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

  public static final String KESTROS_PAGE_CACHE_PURGE_SERVICE_USER = "kestros-page-cache";
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
  protected List<String> getRequiredResourcePaths() {
    List<String> requiredPaths = new ArrayList<>();
    requiredPaths.add(getServiceCacheRootPath());
    return requiredPaths;
  }

  @Override
  protected void afterCachePurgeComplete(ResourceResolver resourceResolver) {
    // Does nothing.
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
          new HtmlFileType().getFileModelClass()).getFileContent();
    } catch (final IOException | ResourceNotFoundException
                               | InvalidResourceTypeException exception) {
      throw new CacheRetrievalException(exception.getMessage());
    }
  }
}
