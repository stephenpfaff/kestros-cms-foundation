package io.kestros.cms.foundation.exceptions;

import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;

/**
 * Exception thrown when sightly scripts are invalid. Potentially caused by missing Resource, or
 * invalid file type.
 */
public class InvalidScriptException extends ModelAdaptionException {

  private static final long serialVersionUID = 6184696106043393068L;

  protected InvalidScriptException(final String message) {
    super(message);
  }

  /**
   * @param scriptName Resource name of script that was invalid.
   * @param uiFrameworkViewPath Path of UiFramework that script was being retrieved from.
   */
  public InvalidScriptException(final String scriptName, final String uiFrameworkViewPath) {
    this(String.format("Unable to adapt '%s' for ComponentUiFrameworkView '%s': %s", scriptName,
        uiFrameworkViewPath, "Script not found."));
  }

}
