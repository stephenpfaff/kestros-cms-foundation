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

package io.kestros.cms.foundation.design.vendorlibrary;

import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.getFailedErrorValidators;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.getFailedWarningValidators;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import io.kestros.cms.foundation.design.htltemplate.HtlTemplateFile;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.uilibraries.UiLibraryValidationService;
import org.apache.commons.lang3.StringUtils;

/**
 * Model Validation service for validation VendorLibraries.
 */
public class VendorLibraryValidationService extends UiLibraryValidationService {

  @Override
  public VendorLibrary getModel() {
    return (VendorLibrary) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    super.registerBasicValidators();
    addBasicValidator(hasDocumentationUrl());
    for (HtlTemplateFile templateFile : getModel().getTemplateFiles()) {
      templateFile.doDetailedValidation();
      for (ModelValidator validator : getFailedErrorValidators(templateFile)) {
        addBasicValidator(validator);
      }
      for (ModelValidator validator : getFailedWarningValidators(templateFile)) {
        addBasicValidator(validator);
      }
    }
  }

  @Override
  public void registerDetailedValidators() {

  }

  ModelValidator hasDocumentationUrl() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isNotEmpty(getModel().getDocumentationUrl());
      }

      @Override
      public String getMessage() {
        return "Has documentation URL.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }
}
