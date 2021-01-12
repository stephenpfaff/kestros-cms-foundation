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

import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentVariation;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewService;
import io.kestros.cms.componenttypes.api.services.ComponentViewUiFrameworkOutputCompilationService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkCompilationAddonService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Appends {@link ComponentUiFrameworkView} and {@link ComponentVariation} CSS and JS to a
 * UiFramework's compiled output.
 */
@Component(immediate = true,
           service = {UiFrameworkCompilationAddonService.class,
               ComponentViewUiFrameworkOutputCompilationService.class})
public class ComponentViewUiFrameworkOutputCompilationServiceImpl
    implements ComponentViewUiFrameworkOutputCompilationService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentUiFrameworkViewService componentUiFrameworkViewService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

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
    final StringBuilder output = new StringBuilder();

    if (componentUiFrameworkViewService != null) {
      for (final ComponentUiFrameworkView componentUiFrameworkView :
          componentUiFrameworkViewService.getComponentViews(
          uiFramework)) {
        String viewOutput = uiLibraryCompilationService.getUiLibraryOutput(componentUiFrameworkView,
            scriptType, false);
        if (StringUtils.isNotEmpty(viewOutput) && StringUtils.isNotEmpty(output.toString())) {
          output.append("\n");
        }
        output.append(viewOutput);
        for (ComponentVariation variation : componentUiFrameworkView.getVariations()) {
          String variationOutput = variation.getOutput(scriptType, false);
          if (StringUtils.isNotEmpty(variationOutput) && StringUtils.isNotEmpty(
              output.toString())) {
            output.append("\n");
          }
          output.append(variationOutput);
        }
      }
    }
    return output.toString();
  }
}
