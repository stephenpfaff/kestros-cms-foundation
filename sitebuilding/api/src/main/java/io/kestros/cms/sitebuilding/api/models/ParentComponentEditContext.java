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

package io.kestros.cms.sitebuilding.api.models;

import static java.lang.Boolean.parseBoolean;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.sitebuilding.api.services.EditModeService;
import io.kestros.cms.sitebuilding.api.services.ScriptProviderService;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidThemeException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import javax.annotation.Nonnull;
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
public class ParentComponentEditContext extends ComponentRequestContext {

  private static final Logger LOG = LoggerFactory.getLogger(ParentComponentEditContext.class);

  @OSGiService
  @Optional
  private ScriptProviderService scriptProviderService;

  @OSGiService
  @Optional
  private EditModeService editModeService;

  /**
   * The path to the content.html script.
   *
   * @return The path to the content.html script.
   * @throws InvalidScriptException The script was not found, or could not be adapt to
   *     HtmlFile.
   * @throws InvalidComponentTypeException The componentType configured for the requested
   *     resource was missing or invalid.
   */
  @Nonnull
  public String getContentScriptPath()
      throws InvalidScriptException, InvalidComponentTypeException {
    LOG.trace("Getting Content Script Path for {}", getBaseResource().getPath());
    final String contentScriptPath = getScriptPath("content.html");
    LOG.trace("Retrieved Content Script Path for {}", getBaseResource().getPath());
    return contentScriptPath;
  }

  /**
   * Retrieves the path to a specified script name, resolved proper to the ComponentUiFramework
   * view.
   *
   * @param scriptName Script to retrieve.
   * @return The path to a specified script.
   * @throws InvalidScriptException The script was not found, or could not be adapt to *
   *     HtmlFile.
   * @throws InvalidComponentTypeException The componentType configured for the requested
   *     resource was missing or invalid.
   */
  @Nonnull
  public String getScriptPath(final String scriptName)
      throws InvalidScriptException, InvalidComponentTypeException {
    return scriptProviderService.getScriptPath(
        getBaseResource().getResource().adaptTo(ParentComponent.class), scriptName, getRequest());
  }

  /**
   * Whether the current request should render the page in Edit Mode. Looks to the editMode
   * parameter, I.E '/content/page.html?editMode=true'.
   *
   * @return Whether the current request should render the page in Edit Mode.
   */
  public boolean isEditMode() {
    if (editModeService != null && editModeService.isEditModeActive()) {
      return parseBoolean(getRequest().getParameter("editMode"));
    }
    return false;
  }

  /**
   * Current edit mode {@link Theme}.
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
