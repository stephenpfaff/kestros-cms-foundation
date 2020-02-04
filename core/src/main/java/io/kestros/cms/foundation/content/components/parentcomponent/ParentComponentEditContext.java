package io.kestros.cms.foundation.content.components.parentcomponent;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourceAsType;
import static java.lang.Boolean.parseBoolean;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.commons.structuredslingmodels.BaseSlingRequest;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = SlingHttpServletRequest.class)
public class ParentComponentEditContext extends BaseSlingRequest {

  private static final Logger LOG = LoggerFactory.getLogger(ParentComponentEditContext.class);

  public boolean isEditMode() {
    return parseBoolean(String.valueOf(getRequest().getAttribute("editMode")));
  }

  /**
   * Current edit mode Theme.
   *
   * @return Current edit mode Theme.
   */
  @Nullable
  public Theme getEditTheme() throws InvalidThemeException {
    final String editUiFrameworkPath = "/libs/kestros/ui-frameworks/kestros-editor-include";
    try {
      return getResourceAsType(editUiFrameworkPath, getRequest().getResourceResolver(),
          UiFramework.class).getDefaultTheme();
    } catch (final ChildResourceNotFoundException | ResourceNotFoundException | InvalidResourceTypeException exception) {
      LOG.error("Unable to retrieve edit theme {}. {}", editUiFrameworkPath,
          exception.getMessage());
      throw new InvalidThemeException(editUiFrameworkPath, "default", exception.getMessage());
    }
  }
}
