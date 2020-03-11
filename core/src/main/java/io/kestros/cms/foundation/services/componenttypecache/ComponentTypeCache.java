package io.kestros.cms.foundation.services.componenttypecache;

import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import java.util.List;

public interface ComponentTypeCache extends CacheService {

  List<String> getAllCachedComponentTypePaths();

  void cacheComponentTypePathList(List<String> componentTypePathList);
}
