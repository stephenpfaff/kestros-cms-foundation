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
import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import io.kestros.commons.structuredslingmodels.validation.ModelValidatorBundle;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Model Validation service for Kestros ComponentTypes.
 */
public class ComponentTypeValidationService extends ModelValidationService {

  @SuppressWarnings("unchecked")
  @Override
  public ComponentType getModel() {
    return (ComponentType) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    addBasicValidator(hasTitle(getModel()));
    addBasicValidator(hasDescription(getModel(), WARNING));
    addBasicValidator(hasComponentGroup());
    addBasicValidator(doesSuperTypeKestrosParentComponent());
    addBasicValidator(isValidAcrossAllUiFrameworksOrBypassUiFrameworkValidation());
    addBasicValidator(doesNotSuperTypeItself());
    addBasicValidator(hasFontAwesomeIcon());
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
}
