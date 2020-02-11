/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.kestros.cms.foundation.eventlisteners.pagecachepurge;

import static io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils.getAllOsgiServicesOfType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.services.pagecacheservice.PageCacheService;
import io.kestros.commons.osgiserviceutils.services.eventlisteners.impl.BaseCachePurgeOnResourceChangeEventListener;
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
  public List<PageCacheService> getCacheServices() {
    return getAllOsgiServicesOfType(getComponentContext(), PageCacheService.class);
  }

  @Override
  public ResourceResolverFactory getResourceResolverFactory() {
    return resourceResolverFactory;
  }
}

