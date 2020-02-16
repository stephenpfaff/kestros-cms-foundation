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
