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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getFirstAncestorOfType;
import static java.lang.Integer.parseInt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

/**
 * Version of a versionable resource.
 *
 * @param <T> extends VersionableResource.
 */
public interface VersionResource<T extends VersionableResource> {

  /**
   * Managing versionable Resource model Class.
   *
   * @return Managing versionable Resource model Class.
   */
  @JsonIgnore
  Class<T> getManagingResourceType();

  /**
   * Sling Resource.
   *
   * @return Sling Resource.
   */
  @JsonIgnore
  Resource getResource();

  /**
   * Whether the version is deprecated.
   *
   * @return Whether the version is deprecated.
   */
  default Boolean isDeprecated() {
    return getResource().getValueMap().get("deprecated", Boolean.FALSE);
  }

  /**
   * Managing Versionable resource.
   *
   * @param <S> extends BaseResource.
   * @return Managing Versionable resource.
   * @throws NoValidAncestorException Could not retrieve the root versionable resource.
   */
  @JsonIgnore
  default <S extends BaseResource> T getRootResource() throws NoValidAncestorException {
    Class type = getManagingResourceType();
    BaseResource resource = getFirstAncestorOfType(((S) this), ((Class<S>) type));
    if (resource instanceof VersionableResource) {
      return (T) resource;
    } else {
      throw new NoValidAncestorException(String.format(
          "Versionable resource %s could not be cast to %s. Resource was not an instance of "
          + "VersionableResource", resource.getPath(), getManagingResourceType().getSimpleName()),
          getManagingResourceType());
    }
  }

  /**
   * Version information.
   *
   * @return Version information.
   * @throws VersionFormatException Version information was not formatted properly.
   */
  default Version getVersion() throws VersionFormatException {
    String versionProperty = getResource().getValueMap().get("versionNumber", StringUtils.EMPTY);
    String version = "";
    if (StringUtils.isEmpty(versionProperty)) {
      version = getResource().getName();
    }
    if (version.split("\\.").length == 3) {
      String[] versionParts = version.split("\\.");
      return new Version(parseInt(versionParts[0]), parseInt(versionParts[1]),
          parseInt(versionParts[2]));
    }
    if (StringUtils.isEmpty(versionProperty)) {
      return null;
    }
    throw new VersionFormatException();
  }

}