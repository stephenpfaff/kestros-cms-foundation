package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;

public interface ComponentViewScriptResolutionCacheService {

  void cacheComponentViewScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, String resolvedScriptPath);

  String getCachedScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework) throws CacheRetrievalException;

}
