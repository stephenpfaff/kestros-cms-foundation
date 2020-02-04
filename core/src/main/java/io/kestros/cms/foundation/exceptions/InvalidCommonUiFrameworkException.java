package io.kestros.cms.foundation.exceptions;

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when the 'common' ComponentUiFrameworkView cannot be retrieved.
 */
public class InvalidCommonUiFrameworkException extends ModelAdaptionException {

  private static final long serialVersionUID = -2417421095024869920L;

  /**
   * Exception thrown when the 'common' ComponentUiFrameworkView cannot be retrieved.
   *
   * @param componentTypePath ComponentType where 'common' could not be retrieved.
   */
  public InvalidCommonUiFrameworkException(final String componentTypePath) {
    super("Unable to retrieve 'common' ComponentUiFrameworkView for '" + componentTypePath + "'.");
  }
}
