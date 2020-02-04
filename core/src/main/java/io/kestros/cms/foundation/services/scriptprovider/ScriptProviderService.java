package io.kestros.cms.foundation.services.scriptprovider;

import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;

public interface ScriptProviderService {

  String getScriptPath(ParentComponent parentComponent, String scriptName)
      throws InvalidScriptException;

}
