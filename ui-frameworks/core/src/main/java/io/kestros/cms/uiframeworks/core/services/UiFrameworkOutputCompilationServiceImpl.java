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
 * Compiles CSS and JS for {@link UiFramework}.
 */
@Component(immediate = true,
           service = {UiFrameworkOutputCompilationService.class},
           property = "service.ranking:Integer=100")
public class UiFrameworkOutputCompilationServiceImpl
    implements UiFrameworkOutputCompilationService {

  private static final Logger LOG = LoggerFactory.getLogger(
      UiFrameworkOutputCompilationServiceImpl.class);

  private ComponentContext componentContext;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private UiLibraryCompilationService uiLibraryCompilationService;

  @Override
  public String getDisplayName() {
    return "UI Framework Output Compilation Service";
  }

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
  public List<ScriptType> getUiFrameworkScriptTypes(UiFramework uiFramework,
      ScriptType scriptType) {
    List<ScriptType> scriptTypes = new ArrayList<>();
    if (uiLibraryCompilationService != null) {
      for (VendorLibrary vendorLibrary : uiFramework.getVendorLibraries()) {
        for (ScriptType libraryScriptType : uiLibraryCompilationService.getLibraryScriptTypes(
            vendorLibrary, scriptType.getName())) {
          if (!scriptTypes.contains(libraryScriptType)) {
            scriptTypes.add(libraryScriptType);
          }
        }
      }

      for (ScriptType libraryScriptType : uiLibraryCompilationService.getLibraryScriptTypes(
          uiFramework, scriptType.getName())) {
        if (!scriptTypes.contains(libraryScriptType)) {
          scriptTypes.add(libraryScriptType);
        }
      }

      for (UiFrameworkCompilationAddonService addonService : getAddonServices()) {
        try {
          for (ScriptType libraryScriptType : addonService.getAddonScriptTypes(uiFramework,
              scriptType)) {
            if (!scriptTypes.contains(libraryScriptType)) {
              scriptTypes.add(libraryScriptType);
            }
          }
        } catch (InvalidResourceTypeException e) {
          LOG.warn(
              String.format("Unable to append UiFramework %s for service %s.", scriptType.getName(),
                  addonService.getDisplayName()));
        }
      }
    }

    return scriptTypes;
  }

  @Override
  public String getUiFrameworkSource(UiFramework uiFramework, ScriptType scriptType)
      throws NoMatchingCompilerException {
    final StringBuilder uiFrameworkSource = new StringBuilder();

    for (VendorLibrary vendorLibrary : uiFramework.getVendorLibraries()) {
      try {
        if (StringUtils.isNotEmpty(uiFrameworkSource.toString())) {
          uiFrameworkSource.append("\n");
        }
        uiFrameworkSource.append(
            uiLibraryCompilationService.getUiLibrarySource(vendorLibrary, scriptType));
      } catch (InvalidResourceTypeException e) {
        LOG.warn(e.getMessage());
      }
    }
    if (uiLibraryCompilationService != null) {
      try {
        String uiFrameworkOutput = uiLibraryCompilationService.getUiLibrarySource(uiFramework,
            scriptType);
        if (StringUtils.isNotEmpty(uiFrameworkOutput) && !uiFrameworkSource.toString().isEmpty()) {
          uiFrameworkSource.append("\n");
        }
        uiFrameworkSource.append(uiFrameworkOutput);
      } catch (InvalidResourceTypeException e) {
        LOG.warn(e.getMessage());
      }
    }

    for (UiFrameworkCompilationAddonService addonService : getAddonServices()) {
      try {
        String addonOutput = addonService.getAppendedOutput(uiFramework, scriptType);
        if (StringUtils.isNotEmpty(addonOutput) && !uiFrameworkSource.toString().isEmpty()) {
          uiFrameworkSource.append("\n");
        }
        uiFrameworkSource.append(addonOutput);
      } catch (InvalidResourceTypeException e) {
        LOG.warn(
            String.format("Unable to append UiFramework %s for service %s.", scriptType.getName(),
                addonService.getDisplayName()));
      }
    }

    return uiFrameworkSource.toString();
  }

  @Override
  @Nonnull
  public List<UiFrameworkCompilationAddonService> getAddonServices() {
    return OsgiServiceUtils.getAllOsgiServicesOfType(componentContext,
        UiFrameworkCompilationAddonService.class);
  }
}
