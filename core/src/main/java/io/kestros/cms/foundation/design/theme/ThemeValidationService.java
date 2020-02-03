package io.kestros.cms.foundation.design.theme;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;

import io.kestros.cms.foundation.exceptions.InvalidUiFrameworkException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.uilibraries.UiLibraryValidationService;

/**
 * Model Validation service for Kestros Themes.
 */
public class ThemeValidationService extends UiLibraryValidationService {

  @Override
  public Theme getModel() {
    return (Theme) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    super.registerBasicValidators();
    addBasicValidator(isChildOfAUiFramework());
  }

  ModelValidator isChildOfAUiFramework() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          for (final Theme theme : getModel().getUiFramework().getThemes()) {
            if (theme.getName().equals(getModel().getName())) {
              return true;
            }
          }
          return false;
        } catch (final InvalidUiFrameworkException e) {
          return false;
        }
      }

      @Override
      public String getMessage() {
        return "Lives in UiFramework's 'themes' folder.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }
}
