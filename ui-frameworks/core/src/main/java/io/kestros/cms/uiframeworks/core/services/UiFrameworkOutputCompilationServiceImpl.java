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

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkCompilationAddonService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkOutputCompilationService;
import io.kestros.commons.osgiserviceutils.utils.OsgiServiceUtils;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import io.kestros.commons.uilibraries.services.compilation.UiLibraryCompilationService;
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
 * Compiles CSS and JS for {@link UiFramework}.
 */
@Component(immediate = true,
           service = {UiFrameworkOutputCompilationService.class},
           property = "service.ranking:Integer=100")
public class UiFrameworkOutputCompilationServiceImpl
    implements UiFrameworkOutputCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      UiFrameworkOutputCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @Override
  public String getDisplayName() {
    return "UI Framework Output Compilation Service";
  }

  private ComponentContext componentContext;

  @Override
  public void activate(ComponentContext componentContext) {
    this.componentContext = componentContext;
  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {

  }

  @Override
  public String getUiFrameworkOutput(UiFramework uiFramework, ScriptType scriptType) {
    final StringBuilder output = new StringBuilder();

    for (VendorLibrary vendorLibrary : uiFramework.getVendorLibraries()) {
      try {
        if (StringUtils.isNotEmpty(output.toString())) {
          output.append("\n");
        }
        output.append(vendorLibrary.getOutput(scriptType, false));
      } catch (InvalidResourceTypeException e) {
        LOG.warn(e.getMessage());
      }
    }
    if (uiLibraryCompilationService != null) {

      try {
        String uiFrameworkOutput = uiLibraryCompilationService.getUiLibraryOutput(uiFramework,
            scriptType, false);
        if (StringUtils.isNotEmpty(uiFrameworkOutput) && !output.toString().isEmpty()) {
          output.append("\n");
        }
        output.append(uiFrameworkOutput);
      } catch (InvalidResourceTypeException e) {
        LOG.warn(e.getMessage());
      }
    }

    for (UiFrameworkCompilationAddonService addonService : getAddonServices()) {
      try {
        String addonOutput = addonService.getAppendedOutput(uiFramework, scriptType);
        if (StringUtils.isNotEmpty(addonOutput) && !output.toString().isEmpty()) {
          output.append("\n");
        }
        output.append(addonOutput);
      } catch (InvalidResourceTypeException e) {
        LOG.warn(
            String.format("Unable to append UiFramework %s for service %s.", scriptType.getName(),
                addonService.getDisplayName()));
      }
    }
    return output.toString();
  }

  @Override
  @Nonnull
  public List<UiFrameworkCompilationAddonService> getAddonServices() {
    return OsgiServiceUtils.getAllOsgiServicesOfType(componentContext,
        UiFrameworkCompilationAddonService.class);
  }
}
