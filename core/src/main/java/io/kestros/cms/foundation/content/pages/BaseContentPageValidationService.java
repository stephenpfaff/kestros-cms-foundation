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

package io.kestros.cms.foundation.content.pages;

import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.getFailedErrorValidators;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.getFailedWarningValidators;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasDescription;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasTitle;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;

/**
 * Model Validation service for BaseContentPage models.
 */
public class BaseContentPageValidationService extends ModelValidationService {

  @Override
  public BaseContentPage getModel() {
    return (BaseContentPage) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    addBasicValidator(hasTitle(getModel()));
    addBasicValidator(hasDescription(getModel(), WARNING));
    addBasicValidator(hasTheme());

    for (final BaseComponent component : getModel().getAllDescendantComponents()) {
      component.doDetailedValidation();
      for (final ModelValidator validator : getFailedErrorValidators(component)) {
        addBasicValidator(validator);
      }
      for (final ModelValidator validator : getFailedWarningValidators(component)) {
        addBasicValidator(validator);
      }
    }
  }

  @Override
  public void registerDetailedValidators() {
    for (final BaseContentPage page : getModel().getChildPages()) {
      page.doDetailedValidation();
      if (!(page).getPath().equals(getModel().getPath())) {
        for (final ModelValidator validator : getFailedErrorValidators(page)) {
          addDetailedValidator(validator);
        }
        for (final ModelValidator validator : getFailedWarningValidators(page)) {
          addDetailedValidator(validator);
        }
      }
    }
  }

  private ModelValidator hasTheme() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          return getModel().getTheme() != null;
        } catch (final ResourceNotFoundException | InvalidThemeException e) {
          return false;
        }
      }

      @Override
      public String getMessage() {
        return "Must have an assigned Theme.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }
}