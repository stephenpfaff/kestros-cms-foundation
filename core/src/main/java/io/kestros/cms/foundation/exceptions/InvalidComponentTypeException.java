package io.kestros.cms.foundation.exceptions;

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
 * missing.
 */
public class InvalidComponentTypeException extends ModelAdaptionException {

  private static final long serialVersionUID = -4673833087114663599L;

  /**
   *  Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
   *   missing.
   * @param componentTypePath Expected path of ComponentType that could not be retrieved.
   */
  public InvalidComponentTypeException(final String componentTypePath) {
    super(componentTypePath, "Invalid or missing ComponentType resource.");
  }

  /**
   *  Exception thrown when a ComponentType cannot not be retrieved because it is either invalid or
   *   missing.
   * @param componentTypePath Expected path of ComponentType that could not be retrieved.
   * @param message Cause message.
   */
  public InvalidComponentTypeException(final String componentTypePath, final String message) {
    super(String.format("Unable to adapt '%s' to ComponentType. %s", componentTypePath, message));
  }
}
