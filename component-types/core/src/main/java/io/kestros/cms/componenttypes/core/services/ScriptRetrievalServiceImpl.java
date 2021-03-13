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

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentUiFrameworkViewException;
import io.kestros.cms.componenttypes.api.exceptions.ScriptRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentType;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ScriptRetrievalService;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.structuredslingmodels.utils.FileModelUtils;
import javax.annotation.Nonnull;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Retrieves HTL scripts for a given {@link ComponentUiFrameworkView}.
 */
@Component(immediate = true,
           service = ScriptRetrievalService.class)
public class ScriptRetrievalServiceImpl implements ScriptRetrievalService, PerformanceService {

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private ComponentUiFrameworkViewRetrievalService componentUiFrameworkViewRetrievalService;

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Nonnull
  @Override
  public HtmlFile getContentScript(@Nonnull ComponentType componentType,
      @Nonnull UiFramework uiFramework)
      throws InvalidComponentTypeException, ScriptRetrievalException,
             InvalidComponentUiFrameworkViewException {
    return getScript("content.html", componentType, uiFramework);
  }

  @Nonnull
  @Override
  public HtmlFile getScript(@Nonnull String scriptName, @Nonnull ComponentType componentType,
      @Nonnull UiFramework uiFramework)
      throws InvalidComponentUiFrameworkViewException, InvalidComponentTypeException,
             ScriptRetrievalException {
    String tracker = startPerformanceTracking();
    if (componentUiFrameworkViewRetrievalService != null) {
      ComponentUiFrameworkView view
          = componentUiFrameworkViewRetrievalService.getComponentUiFrameworkViewWithFallback(
          componentType, uiFramework);
      if (view instanceof ComponentUiFrameworkViewResource) {
        ComponentUiFrameworkViewResource viewResource = (ComponentUiFrameworkViewResource) view;
        try {
          return FileModelUtils.getChildAsFileType(scriptName, viewResource, HtmlFile.class);
        } catch (ChildResourceNotFoundException e) {
          ComponentType superType = componentType.getComponentSuperType();
          if (superType != null) {
            endPerformanceTracking(tracker);
            return getScript(scriptName, superType, uiFramework);
          }
        } catch (InvalidResourceTypeException e) {
          e.printStackTrace();
        }
      }

    }
    endPerformanceTracking(tracker);
    throw new ScriptRetrievalException(
        String.format("Failed to retrieve script %s for componentType %s, uiFramework %s.",
            scriptName, componentType.getPath(), uiFramework.getPath()));
  }

  @Override
  public String getDisplayName() {
    return "Script Retrieval Service";
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
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
