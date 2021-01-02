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

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.services.ComponentViewScriptResolutionCacheService;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.BaseCacheService;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;

/**
 * Uses a simple HashMap to provide resolved paths for
 * {@link io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView}
 * resolution.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, ComponentViewScriptResolutionCacheService.class},
           property = "service.ranking:Integer=100")
public class CachedScriptProviderService extends BaseCacheService
    implements ComponentViewScriptResolutionCacheService {

  private static final long serialVersionUID = -6294003601560084296L;
  private Map<String, Object> componentViewCacheMap = new HashMap<>();

  @Override
  public void activate(ComponentContext componentContext) {
    // Does nothing.
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    // Does nothing.
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    if (componentViewCacheMap.isEmpty()) {
      log.warn("Component view cache map is empty.");
    }
  }

  @Override
  public void cacheComponentViewScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, String resolvedScriptPath, SlingHttpServletRequest request) {

    String cacheKey = request.getRequestURI() + "::" + uiFramework.getPath() + "::"
                      + componentType.getPath() + "::" + scriptName;

    if (componentViewCacheMap.containsKey(cacheKey)) {
      componentViewCacheMap.replace(cacheKey, resolvedScriptPath);
    }
    componentViewCacheMap.put(cacheKey, resolvedScriptPath);
  }

  @Override
  public String getCachedScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, SlingHttpServletRequest request) throws CacheRetrievalException {
    String cacheKey = request.getRequestURI() + "::" + uiFramework.getPath() + "::"
                      + componentType.getPath() + "::" + scriptName;

    if (componentViewCacheMap.containsKey(cacheKey)) {
      Object cachedScriptValue = componentViewCacheMap.get(cacheKey);
      if (cachedScriptValue instanceof String) {
        return (String) cachedScriptValue;
      } else {
        componentViewCacheMap.remove(cacheKey);
        throw new CacheRetrievalException(
            String.format("Cache for %s could not be cast to String.", cacheKey));
      }
    }
    throw new CacheRetrievalException(String.format("Cache for %s did not exist.", cacheKey));
  }


  @Override
  protected void doPurge(ResourceResolver resourceResolver) throws CachePurgeException {
    componentViewCacheMap = new HashMap<>();
  }

  @Override
  protected void afterCachePurgeComplete(ResourceResolver resourceResolver) {
    // Does nothing.
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 1;
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
    return "Component Script Resolution Cache";
  }
}
