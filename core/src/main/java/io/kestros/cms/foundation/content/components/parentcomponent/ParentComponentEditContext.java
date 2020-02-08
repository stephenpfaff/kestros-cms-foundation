package io.kestros.cms.foundation.content.components.parentcomponent;

import static java.lang.Boolean.parseBoolean;

import io.kestros.cms.foundation.design.theme.Theme;
import io.kestros.cms.foundation.exceptions.InvalidThemeException;
import io.kestros.cms.foundation.services.editmodeservice.EditModeService;
import io.kestros.commons.structuredslingmodels.BaseSlingRequest;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request context attributes for {@link ParentComponent}. Contains logic for determining whether a
 * page is to be rendered in edit mode.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ParentComponentEditContext extends BaseSlingRequest {

  private static final Logger LOG = LoggerFactory.getLogger(ParentComponentEditContext.class);

  @OSGiService
  @Optional
  private EditModeService editModeService;

  /**
   * Whether the current request should render the page in Edit Mode. Looks to the editMode
   * parameter, I.E '/content/page.html?editMode=true'.
   *
   * @return Whether the current request should render the page in Edit Mode.
   */
  public boolean isEditMode() {
    if (editModeService != null && editModeService.isEditModeActive()) {
      return parseBoolean(String.valueOf(getRequest().getAttribute("editMode")));
    }
    return false;
  }

  /**
   * Current edit mode {@link Theme}
   *
   * @return Current edit mode Theme.
   * @throws InvalidThemeException Expected edit mode Theme was not found, or was not a valid
   *     Theme Resource.
   */
  @Nullable
  public Theme getEditTheme() throws InvalidThemeException {
    if (editModeService != null && editModeService.isEditModeActive()) {
      return editModeService.getEditModeTheme(getRequest());

    }
    return null;
  }
}
