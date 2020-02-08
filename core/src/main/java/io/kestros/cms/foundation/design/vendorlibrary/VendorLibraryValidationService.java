package io.kestros.cms.foundation.design.vendorlibrary;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

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
