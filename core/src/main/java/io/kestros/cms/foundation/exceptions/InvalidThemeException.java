package io.kestros.cms.foundation.exceptions;

import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when an expected Theme is invalid or missing.
 */
public class InvalidThemeException extends ModelAdaptionException {

  private static final long serialVersionUID = -1786191060331649630L;

  protected InvalidThemeException(final String message) {
    super(message);
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param uiFramework UiFramework the expected Theme should belong to.
   * @param themeName Expected Theme name.
   * @param message Cause message.
   */
  public InvalidThemeException(final UiFramework uiFramework, final String themeName, final String message) {
    this(uiFramework.getPath(), themeName, message);
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param uiFrameworkPath Path of UiFramework the expected Theme should belong to.
   * @param themeName Expected Theme name.
   * @param message Cause message.
   */
  public InvalidThemeException(final String uiFrameworkPath, final String themeName, final String message) {
    super(String.format("Unable to retrieve theme '%s' under UiFramework '%s'. %s", themeName,
        uiFrameworkPath, message));
  }

  /**
   * Exception thrown when an expected Theme is invalid or missing.
   *
   * @param themePath Absolute path to expected Theme.
   * @param message Cause message.
   */
  public InvalidThemeException(final String themePath, final String message) {
    super(String.format("Unable to retrieve theme '%s'. %s", themePath, message));
  }
}
