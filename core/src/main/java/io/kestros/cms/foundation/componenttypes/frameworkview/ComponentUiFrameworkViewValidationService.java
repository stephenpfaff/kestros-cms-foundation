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

import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplateParameter;
import io.kestros.cms.foundation.design.htltemplate.usage.HtlTemplateParameterUsage;
import io.kestros.cms.foundation.design.htltemplate.usage.HtlTemplateUsage;
import io.kestros.cms.foundation.exceptions.InvalidScriptException;
import io.kestros.cms.foundation.utils.ComponentTypeUtils;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.validation.CommonValidators;
import io.kestros.commons.structuredslingmodels.validation.DocumentedModelValidator;
import io.kestros.commons.structuredslingmodels.validation.DocumentedModelValidatorBundle;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.uilibraries.UiLibraryValidationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model Validation Service for validating ComponentUiFrameworkViews.
 */
public class ComponentUiFrameworkViewValidationService extends UiLibraryValidationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ComponentUiFrameworkViewValidationService.class);

  private List<HtlTemplateUsage> htlTemplateUsageList;

  @Override
  public ComponentUiFrameworkView getModel() {
    return (ComponentUiFrameworkView) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    try {
      htlTemplateUsageList = ComponentTypeUtils.getHtlTemplateUsageList(getModel());
      addBasicValidator(isAllUsedTemplatesValid());
      addBasicValidator(isProperParametersUsedForEachTemplateUsage());
    } catch (Exception e) {
      LOG.warn(
          "ComponentUiFrameworkView Validation Service failed to register template usage "
          + "validators. {}.",
          e.getMessage());
    }

    addBasicValidator(isAllIncludedScriptsFound());
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

  DocumentedModelValidator isAllUsedTemplatesValid() {
    return new DocumentedModelValidator() {
      @Override
      public String getResourceType() {
        return "kestros/components/component-management/troubleshooting/view/called-templates"
               + "-exist";
      }

      @Override
      public boolean isValid() {
        for (HtlTemplateUsage templateUsage : htlTemplateUsageList) {
          try {
            if (templateUsage.getUsedHtlTemplate() == null) {
              return false;
            }
          } catch (ResourceNotFoundException e) {
            return false;
          }
        }

        return true;
      }

      @Override
      public String getMessage() {
        return "Content script only calls existing HTL Templates.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  DocumentedModelValidatorBundle isProperParametersUsedForEachTemplateUsage() {
    return new DocumentedModelValidatorBundle() {
      @Override
      public String getResourceType() {
        return "";
      }

      @Override
      public void registerValidators() {

        for (HtlTemplateUsage templateUsage : htlTemplateUsageList) {
          for (HtlTemplateParameterUsage parameter :
              templateUsage.getTemplateParameterUsageList()) {
            if (parameter.getParameter() == null) {
              addBasicValidator(isHtlTemplateParameterUsageValid(parameter));
            }
          }
          try {
            for (HtlTemplateParameter parameter :
                templateUsage.getUsedHtlTemplate().getTemplateParameters()) {
              boolean parameterIsUsed = false;
              for (HtlTemplateParameterUsage parameterUsage :
                  templateUsage.getTemplateParameterUsageList()) {
                if (parameterUsage.getName().equals(parameter.getName())) {
                  parameterIsUsed = true;
                  continue;
                }
              }
              if (!parameterIsUsed) {
                addBasicValidator(isHtlTemplateParameterMissing(parameter));
              }
            }
          } catch (ResourceNotFoundException e) {
            //            todo log.
          }
        }

      }


      @Override
      public String getBundleMessage() {
        return "All HTL Templates are properly implemented.";
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  ModelValidator isHtlTemplateParameterMissing(HtlTemplateParameter parameter) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return parameter.getName() + " is not used.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
  ModelValidator isHtlTemplateParameterUsageValid(HtlTemplateParameterUsage parameter) {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return false;
      }

      @Override
      public String getMessage() {
        return parameter.getName() + " - Parameter is called, but does not exist on the template.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }


}
