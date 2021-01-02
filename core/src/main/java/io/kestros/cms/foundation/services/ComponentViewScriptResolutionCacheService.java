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

package io.kestros.cms.foundation.services;

import io.kestros.cms.foundation.componenttypes.ComponentType;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.CacheService;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Manages cache for providing resolved paths for
 * {@link io.kestros.cms.foundation.componenttypes.frameworkview.ComponentUiFrameworkView}
 * resolution.
 */
public interface ComponentViewScriptResolutionCacheService extends CacheService {

  /**
   * Caches a resolved script path for a specified script, when accounting for ComponentType and the
   * requesting page's UiFramework.
   *
   * @param scriptName Name of script to cache resolved path for.
   * @param componentType ComponentType that the script belongs to.
   * @param uiFramework UiFramework to cache resolved path for.
   * @param resolvedScriptPath Resolved script path to cache.
   * @param request SlingHttpServletRequest. Required for inheritance and references.
   */
  void cacheComponentViewScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, String resolvedScriptPath, SlingHttpServletRequest request);

  /**
   * Retrieves a cached resolved script path.
   *
   * @param scriptName Name of script.
   * @param componentType ComponentType the script belongs to.
   * @param uiFramework UiFramework to find view for.
   * @param request SlingHttpServletRequest.
   * @return Cached resolved script path.
   * @throws CacheRetrievalException Failed to retrieve resolved script path.
   */
  String getCachedScriptPath(String scriptName, ComponentType componentType,
      UiFramework uiFramework, SlingHttpServletRequest request) throws CacheRetrievalException;

}
