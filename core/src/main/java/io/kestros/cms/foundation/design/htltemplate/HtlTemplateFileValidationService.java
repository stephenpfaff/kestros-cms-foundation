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

package io.kestros.cms.foundation.design.htltemplate;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import org.apache.commons.lang3.StringUtils;

/**
 * Validation Service for HTL template files.
 */
public class HtlTemplateFileValidationService extends ModelValidationService {

  @Override
  public HtlTemplateFile getModel() {
    return (HtlTemplateFile) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    addBasicValidator(isAllTemplatesHaveTitle());
    addBasicValidator(isAllTemplatesHaveDescriptions());
    addBasicValidator(isAllTemplateParametersHaveDescriptions());
  }

  @Override
  public void registerDetailedValidators() {

  }

  ModelValidator isAllTemplatesHaveTitle() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        for (HtlTemplate template : getModel().getTemplates()) {
          if (template.getTitle().equals(template.getName())) {
            addBasicValidator(new ModelValidator() {
              @Override
              public boolean isValid() {
                return false;
              }

              @Override
              public String getMessage() {
                return template.getName() + "must have a configured title.";
              }

              @Override
              public ModelValidationMessageType getType() {
                return ModelValidationMessageType.WARNING;
              }
            });
          }
        }
      }

      @Override
      public String getBundleMessage() {
        return "All templates have titles";
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.WARNING;
      }
    };
  }

  ModelValidator isAllTemplatesHaveDescriptions() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        for (HtlTemplate template : getModel().getTemplates()) {
          if (StringUtils.isBlank(template.getDescription())) {
            addBasicValidator(new ModelValidator() {
              @Override
              public boolean isValid() {
                return false;
              }

              @Override
              public String getMessage() {
                return template.getName() + "must have a configured description.";
              }

              @Override
              public ModelValidationMessageType getType() {
                return ModelValidationMessageType.WARNING;
              }
            });
          }
        }
      }

      @Override
      public String getBundleMessage() {
        return "All templates have descriptions";
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.WARNING;
      }
    };
  }

  ModelValidator isAllTemplateParametersHaveDescriptions() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        for (HtlTemplate template : getModel().getTemplates()) {
          for (HtlTemplateParameter parameter : template.getTemplateParameters()) {
            if (StringUtils.isBlank(parameter.getDescription())) {
              addBasicValidator(new ModelValidator() {
                @Override
                public boolean isValid() {
                  return false;
                }

                @Override
                public String getMessage() {
                  return "Parameter " + parameter.getName()
                         + "must have a configured description for template \""
                         + template.getTitle() + "\".";
                }

                @Override
                public ModelValidationMessageType getType() {
                  return ModelValidationMessageType.WARNING;
                }
              });
            }
          }
        }
      }

      @Override
      public String getBundleMessage() {
        return "All templates parameters must have descriptions.";
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.WARNING;
      }
    };
  }
}
