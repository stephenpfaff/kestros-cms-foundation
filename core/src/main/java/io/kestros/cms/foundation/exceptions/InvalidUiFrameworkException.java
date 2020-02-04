package io.kestros.cms.foundation.exceptions;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;

/**
 * Exception throw when a UiFramework is invalid and cannot be retrieved.
 */
public class InvalidUiFrameworkException extends InvalidResourceTypeException {

  protected InvalidUiFrameworkException(final String message) {
    super(message);
  }

  /**
   * Exception throw when a UiFramework is invalid and cannot be retrieved.
   *
   * @param uiFrameworkPath Expected UiFramework path that could not be retrieved.
   * @param message Cause message.
   */
  public InvalidUiFrameworkException(final String uiFrameworkPath, final String message) {
    super(String.format("Unable to retrieve UiFramework '%s'. %s", uiFrameworkPath, message));
  }

  /**
   * Exception throw when a UiFramework is invalid and cannot be retrieved from a Theme.
   *
   * @param theme Theme that could not find a ancestor UiFramework.
   * @param message Cause message.
   */
  public InvalidUiFrameworkException(final Theme theme, final String message) {
    super(String.format("Unable to retrieve parent UiFramework for theme '%s'. %s", theme.getPath(),
        message));
  }
}
