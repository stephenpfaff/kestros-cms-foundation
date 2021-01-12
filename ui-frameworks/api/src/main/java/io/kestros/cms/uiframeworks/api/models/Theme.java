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

package io.kestros.cms.uiframeworks.api.models;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getParentResourceAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.uiframeworks.api.exceptions.InvalidUiFrameworkException;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Structured UiLibrary which is built off of a specific UiFramework and can have any number of
 * defined variations.
 */
@KestrosModel()
@Model(adaptables = Resource.class,
       resourceType = "kes:Theme")
@Exporter(name = "jackson",
          selector = "theme",
          extensions = "json")
public class Theme extends UiLibrary implements IconResource {

  private static final Logger LOG = LoggerFactory.getLogger(Theme.class);

  @OSGiService
  @Optional
  private ThemeOutputCompilationService themeOutputCompilationService;

  private UiFramework uiFramework;

  /**
   * UiFramework that the current Theme derives from.
   *
   * @return UiFramework that the current Theme derives from.
   * @throws InvalidUiFrameworkException Ancestor UiFramework was invalid or could not be
   *     found.
   */
  @Nonnull
  @JsonIgnore
  public UiFramework getUiFramework() throws InvalidUiFrameworkException {
    if (uiFramework == null) {
      try {
        uiFramework = getParentResourceAsType(getParent(), UiFramework.class);
        return uiFramework;
      } catch (final ModelAdaptionException exception) {
        LOG.error("Unable to retrieve UiFramework for Theme {}. {}", getPath(),
            exception.getMessage());
        throw new InvalidUiFrameworkException(this, exception.getMessage());
      }
    }
    return uiFramework;
  }

  /**
   * Returns the uncached output for the specified ScriptType.
   *
   * @param scriptType ScriptType to retrieve.
   * @return The uncached output for the specified ScriptType.
   */
  @Override
  public String getOutput(final ScriptType scriptType, final boolean minify)
      throws InvalidResourceTypeException {
    if (themeOutputCompilationService != null) {
      return themeOutputCompilationService.getThemeOutput(this, scriptType, minify);
    }
    return null;
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @JsonIgnore
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fas fa-paint-brush");
  }
}