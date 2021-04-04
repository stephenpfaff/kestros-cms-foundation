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

package io.kestros.cms.componenttypes.core.services;

import io.kestros.cms.componenttypes.api.exceptions.ComponentVariationRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentVariation;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentViewUiFrameworkOutputCompilationService;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import io.kestros.cms.componenttypes.core.models.ComponentVariationResource;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkCompilationAddonService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
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
 * Appends {@link ComponentUiFrameworkViewResource} and {@link ComponentVariationResource} CSS and
 * JS to a UiFramework's compiled output.
 */
@Component(immediate = true,
           service = {UiFrameworkCompilationAddonService.class,
               ComponentViewUiFrameworkOutputCompilationService.class})
public class ComponentViewUiFrameworkOutputCompilationServiceImpl
    implements ComponentViewUiFrameworkOutputCompilationService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ComponentViewUiFrameworkOutputCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentUiFrameworkViewRetrievalService componentUiFrameworkViewService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentVariationRetrievalService componentVariationRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public String getDisplayName() {
    return "Component UI Framework View Output Compilation Service";
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


  @Override
  public String getAppendedOutput(@Nonnull UiFramework uiFramework, @Nonnull ScriptType scriptType)
      throws InvalidResourceTypeException {
    String tracker = startPerformanceTracking();
    final StringBuilder output = new StringBuilder();

    if (componentUiFrameworkViewService != null) {
      for (final ComponentUiFrameworkView componentUiFrameworkView :
          componentUiFrameworkViewService.getComponentViews(
          uiFramework, true, true, true)) {
        String viewOutput = null;
        try {
          viewOutput = uiLibraryCompilationService.getUiLibrarySource(componentUiFrameworkView,
              scriptType);
        } catch (NoMatchingCompilerException e) {
          e.printStackTrace();
        }
        if (StringUtils.isNotEmpty(viewOutput) && StringUtils.isNotEmpty(output.toString())) {
          output.append("\n");
        }
        output.append(viewOutput);
        if (componentVariationRetrievalService != null) {
          try {
            for (ComponentVariation variation :
                componentVariationRetrievalService.getComponentVariations(
                componentUiFrameworkView)) {
              String variationOutput = uiLibraryCompilationService.getUiLibrarySource(variation,
                  scriptType);
              if (StringUtils.isNotEmpty(variationOutput) && StringUtils.isNotEmpty(
                  output.toString())) {
                output.append("\n");
              }
              output.append(variationOutput);
            }
          } catch (ComponentVariationRetrievalException e) {
            LOG.debug(e.getMessage());
          } catch (NoMatchingCompilerException e) {
            LOG.debug(e.getMessage());
          }
        }
      }
    }
    endPerformanceTracking(tracker);
    return output.toString();
  }

  @Override
  public List<ScriptType> getAddonScriptTypes(@Nonnull UiFramework uiFramework,
      @Nonnull ScriptType scriptType) throws InvalidResourceTypeException {
    String tracker = startPerformanceTracking();
    List<ScriptType> scriptTypes = new ArrayList<>();
    if (uiLibraryCompilationService != null) {
      for (final ComponentUiFrameworkView componentUiFrameworkView :
          componentUiFrameworkViewService.getComponentViews(
          uiFramework, true, true, true)) {
        scriptTypes.addAll(
            uiLibraryCompilationService.getLibraryScriptTypes(componentUiFrameworkView,
                scriptType.getName()));

        if (componentVariationRetrievalService != null) {
          try {
            for (ComponentVariation variation :
                componentVariationRetrievalService.getComponentVariations(
                componentUiFrameworkView)) {
              scriptTypes.addAll(uiLibraryCompilationService.getLibraryScriptTypes(variation,
                  scriptType.getName()));
            }
          } catch (ComponentVariationRetrievalException e) {
            // todo log.
          }
        }
      }
    }
    endPerformanceTracking(tracker);
    return scriptTypes;
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
