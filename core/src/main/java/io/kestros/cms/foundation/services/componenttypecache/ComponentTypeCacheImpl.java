package io.kestros.cms.foundation.services.componenttypecache;

import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.BaseCacheService;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
           service = {ManagedCacheService.class, ComponentTypeCache.class},
           property = "service.ranking:Integer=100")
public class ComponentTypeCacheImpl extends BaseCacheService implements ComponentTypeCache {

  private List<String> componentTypePathList = new ArrayList<>();

  @Override
  public List<String> getAllCachedComponentTypePaths() {
    return this.componentTypePathList;
  }

  @Override
  public void cacheComponentTypePathList(List<String> componentTypePathList) {
    this.componentTypePathList = componentTypePathList;
  }

  @Override
  protected void doPurge(ResourceResolver resourceResolver) throws CachePurgeException {
    this.componentTypePathList = new ArrayList<>();
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
