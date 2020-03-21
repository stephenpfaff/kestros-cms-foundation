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

package io.kestros.cms.foundation.services.componenttypecache;

import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.BaseCacheService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;

/**
 * Uses a simple HashMap to maintain cache for ComponentTypes within Kestros.
 */
@Component(immediate = true,
           service = {ManagedCacheService.class, ComponentTypeCache.class},
           property = "service.ranking:Integer=100")
public class ComponentTypeCacheImpl extends BaseCacheService implements ComponentTypeCache {

  private static final long serialVersionUID = -7190437579645105257L;

  private Map<String, List<String>> componentTypePathList = new HashMap<>();

  @Override
  public Map<String, List<String>> getAllCachedComponentTypePaths() {
    return this.componentTypePathList;
  }

  @Override
  public List<String> getAllCachedComponentTypes(String rootPath) throws CacheRetrievalException {
    if (componentTypePathList.containsKey(rootPath)) {
      return componentTypePathList.get("key");
    }
    throw new CacheRetrievalException(
        String.format("Failed to retrieve cached ComponentType list under %s.", rootPath));
  }

  @Override
  public void cacheComponentTypePathList(String rootPath, List<String> componentTypePathList) {
    if (this.componentTypePathList.containsKey(rootPath)) {
      this.componentTypePathList.replace(rootPath, componentTypePathList);
    } else {
      this.componentTypePathList.put(rootPath, componentTypePathList);
    }
  }

  @Override
  protected void doPurge(ResourceResolver resourceResolver) throws CachePurgeException {
    this.componentTypePathList = new HashMap<>();
  }

  @Override
  protected long getMinimumTimeBetweenCachePurges() {
    return 10;
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
  public void activate() {

  }

  @Override
  public void deactivate() {

  }

  @Override
  public String getDisplayName() {
    return "ComponentType Cache";
  }
}
