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

package io.kestros.cms.sitebuilding.api.utils;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.adaptToBaseResource;

import io.kestros.commons.structuredslingmodels.BaseResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for converting Jcr Properties to various types.
 */
public class JcrPropertyUtils {

  private static final Logger LOG = LoggerFactory.getLogger(JcrPropertyUtils.class);

  private JcrPropertyUtils() {
  }

  /**
   * Converts a specified property's value to a RelativeDate object.
   *
   * @param resource Resource to retrieve property from.
   * @param propertyName Property to convert to relative date.
   * @return A specified property's value to as a RelativeDate.
   */
  public static RelativeDate getRelativeDate(final BaseResource resource,
      final String propertyName) {
    final String timestampString = resource.getProperties().get(propertyName, StringUtils.EMPTY);
    try {
      final long timestamp = Long.parseLong(timestampString);

      return new RelativeDate(timestamp);
    } catch (final NumberFormatException exception) {
      LOG.error(
          "Unable to build relative date String for property {} on {}, due to NumberException",
          propertyName, resource.getPath());
    }
    return null;
  }

  /**
   * This method is functionally the same as {@link #getRelativeDate(BaseResource, String)} but
   * accepts {@link Resource} instead of {@link BaseResource}.
   *
   * @param resource Resource to retrieve property from.
   * @param propertyName Property to convert to relative date.
   * @return A specified property's value to as a RelativeDate.
   */
  public static RelativeDate getRelativeDate(final Resource resource, final String propertyName) {
    return getRelativeDate(adaptToBaseResource(resource), propertyName);
  }

}
