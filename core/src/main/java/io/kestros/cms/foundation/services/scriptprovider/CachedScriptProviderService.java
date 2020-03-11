package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CachePurgeException;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.impl.BaseCacheService;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true,
           service = {ManagedCacheService.class, ComponentViewScriptResolutionCacheService.class},
           property = "service.ranking:Integer=100")
public class CachedScriptProviderService extends BaseCacheService
    implements ComponentViewScriptResolutionCacheService {


  private static final long serialVersionUID = -6294003601560084296L;
  private Map<String, Object> componentViewCacheMap = new HashMap<>();

  @Override
  public void activate() {

  }

  @Override
  public void deactivate() {

  }

  public void cacheComponentViewScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, String resolvedScriptPath) {
    String cacheKey = uiFramework.getPath() + "::" + componentType.getPath() + "::" + scriptName;

    if (componentViewCacheMap.containsKey(cacheKey)) {
      componentViewCacheMap.replace(cacheKey, resolvedScriptPath);
    }
    componentViewCacheMap.put(cacheKey, resolvedScriptPath);
  }

  @Override
  public String getCachedScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework) throws CacheRetrievalException {
    String cacheKey = uiFramework.getPath() + "::" + componentType.getPath() + "::" + scriptName;

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
