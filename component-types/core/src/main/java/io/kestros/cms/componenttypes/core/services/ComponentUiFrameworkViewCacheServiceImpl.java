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

package io.kestros.cms.componenttypes.core.services;

import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewCacheService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheBuilderException;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.BaseCacheService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caches ComponentUiFramework view paths for UiFrameworks.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, ComponentUiFrameworkViewCacheService.class},
           property = "service.ranking:Integer=100")
public class ComponentUiFrameworkViewCacheServiceImpl extends BaseCacheService
    implements ComponentUiFrameworkViewCacheService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ComponentUiFrameworkViewCacheServiceImpl.class);
  private static final long serialVersionUID = -7704202132724713236L;

  private Map<String, Map<String, List<String>>> uiFrameworkComponentViewPathList = new HashMap<>();

  @Override
  public List<String> getCachedComponentUiFrameworkViewPaths(UiFramework uiFramework,
      Boolean includeApps, Boolean includeLibsCommons, Boolean includeAllLibs)
      throws CacheRetrievalException {
    List<String> viewPaths = new ArrayList<>();
    if (uiFrameworkComponentViewPathList.containsKey(uiFramework.getPath())) {
      Map<String, List<String>> uiFrameworkViews = uiFrameworkComponentViewPathList.get(
          uiFramework.getPath());

      if (includeApps) {
        viewPaths.addAll(uiFrameworkViews.get("apps"));
      }
      if (includeAllLibs) {
        viewPaths.addAll(uiFrameworkViews.get("libs"));
      } else if (includeLibsCommons) {
        viewPaths.addAll(uiFrameworkViews.get("libs-commons"));
      }
      return viewPaths;
    }
    throw new CacheRetrievalException(String.format("Key %s not found.", uiFramework.getPath()));
  }

  @Override
  public void cacheComponentUiFrameworkViewPathList(UiFramework uiFramework, List<String> appsPaths,
      List<String> libsPaths, List<String> libsCommonsPaths) throws CacheBuilderException {
    Map<String, List<String>> viewsMap = new HashMap<>();
    viewsMap.put("apps", appsPaths);
    viewsMap.put("libs", libsPaths);
    viewsMap.put("libs-commons", libsCommonsPaths);
    if (this.uiFrameworkComponentViewPathList.containsKey(uiFramework.getPath())) {
      this.uiFrameworkComponentViewPathList.replace(uiFramework.getPath(), viewsMap);
    } else {
      this.uiFrameworkComponentViewPathList.put(uiFramework.getPath(), viewsMap);
    }
  }

  @Override
  protected void doPurge(ResourceResolver resourceResolver) throws CachePurgeException {
    this.uiFrameworkComponentViewPathList = new HashMap<>();
  }

  @Override
  protected void afterCachePurgeComplete(ResourceResolver resourceResolver) {

  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 100;
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
    return "Component UI Framework View Cache Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {
    LOG.info("Activating {}", getDisplayName());
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}", getDisplayName());
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }
}
