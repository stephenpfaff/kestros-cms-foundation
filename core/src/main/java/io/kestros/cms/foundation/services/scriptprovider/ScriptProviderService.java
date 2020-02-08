package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;

/**
 * Provides script paths for {@link ParentComponent}.
 */
public interface ScriptProviderService {

  /**
   * Retrieves an absolute path for the matching HTL scrip for the passed {@link ParentComponent}.
   *
   * @param parentComponent Component to retrieve a script from.
   * @param scriptName Script to look up.
   * @return An absolute path for the matching scriptName for the passed {@link ParentComponent}.
   * @throws InvalidScriptException Expected HTL script was not not found, or was an invalid
   *     {@link io.kestros.cms.foundation.componenttypes.HtmlFile}
   */
  String getScriptPath(ParentComponent parentComponent, String scriptName)
      throws InvalidScriptException;

}
