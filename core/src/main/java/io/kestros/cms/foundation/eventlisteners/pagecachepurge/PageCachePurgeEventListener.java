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

package io.kestros.cms.foundation.eventlisteners.pagecachepurge;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.services.cache.validation.ValidationCacheService;
import io.kestros.cms.foundation.services.componenttypecache.ComponentTypeCache;
import io.kestros.cms.foundation.services.pagecacheservice.PageCacheService;
import io.kestros.cms.foundation.services.scriptprovider.ComponentViewScriptResolutionCacheService;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import io.kestros.commons.osgiserviceutils.services.eventlisteners.impl.BaseCachePurgeOnResourceChangeEventListener;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * ResourceChangeListener which listens for changes to /etc, /libs, /apps, /content and purges the
 * cache for all PageCacheServices when any change is detected.
 */
@Component(service = ResourceChangeListener.class,
           property = {ResourceChangeListener.CHANGES + "=ADDED",
               ResourceChangeListener.CHANGES + "=CHANGED",
               ResourceChangeListener.CHANGES + "=REMOVED",
               ResourceChangeListener.CHANGES + "=PROVIDER_ADDED",
               ResourceChangeListener.CHANGES + "=PROVIDER_REMOVED",
               ResourceChangeListener.PATHS + "=/etc", ResourceChangeListener.PATHS + "=/libs",
               ResourceChangeListener.PATHS + "=/apps", ResourceChangeListener.PATHS + "=/content"})
public class PageCachePurgeEventListener extends BaseCachePurgeOnResourceChangeEventListener {

  public static final String KESTROS_PAGE_CACHE_PURGE_SERVICE_USER = "kestros-page-cache-purge";

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  @Reference
  private transient ResourceResolverFactory resourceResolverFactory;

  @Override
  protected String getServiceUserName() {
    return KESTROS_PAGE_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  protected boolean purgeOnActivation() {
    return false;
  }

  @Override
  public List<CacheService> getCacheServices() {
    List<CacheService> cacheServices = new ArrayList<>();
    cacheServices.addAll(getAllOsgiServicesOfType(getComponentContext(), PageCacheService.class));
    cacheServices.addAll(getAllOsgiServicesOfType(getComponentContext(), ComponentTypeCache.class));
    cacheServices.addAll(getAllOsgiServicesOfType(getComponentContext(),
        ComponentViewScriptResolutionCacheService.class));
    return cacheServices;
  }

  @Override
  public ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }
}

