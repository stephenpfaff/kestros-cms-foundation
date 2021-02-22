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

package io.kestros.cms.uiframeworks.core.services;

import static io.kestros.cms.uiframeworks.core.models.HtlTemplateFileResource.EXTENSION_HTML;
import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getOpenServiceResourceResolverOrNullAndLogExceptions;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsBaseResource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.cms.filetypes.HtmlFileType;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateCacheService;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.JcrFileCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline service for caching compiled HTL Template files for UiFrameworks with Kestros.
 */
@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
@Component(immediate = true,
           service = {ManagedCacheService.class, HtlTemplateCacheService.class},
           property = "service.ranking:Integer=100")
public class HtlTemplateCacheServiceImpl extends JcrFileCacheService
    implements HtlTemplateCacheService, ManagedService {

  private static final long serialVersionUID = -36074253147694345L;

  private static final Logger LOG = LoggerFactory.getLogger(HtlTemplateCacheServiceImpl.class);

  public static final String UI_FRAMEWORKS_CACHE_ROOT
      = "/apps/kestros/cache/compiled-htl-templates";

  private static final String HTL_TEMPLATE_CACHE_SERVICE_USER = "kestros-htl-template-cache";

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @SuppressFBWarnings("SE_BAD_FIELD")
  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  @Activate
  public void activate(ComponentContext componentContext) {
    super.activate(componentContext);
    //    try {
    this.cacheAllUiFrameworkCompiledHtlTemplates();
    //    } catch (CacheBuilderException e) {
    //      LOG.error("Failed to rebuild compiled HTL Template files during bundle activation. {}",
    //          e.getMessage());
    //    } catch (HtlTemplateFileRetrievalException e) {
    //      LOG.error("Failed to rebuild compiled HTL Template files during bundle activation.
    //      Failed to "
    //                + "retrieve HTL Template Files. {}", e.getMessage());
    //    }
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    super.runAdditionalHealthChecks(log);
    if (getServiceResourceResolver() != null) {
      try {
        if (getAllDescendantsOfType(getCacheRootResource(), HtmlFile.class).size() == 0) {
          log.warn("HtlTemplateCacheService has no cached compilation files.");
        }
      } catch (ResourceNotFoundException e) {
        log.critical("Root template cache folder not found.");
      }
    }
  }

  @Override
  protected List<String> getRequiredResourcePaths() {
    List<String> resourcePaths = new ArrayList<>();
    resourcePaths.add(getServiceCacheRootPath());
    resourcePaths.add("/etc/ui-frameworks");
    resourcePaths.add("/etc/vendor-libraries");
    resourcePaths.add("/libs/kestros/ui-frameworks");
    resourcePaths.add("/libs/kestros/vendor-libraries");
    return resourcePaths;
  }

  @Override
  public String getCompiledTemplateFilePath(UiFramework uiFramework)
      throws ResourceNotFoundException {
    BaseResource getCacheRootResource = getResourceAsBaseResource(getServiceCacheRootPath(),
        getServiceResourceResolver());
    LOG.trace("Searching for HTL Template File cache in {}", getCacheRootResource.getPath());
    try {
      return getResourceAsBaseResource(
          getServiceCacheRootPath() + uiFramework.getPath() + EXTENSION_HTML,
          getServiceResourceResolver()).getPath();
    } catch (final ResourceNotFoundException e) {
      cacheAllUiFrameworkCompiledHtlTemplates();
      return getResourceAsBaseResource(
          getServiceCacheRootPath() + uiFramework.getPath() + EXTENSION_HTML,
          getServiceResourceResolver()).getPath();
    }
  }

  @Override
  public void cacheCompiledHtlTemplates(UiFramework uiFramework) throws CacheBuilderException {
    int attempts = 0;
    while (attempts < 100) {
      try {
        cacheCompiledHtlTemplates(uiFramework, attempts);
        return;
      } catch (CacheBuilderException e) {
        LOG.debug(e.getMessage());
      } catch (HtlTemplateFileRetrievalException e) {
        LOG.debug(e.getMessage());
      }
      attempts++;
    }
    throw new CacheBuilderException(
        String.format("Failed to build HTL Template cache for UI Framework %s.",
            uiFramework.getPath()));
  }

  /**
   * Cache compiled HtlTemplate files for a specified UiFramework.
   *
   * @param uiFramework UI Framework.
   * @param attempts Number of attempts.
   * @throws CacheBuilderException Failed cache building.
   * @throws HtlTemplateFileRetrievalException failed to retrieve HTL Template files.
   */
  public void cacheCompiledHtlTemplates(UiFramework uiFramework, final int attempts)
      throws CacheBuilderException, HtlTemplateFileRetrievalException {
    final StringBuilder templatesOutput = new StringBuilder();

    if (htlTemplateFileRetrievalService != null) {
      for (HtlTemplateFile file : htlTemplateFileRetrievalService.getHtlTemplatesFromUiFramework(
          uiFramework)) {
        if (StringUtils.isNotEmpty(templatesOutput)) {
          templatesOutput.append("\n");
        }
        try {
          templatesOutput.append(file.getFileContent());
        } catch (IOException e) {
          throw new CacheBuilderException(e.getMessage());
        }
      }
      if (StringUtils.isNotEmpty(templatesOutput.toString())) {
        cacheOutput(templatesOutput.toString(), uiFramework);
      } else {
        throw new CacheBuilderException(String.format(
            "Failed to Build HTL Template cache for UiFramework %s. Compiled HTL Template file "
            + "was empty.", uiFramework.getPath()));
      }
    } else {
      throw new CacheBuilderException(String.format(
          "Failed to Build HTL Template cache for UiFramework %s. HtlTemplateFileRetrievalService"
          + " was null.", uiFramework.getPath()));
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
    return "HTL Template Cache Service";
  }


  /**
   * Caches Compiled HTL Template files for all UiFrameworks.
   */
  public void cacheAllUiFrameworkCompiledHtlTemplates() {
    if (uiFrameworkRetrievalService != null) {
      if (getServiceResourceResolver() == null) {
        this.serviceResourceResolver = getOpenServiceResourceResolverOrNullAndLogExceptions(
            getServiceUserName(), getServiceResourceResolver(), getResourceResolverFactory(), this);
      }

      if (getServiceResourceResolver() != null) {
        int attempts = 0;
        LOG.info("Attempting to cache compiled HTL Template files for all UiFrameworks.");
        List<UiFramework> uiFrameworkList
            = uiFrameworkRetrievalService.getAllUnmanagedUiFrameworksAndManagedUiFrameworkVersions(
            true, true);

        if (uiFrameworkList.size() == 0) {
          getServiceResourceResolver().refresh();
        }
        for (final UiFramework uiFramework : uiFrameworkList) {
          try {
            cacheCompiledHtlTemplates(uiFramework);
          } catch (CacheBuilderException e) {
            LOG.error(e.getMessage());
          }
        }
      }
    }
    //        } catch (final CacheBuilderException e) {
    //          LOG.warn("Failed to build HTL Library cache. Attempt {}. {}", attempts, e
    //          .getMessage());
    //          attempts++;
    //        } catch (HtlTemplateFileRetrievalException e) {
    //          LOG.warn(
    //              "Failed to build HTL Library cache due to HTLTemplateFile retrieval error.
    //              Attempt "
    //              + "{}. {}", attempts, e.getMessage());
    //          attempts++;
    //        }
    //      }
    //      if (attempts <= 10) {
    //        LOG.info("Successfully cached all compiled HTL Template Libraries.");
    //      }
  }


  @Override
  protected void doPurge(final ResourceResolver resourceResolver) throws CachePurgeException {
    super.doPurge(resourceResolver);
    cacheAllUiFrameworkCompiledHtlTemplates();
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
