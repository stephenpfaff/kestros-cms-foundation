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
