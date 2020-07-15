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

package io.kestros.cms.foundation.componenttypes;

import static io.kestros.cms.foundation.utils.DesignUtils.getAllUiFrameworks;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasDescription;
import static io.kestros.commons.structuredslingmodels.validation.CommonValidators.hasTitle;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.ERROR;
import static io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType.WARNING;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidCommonUiFrameworkException;
import io.kestros.cms.foundation.exceptions.InvalidComponentTypeException;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.validation.CommonValidators;
import io.kestros.commons.structuredslingmodels.validation.DocumentedModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Model Validation service for Kestros ComponentTypes.
 */
public class ComponentTypeValidationService extends ModelValidationService {

  private Class modelClass;

  @SuppressWarnings("unchecked")
  @Override
  public ComponentType getModel() {
    return (ComponentType) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    if (getModel().getModelTrackerService() != null) {
      this.modelClass = getModel().getModelTrackerService().getModelClassForResourceType(
          getModel().getImplementingComponentResourceType());
    }
    addBasicValidator(hasTitle(getModel()));
    addBasicValidator(hasDescription(getModel(), WARNING));
    addBasicValidator(hasComponentGroup());
    addBasicValidator(doesSuperTypeKestrosParentComponent());
    addBasicValidator(isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation());
    addBasicValidator(doesNotSuperTypeItself());
    addBasicValidator(hasFontAwesomeIcon());
    addBasicValidator(hasProperlyConfiguredModel());
    addBasicValidator(hasDocumentedModel());
    addBasicValidator(hasValidationService());
    addBasicValidator(CommonValidators.modelListHasNoErrors(getModel().getUiFrameworkViews(),
        "Views have no errors."));
    addBasicValidator(CommonValidators.modelListHasNoWarnings(getModel().getUiFrameworkViews(),
        "Views have no warnings."));
  }

  @Override
  public void registerDetailedValidators() {
    // No detailed validators
  }

  ModelValidator hasComponentGroup() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isNotEmpty(getModel().getComponentGroup());
      }

      @Override
      public String getMessage() {
        return "Component Group must be configured.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  ModelValidator doesSuperTypeKestrosParentComponent() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        ComponentType componentType = getModel();
        while (componentType != null) {
          if (componentType.getPath().equals("/libs/kestros/commons/components/kestros-parent")) {
            return true;
          }
          try {
            componentType = componentType.getComponentSuperType();
          } catch (final InvalidComponentTypeException e) {
            componentType = null;
          }
        }
        return false;
      }

      @Override
      public String getMessage() {
        return "SuperTypes Kestros Parent Component.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidatorBundle isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addBasicValidator(hasCommonUiFrameworkView());
        addBasicValidator(hasUiFrameworkViews());
        addBasicValidator(isBypassUiFrameworks());
      }

      @Override
      public String getBundleMessage() {
        return "Must be implemented across all UI Frameworks, or set to bypass UI Frameworks.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }
    };
  }

  ModelValidator isBypassUiFrameworks() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return getModel().isBypassUiFrameworks();
      }

      @Override
      public String getMessage() {
        return "Bypasses UiFrameworks Validation checks.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidator hasCommonUiFrameworkView() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          getModel().getCommonUiFrameworkView();
          return true;
        } catch (final InvalidCommonUiFrameworkException exception) {
          return false;
        }
      }

      @Override
      public String getMessage() {
        return "Has Common UiFramework view.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  @SuppressFBWarnings("SIC")
  ModelValidatorBundle hasUiFrameworkViews() {

    final List<ModelValidator> validatorList = new ArrayList<>();

    for (final UiFramework uiFramework : getAllUiFrameworks(getModel().getResourceResolver(), true,
        false)) {
      validatorList.add(new ModelValidator() {
        @Override
        public boolean isValid() {
          return !getModel().getMissingUiFrameworkCodes().contains(uiFramework.getFrameworkCode());
        }

        @Override
        public String getMessage() {
          return "UiFramework view " + uiFramework.getFrameworkCode() + " must be configured.";
        }

        @Override
        public ModelValidationMessageType getType() {
          return WARNING;
        }
      });
    }

    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addAllValidators(validatorList);
      }

      @Override
      public String getBundleMessage() {
        return "Contains views for all UiFrameworks.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }
    };
  }

  ModelValidator doesNotSuperTypeItself() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        try {
          return !getModel().getPath().equals(getModel().getComponentSuperType().getPath());
        } catch (final InvalidComponentTypeException e) {
          // do nothing
        }
        return true;
      }

      @Override
      public String getMessage() {
        return "Does not SuperType itself.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  ModelValidator hasFontAwesomeIcon() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isNotBlank(getModel().getProperty("fontAwesomeIcon", StringUtils.EMPTY));
      }

      @Override
      public String getMessage() {
        return "Has FontAwesome icon.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidatorBundle hasProperlyConfiguredModel() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addBasicValidator(hasMatchingModel());
        addBasicValidator(hasOnlyOneMatchingModel());
      }

      @Override
      public String getBundleMessage() {
        return "Model is properly configured.";
      }

      @Override
      public boolean isAllMustBeTrue() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  ModelValidator hasMatchingModel() {

    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return modelClass != null;
      }

      @Override
      public String getMessage() {
        return "Has a Sling Model associated to the resourceType.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  DocumentedModelValidator hasOnlyOneMatchingModel() {

    return new DocumentedModelValidator() {
      @Override
      public String getResourceType() {
        return "kestros/components/component-management/troubleshooting/multiple-model"
               + "-registration";
      }

      @Override
      public boolean isValid() {
        return getModel().getModelTrackerService().getAllClassesRegisteredToAResourceType(
            getModel().getImplementingComponentResourceType()).size() == 1;
      }

      @Override
      public String getMessage() {
        return "Registered to exactly one Model type.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ERROR;
      }
    };
  }

  ModelValidatorBundle hasDocumentedModel() {
    return new ModelValidatorBundle() {
      @Override
      public void registerValidators() {
        addBasicValidator(hasKestrosModelAnnotation());
        addBasicValidator(hasDocumentedMethods());
      }

      @Override
      public String getBundleMessage() {
        return "Model is properly documented.";
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

  ModelValidator hasKestrosModelAnnotation() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        if (modelClass != null) {
          return modelClass.getAnnotation(KestrosModel.class) != null;
        }
        return false;
      }

      @Override
      public String getMessage() {
        return "Model Class has KestrosModel annotation.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidator hasDocumentedMethods() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        for (Method method : modelClass.getDeclaredMethods()) {
          if (method.getAnnotation(KestrosProperty.class) == null) {
            return false;
          }
        }
        return true;
      }

      @Override
      public String getMessage() {
        return "All public methods are annotated with @KestrosProperty.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }

  ModelValidator hasValidationService() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        if (modelClass != null) {
          if (modelClass.getAnnotation(KestrosModel.class) != null) {
            KestrosModel kestrosModelAnnotation = (KestrosModel) modelClass.getAnnotation(
                KestrosModel.class);
            return kestrosModelAnnotation.validationService() != null;
          }
        }
        return false;
      }

      @Override
      public String getMessage() {
        return "Model has been configured with a ModelValidationService.";
      }

      @Override
      public ModelValidationMessageType getType() {
        return WARNING;
      }
    };
  }
}
