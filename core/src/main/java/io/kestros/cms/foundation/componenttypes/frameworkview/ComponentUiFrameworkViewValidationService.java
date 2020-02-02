package io.kestros.cms.foundation.componenttypes.frameworkview;

import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.uilibraries.UiLibraryValidationService;

/**
 * Model Validation Service for validating ComponentUiFrameworkViews.
 */
public class ComponentUiFrameworkViewValidationService extends UiLibraryValidationService {

  @Override
  public ComponentUiFrameworkView getModel() {
    return (ComponentUiFrameworkView) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    super.registerBasicValidators();

    addBasicValidator(hasValidContentScript());
  }

  ModelValidator hasValidContentScript() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          getModel().getUiFrameworkViewScript("content.html");
        } catch (final InvalidScriptException exception) {
          return false;
        }
        return true;
      }

      @Override
      public String getMessage() {
        return "Must have content.html script.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.ERROR;
      }
    };
  }
}
