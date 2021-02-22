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


package io.kestros.cms.versioning.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.apache.sling.api.resource.Resource;

/**
 * Baseline Versionable resource.
 *
 * @param <T> extends VersionResource.
 */
public interface VersionableResource<T extends VersionResource> {

  /**
   * Resource title.
   *
   * @return Title.
   */
  String getTitle();

  /**
   * Resource path.
   *
   * @return Resource path.
   */
  String getPath();

  /**
   * Resource name.
   *
   * @return Resource name.
   */
  String getName();

  /**
   * Baseline Sling Resource.
   *
   * @return Baseline Sling Resource.
   */
  @JsonPropertyOrder
  Resource getResource();

  /**
   * Class the version service looks for when retrieving versions.
   *
   * @return Class the version service looks for when retrieving versions.
   */
  @JsonIgnore
  Class<T> getVersionResourceType();

  /**
   * All managed versions.
   *
   * @return All managed versions.
   */
  List<T> getVersions();

  /**
   * Latest managed version.
   *
   * @return Latest managed version.
   */
  @JsonIgnore
  T getCurrentVersion();

}