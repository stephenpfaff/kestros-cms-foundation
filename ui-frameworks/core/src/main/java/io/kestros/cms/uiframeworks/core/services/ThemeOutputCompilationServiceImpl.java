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

package io.kestros.cms.uiframeworks.core.services;

import static io.kestros.commons.uilibraries.filetypes.ScriptType.LESS;

import com.inet.lib.less.Less;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.exceptions.ScriptCompressionException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.services.minification.UiLibraryMinificationService;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Baseline Theme output compilation service.
 */
@Component(immediate = true,
           service = ThemeOutputCompilationService.class)
public class ThemeOutputCompilationServiceImpl implements ThemeOutputCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ThemeOutputCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryMinificationService uiLibraryMinificationService;

  @Override
  public String getThemeOutput(Theme theme, ScriptType scriptType, Boolean minify)
      throws InvalidResourceTypeException {
    String uncompiledOutput = getUncompiledThemeOutput(theme, scriptType);
    if (ScriptType.CSS.equals(scriptType) || LESS.equals(scriptType)) {
      return Less.compile(null, uncompiledOutput, minify);
    }

    if (uiLibraryMinificationService != null && minify) {
      try {
        return uiLibraryMinificationService.getMinifiedOutput(uncompiledOutput, scriptType);
      } catch (final ScriptCompressionException e) {
        LOG.error("Unable to compress {} script for Theme {} when minification is {}. {}",
            scriptType.getName(), theme.getPath(), minify, e.getMessage());
      }
    }
    return uncompiledOutput;
  }

  @Override
  public String getDisplayName() {
    return "Theme Output Compilation Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {

  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }

  /**
   * Css or Javascript output prior to being compiled or minified.
   *
   * @param theme Theme.
   * @param scriptType ScriptType to retrieve.
   * @return Css or Javascript output prior to being compiled or minified.
   * @throws InvalidResourceTypeException Thrown when a referenced dependency could not be
   *     adapted to UiLibrary.
   */
  private String getUncompiledThemeOutput(Theme theme, ScriptType scriptType)
      throws InvalidResourceTypeException {
    final StringBuilder output = new StringBuilder();
    output.append(theme.getUiFramework().getOutput(scriptType));
    if (uiLibraryCompilationService != null) {
      String uncompiledThemeLibrary = uiLibraryCompilationService.getUiLibraryOutput(theme,
          scriptType, false);
      output.append(uncompiledThemeLibrary);
    }
    return output.toString();
  }

}
