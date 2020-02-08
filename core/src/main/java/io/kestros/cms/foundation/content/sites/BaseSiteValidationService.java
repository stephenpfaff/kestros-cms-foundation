package io.kestros.cms.foundation.content.sites;

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;

import io.kestros.cms.foundation.content.pages.BaseContentPageValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;

/**
 * ModelValidationService for {@link BaseSite} models.
 */
public class BaseSiteValidationService extends BaseContentPageValidationService {

  @Override
  public BaseSite getModel() {
    return (BaseSite) getGenericModel();
  }

  ModelValidator hasPages() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return !getModel().getChildPages().isEmpty();
      }

      @Override
      public String getMessage() {
        return "Site has pages.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }
}
