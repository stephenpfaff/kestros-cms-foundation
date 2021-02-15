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


import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.ThemeOutputCompilationService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.cms.uiframeworks.core.models.ThemeResource;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import io.kestros.commons.uilibraries.core.services.impl.UiLibraryCompilationServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
public class ThemeOutputCompilationServiceImpl extends UiLibraryCompilationServiceImpl
    implements ThemeOutputCompilationService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ThemeOutputCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiFrameworkOutputCompilationService uiFrameworkOutputCompilationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @Override
  public List<ScriptType> getThemeScriptTypes(Theme theme, ScriptType scriptType) {
    String tracker = startPerformanceTracking();
    List<ScriptType> scriptTypes = new ArrayList<>();
    scriptTypes.addAll(
        uiFrameworkOutputCompilationService.getUiFrameworkScriptTypes(theme.getUiFramework(),
            scriptType));

    for (ScriptType themeScriptType : uiLibraryCompilationService.getLibraryScriptTypes(theme,
        scriptType.getName())) {
      if (!scriptTypes.contains(themeScriptType)) {
        scriptTypes.add(themeScriptType);
      }
    }

    endPerformanceTracking(tracker);
    return scriptTypes;
  }

  @Override
  public String getUiLibraryOutput(FrontendLibrary library, ScriptType scriptType)
      throws InvalidResourceTypeException, NoMatchingCompilerException {
    String tracker = startPerformanceTracking();
    if (library instanceof Theme) {
      Theme theme = (Theme) library;
      String themeSource = getThemeSource(theme, scriptType);
      endPerformanceTracking(tracker);
      return uiLibraryCompilationService.getCompiler(getThemeScriptTypes(theme, scriptType),
          uiLibraryCompilationService.getCompilers()).getOutput(themeSource);
    }
    endPerformanceTracking(tracker);
    throw new InvalidResourceTypeException(library.getPath(), ThemeResource.class,
        "Failed to compile Theme output. Resource was not a valid Resource Theme resourceType");
  }

  @Override
  public String getDisplayName() {
    return "Theme Output Compilation Service";
  }

  @Override
  public void activate(ComponentContext componentContext) {
    LOG.info("Activating {}.", getDisplayName());
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating {}.", getDisplayName());
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
  @Override
  public String getThemeSource(Theme theme, ScriptType scriptType)
      throws NoMatchingCompilerException, InvalidResourceTypeException {
    String tracker = startPerformanceTracking();
    final StringBuilder source = new StringBuilder();
    if (uiLibraryCompilationService != null && uiFrameworkOutputCompilationService != null) {
      UiFramework uiFramework = theme.getUiFramework();
      String uiFrameworkSource = uiFrameworkOutputCompilationService.getUiFrameworkSource(
          uiFramework, scriptType);
      if (StringUtils.isNotEmpty(uiFrameworkSource)) {
        source.append(uiFrameworkSource);
      }

      String themeSource = uiLibraryCompilationService.getUiLibrarySource(theme, scriptType);
      if (StringUtils.isNotEmpty(source.toString()) && StringUtils.isNotEmpty(themeSource)) {
        source.append("\n");
      }
      source.append(themeSource);
    }
    endPerformanceTracking(tracker);
    return source.toString();
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
