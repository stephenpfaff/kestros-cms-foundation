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

package io.kestros.cms.foundation.services.cache.validation;

import io.kestros.commons.osgiserviceutils.exceptions.CacheRetrievalException;
import io.kestros.commons.osgiserviceutils.services.cache.ManagedCacheService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import java.util.List;
import org.apache.sling.api.resource.Resource;

/**
 * Manages Resource Model validation caches.
 */
public interface ValidationCacheService extends ManagedCacheService {

  /**
   * Retrieves cached validators for a specified resource.
   *
   * @param resource Resource to retrieve validation cache for.
   * @param clazz Model class that the resource was validated against.
   * @param isDetailed Whether to retrieve detailed validation results.
   * @param <T> extends BaseResource.
   * @return Cached validators for a specified resource.
   * @throws CacheRetrievalException Failed to retrieve a cached ModelValidation list for the
   *     specified Class and detail level.
   */
  <T extends BaseResource> List<ModelValidator> getCachedValidators(Resource resource,
      Class<T> clazz, boolean isDetailed) throws CacheRetrievalException;

  /**
   * Retrieves cached error messages for a specified resource, when adapted to the specified Class.
   *
   * @param resource Resource to cache validators for.
   * @param clazz Model class that the resource was validated against.
   * @param isDetailed Whether to retrieve cache as detailed ( false = basic).
   * @param <T> extends BaseResource
   * @return List of validation errors as String.
   * @throws CacheRetrievalException Failed to retrieve a cached error message list.
   */
  <T extends BaseResource> List<String> getCachedErrorMessages(Resource resource, Class<T> clazz,
      boolean isDetailed) throws CacheRetrievalException;

  /**
   * Retrieves cached warning messages for a specified resource, when adapted to the specified
   * Class.
   *
   * @param resource Resource to cache validators for.
   * @param clazz Model class that the resource was validated against.
   * @param isDetailed Whether to retrieve cache as detailed ( false = basic).
   * @param <T> extends BaseResource
   * @return List of validation warnings as String.
   * @throws CacheRetrievalException Failed to retrieve a cached warning message list.
   */
  <T extends BaseResource> List<String> getCachedWarningMessages(Resource resource, Class<T> clazz,
      boolean isDetailed) throws CacheRetrievalException;

  /**
   * Caches validators for a specified resource.
   *
   * @param model model to cache validators for.
   * @param isDetailed Whether to retrieve cache as detailed ( false = basic).
   * @param <T> extends BaseResource
   */
  <T extends BaseResource> void cacheValidationResults(T model, boolean isDetailed);

}
