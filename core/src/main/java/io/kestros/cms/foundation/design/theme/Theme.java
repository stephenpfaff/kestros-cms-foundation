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

package io.kestros.cms.foundation.design.theme;

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getParentResourceAsType;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.CSS;
import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inet.lib.less.Less;
import io.kestros.cms.foundation.design.uiframework.UiFramework;
import io.kestros.cms.foundation.exceptions.InvalidUiFrameworkException;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.exceptions.ModelAdaptionException;
import io.kestros.commons.uilibraries.UiLibrary;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Structured UiLibrary which is built off of a specific UiFramework and can have any number of
 * defined variations.
 */
@StructuredModel(validationService = ThemeValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "kes:Theme")
@Exporter(name = "jackson",
          selector = "theme",
          extensions = "json")
public class Theme extends UiLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(Theme.class);

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
    final StringBuilder output = new StringBuilder();

    output.append(getUiFramework().getOutput(scriptType));

    output.append(super.getOutput(scriptType, false));

    if (CSS.equals(scriptType) || LESS.equals(scriptType)) {
      return Less.compile(null, output.toString(), minify);
    }

    if (uiLibraryMinificationService != null && minify) {
      try {
        return uiLibraryMinificationService.getMinifiedOutput(output.toString(), scriptType);
      } catch (final ScriptCompressionException e) {
        LOG.error("Unable to compress {} script for Theme {} when minification is {}. {}",
            scriptType.getName(), getPath(), minify, e.getMessage());
      }
    }
    return output.toString();
  }

}