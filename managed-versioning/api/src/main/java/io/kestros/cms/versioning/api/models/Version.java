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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Semantic Version information.
 */
public class Version {

  private Integer majorRelease;
  private Integer minorRelease;
  private Integer patchRelease;

  /**
   * Constructs version object.
   *
   * @param majorRelease Major release version.
   * @param minorRelease Minor release version.
   * @param patchRelease Patch release version.
   */
  public Version(Integer majorRelease, Integer minorRelease, Integer patchRelease) {
    this.majorRelease = majorRelease;
    this.minorRelease = minorRelease;
    this.patchRelease = patchRelease;
  }

  /**
   * Version formatted as a String.
   *
   * @return Version formatted as a String.
   */
  public String getFormatted() {
    return String.format("%s.%s.%s", majorRelease, minorRelease, patchRelease);
  }

  /**
   * Major version.
   *
   * @return Major version.
   */
  public Integer getMajorVersion() {
    return majorRelease;
  }

  /**
   * Minor version.
   *
   * @return Minor version.
   */
  public Integer getMinorVersion() {
    return minorRelease;
  }

  /**
   * Patch version.
   *
   * @return Patch version.
   */
  public Integer getPatchVersion() {
    return patchRelease;
  }

  /**
   * Compares two versions to see which is has a higher version number.
   *
   * @param version Version to compare to.
   * @return Compares two versions to see which is has a higher version number.
   */
  @SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
  public int compareTo(Version version) {
    if (getMajorVersion().equals(version.getMajorVersion())) {
      if (getMinorVersion().equals(version.getMinorVersion())) {
        if (getPatchVersion().equals(version.getPatchVersion())) {
          return 0;
        }
        if (getPatchVersion() > version.getPatchVersion()) {
          return 1;
        } else {
          return -1;
        }
      }
      if (getMinorVersion() > version.getMinorVersion()) {
        return 1;
      } else {
        return -1;
      }
    }
    if (getMajorVersion() > version.getMajorVersion()) {
      return 1;
    } else {
      return -1;
    }
  }

}