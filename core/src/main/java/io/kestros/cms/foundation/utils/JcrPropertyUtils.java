package io.kestros.cms.foundation.utils;

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
