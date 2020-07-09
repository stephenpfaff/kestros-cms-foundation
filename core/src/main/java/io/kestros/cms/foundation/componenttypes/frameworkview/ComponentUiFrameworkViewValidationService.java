/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.cms.foundation.componenttypes.frameworkview;

import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasDescription;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasTitle;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.commons.structuredslingmodels.validation.CommonValidators;
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
    addBasicValidator(hasTitle(getModel()));
    addBasicValidator(hasDescription(getModel(), WARNING));
    addBasicValidator(isAllIncludedScriptsFound());
    addBasicValidator(isAllDependenciesFound());

    addBasicValidator(hasValidContentScript());
    addBasicValidator(CommonValidators.modelListHasNoErrors(getModel().getVariations(),
        "Variations have no errors."));
    addBasicValidator(CommonValidators.modelListHasNoWarnings(getModel().getVariations(),
        "Variations have no warnings."));
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
        return ERROR;
      }
    };
  }

}
