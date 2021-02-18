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

package io.kestros.cms.versioning.core.utils;

import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts version resources by their version numbers.
 */
public class VersionResourceSorter implements Comparator<BaseResource>, Serializable {

  private static final long serialVersionUID = -6211524234868758103L;

  @Override
  public int compare(BaseResource resource1, BaseResource resource2) {
    VersionResource versionResource1;
    VersionResource versionResource2;
    Version version1;
    Version version2;
    if (resource1 instanceof VersionResource) {
      versionResource1 = (VersionResource) resource1;
    } else {
      return -1;
    }
    if (resource2 instanceof VersionResource) {
      versionResource2 = (VersionResource) resource2;
    } else {
      return 1;
    }

    try {
      version1 = versionResource1.getVersion();
    } catch (VersionFormatException e) {
      return -1;
    }
    try {
      version2 = versionResource2.getVersion();
    } catch (VersionFormatException e) {
      return 1;
    }
    return version1.compareTo(version2);
  }
}

