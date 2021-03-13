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

package io.kestros.cms.versioning.api.services;

import io.kestros.cms.versioning.api.exceptions.VersionRetrievalException;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Performs version lookups.
 */
public interface VersionService {

  /**
   * Retrieves the current version for a versionable.
   *
   * @param resource Versionable resource.
   * @param <T> extends BaseResource.
   * @return Current version for a versionable.
   */
  <T extends BaseResource> T getCurrentVersion(VersionableResource resource);

  /**
   * Retrieves a specified version for a versionable.
   *
   * @param resource Versionable resource.
   * @param versionNumber Desired version.
   * @param <T> extends BaseResource.
   * @return Specified version for a versionable.
   * @throws VersionRetrievalException Failed to retrieve the specified version.
   */
  <T extends BaseResource> T getVersionResource(VersionableResource resource, String versionNumber)
      throws VersionRetrievalException;

  /**
   * All versions for a specified versionable.
   *
   * @param resource Versionable resource.
   * @param <T> extends BaseResource.
   * @return All versions for a specified versionable.
   */
  <T extends BaseResource> List<T> getVersionHistory(VersionableResource resource);

  /**
   * Previous version, for a given Version.
   *
   * @param resource Version Resource.
   * @param <T> extends BaseResource.
   * @return Previous version, for a given Version.
   * @throws NoValidAncestorException Could not retrieve the root versionable resource.
   */
  <T extends BaseResource> T getPreviousVersion(VersionResource resource)
      throws NoValidAncestorException;

  /**
   * Next version, for a given Version.
   *
   * @param resource Version Resource.
   * @param <T> extends BaseResource.
   * @return Next version, for a given Version.
   * @throws NoValidAncestorException Could not retrieve the root versionable resource.
   */
  <T extends BaseResource> T getNextVersion(VersionResource resource)
      throws NoValidAncestorException;

  /**
   * Closest matching version, for a given versionable. Matching versions will be less than or equal
   * to the specified version number.
   *
   * @param resource Versionable resource.
   * @param versionNumber Version number.
   * @param <T> extends BaseResource.
   * @return Closest matching version, for a given versionable.
   * @throws VersionRetrievalException Failed to find version earlier than desired version
   *     number.
   */
  @Nonnull
  <T extends BaseResource> T getClosestVersion(VersionableResource resource, String versionNumber)
      throws VersionRetrievalException;

}
