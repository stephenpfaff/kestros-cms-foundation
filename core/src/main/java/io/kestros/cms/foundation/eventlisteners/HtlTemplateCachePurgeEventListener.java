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

package io.kestros.cms.foundation.eventlisteners;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import io.kestros.cms.foundation.services.HtlTemplateCacheService;
import io.kestros.commons.osgiserviceutils.services.eventlisteners.impl.BaseCachePurgeOnResourceChangeEventListener;
import java.util.List;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Listens for changes to /etc and /libs and purges, then rebuilds the compiled HTL Template cache
 * for all UiFrameworks.
 */
@Component(service = ResourceChangeListener.class,
           property = {ResourceChangeListener.CHANGES + "=ADDED",
               ResourceChangeListener.CHANGES + "=CHANGED",
               ResourceChangeListener.CHANGES + "=REMOVED",
               ResourceChangeListener.CHANGES + "=PROVIDER_ADDED",
               ResourceChangeListener.CHANGES + "=PROVIDER_REMOVED",
               ResourceChangeListener.PATHS + "=/etc", ResourceChangeListener.PATHS + "=/libs"},
           immediate = true)
public class HtlTemplateCachePurgeEventListener
    extends BaseCachePurgeOnResourceChangeEventListener {

  public static final String KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER
      = "kestros-htl-template-cache-purge";

  @SuppressWarnings("unused")
  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public List<HtlTemplateCacheService> getCacheServices() {
    return getAllOsgiServicesOfType(getComponentContext(), HtlTemplateCacheService.class);
  }

  @Override
  public ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }

  @Override
  protected String getServiceUserName() {
    return KESTROS_HTL_TEMPLATE_CACHE_PURGE_SERVICE_USER;
  }

  @Override
  protected boolean purgeOnActivation() {
    return false;
  }

  @Override
  public String getDisplayName() {
    return "HTL Template Cache Purge Event Listener";
  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }
}
