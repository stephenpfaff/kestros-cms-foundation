package io.kestros.cms.foundation.exceptions;

import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
 */
public class InvalidComponentUiFrameworkViewException extends ModelAdaptionException {

  private static final long serialVersionUID = 6294137605911848689L;

  /**
   * Exception thrown when a ComponentUiFrameworkView cannot be retrieved from a ComponentType.
   *
   * @param componentTypePath ComponentType that the expected componentUiFrameworkView could not
   *     be retrieved from.
   * @param uiFramework UiFramework
   */
  public InvalidComponentUiFrameworkViewException(final String componentTypePath,
      final UiFramework uiFramework) {
    super(String.format(
        "Unable to ComponentUiFrameworkView for ComponentType'%s' and UiFramework '%s'.",
        componentTypePath, uiFramework.getPath()));
  }
}
