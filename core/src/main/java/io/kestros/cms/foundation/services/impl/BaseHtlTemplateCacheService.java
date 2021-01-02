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

package io.kestros.cms.foundation.services.impl;

import static io.kestros.cms.foundation.utils.DesignUtils.getAllUiFrameworks;
import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getOpenServiceResourceResolverOrNullAndLogExceptions;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.componenttypes.HtmlFile;
import io.kestros.cms.foundation.componenttypes.HtmlFileType;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.services.HtlTemplateCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline service for caching compiled HTL Template files for UiFrameworks with Kestros.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, HtlTemplateCacheService.class},
           property = "service.ranking:Integer=100")
public class BaseHtlTemplateCacheService extends JcrFileCacheService
    implements HtlTemplateCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseHtlTemplateCacheService.class);

  public static final String UI_FRAMEWORKS_CACHE_ROOT
      = "/apps/kestros/cache/compiled-htl-templates";

  private static final String HTL_TEMPLATE_CACHE_SERVICE_USER = "kestros-htl-template-cache";

  private static final long serialVersionUID = 6704602826025832237L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  @Override
  @Activate
  public void activate(ComponentContext componentContext) {
    super.activate(componentContext);
    try {
      this.cacheAllUiFrameworkCompiledHtlTemplates(10);
    } catch (CacheBuilderException e) {
      LOG.error("Failed to rebuild compiled HTL Template files during bundle activation.");
    }
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    super.runAdditionalHealthChecks(log);
    if (getServiceResourceResolver() != null) {
      try {
        if (getAllDescendantsOfType(getCacheRootResource(), HtmlFile.class).size() == 0) {
          log.critical("HtlTemplateCacheService has no cached compilation files.");
        }
      } catch (ResourceNotFoundException e) {
        log.critical("Root template cache folder not found.");
      }
    }
  }

  @Override
  public String getServiceCacheRootPath() {
    return UI_FRAMEWORKS_CACHE_ROOT;
  }

  @Override
  protected String getServiceUserName() {
    return HTL_TEMPLATE_CACHE_SERVICE_USER;
  }

  @Override
  protected ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  protected String getCacheCreationJobName() {
    return null;
  }

  @Override
  protected JobManager getJobManager() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "HTL Template Cache";
  }

  /**
   * Caches Compiled HTL Template files for all UiFrameworks.
   */
  public void cacheAllUiFrameworkCompiledHtlTemplates() {
    if (getServiceResourceResolver() == null) {
      this.serviceResourceResolver = getOpenServiceResourceResolverOrNullAndLogExceptions(
          getServiceUserName(), getServiceResourceResolver(), getResourceResolverFactory(), this);
    }

    if (getServiceResourceResolver() != null) {
      int attempts = 0;
      LOG.info("Attempting to cache compiled HTL Template files for all UiFrameworks.");
      while (attempts < 10) {
        try {
          if (getAllUiFrameworks(getServiceResourceResolver(), true, true).size() == 0) {
            getServiceResourceResolver().refresh();
          }
          cacheAllUiFrameworkCompiledHtlTemplates(attempts);
          break;
        } catch (final CacheBuilderException e) {
          LOG.debug("Failed to build HTL Library cache. Attempt {}. {}", attempts, e.getMessage());
          attempts++;
        }
      }
      if (attempts <= 10) {
        LOG.info("Successfully cached all compiled HTL Template Libraries.");
      } else {
        LOG.error("Failed to cache all compiled HTL Template Libraries");
      }
    }
  }

  private void cacheAllUiFrameworkCompiledHtlTemplates(final int attempts)
      throws CacheBuilderException {
    LOG.debug("Attempting to build HTL Library cache. Attempt {}.", attempts);
    if (getServiceResourceResolver() != null) {
      for (final UiFramework uiFramework : getAllUiFrameworks(getServiceResourceResolver(), true,
          true)) {
        cacheUiFrameworkCompiledHtlTemplates(uiFramework);
      }
    } else {
      LOG.error(
          "Unable to cache all UiFramework HTL Templates. Service ResourceResolver was null.");
    }
  }

  /**
   * Caches a UiFramework's compiled HTL Template scripts.
   *
   * @param uiFramework UiFramework to cache HtlTemplate files for.
   * @throws CacheBuilderException Failed to build HTL Template cache for UiFramework.
   */
  public void cacheUiFrameworkCompiledHtlTemplates(final UiFramework uiFramework)
      throws CacheBuilderException {
    final StringBuilder templatesOutput = new StringBuilder();
    for (final HtlTemplate template : uiFramework.getTemplates()) {
      templatesOutput.append(template.getOutput());
    }
    cacheOutput(templatesOutput.toString(), uiFramework);
  }

  @Override
  protected void doPurge(final ResourceResolver resourceResolver) throws CachePurgeException {
    super.doPurge(resourceResolver);
    try {
      cacheAllUiFrameworkCompiledHtlTemplates(10);
    } catch (CacheBuilderException e) {
      LOG.error("Failed to rebuild compiled HTL Template files after cache purge.");
    }
  }

  @Override
  protected void afterCachePurgeComplete(ResourceResolver resourceResolver) {
    // Does nothing.
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 1000;
  }

  private void cacheOutput(final String output, final UiFramework uiFramework)
      throws CacheBuilderException {
    final String fileName = uiFramework.getPath() + ".html";
    createCacheFile(output, fileName, new HtmlFileType());
  }

  private BaseResource getCacheRootResource() throws ResourceNotFoundException {
    return getResourceAsBaseResource(getServiceCacheRootPath(), getServiceResourceResolver());
  }
}
