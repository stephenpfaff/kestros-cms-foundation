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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getAllDescendantsOfType;

import io.kestros.cms.componenttypes.api.exceptions.ComponentVariationRetrievalException;
import io.kestros.cms.componenttypes.api.models.ComponentUiFrameworkView;
import io.kestros.cms.componenttypes.api.models.ComponentVariation;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.cms.componenttypes.core.models.ComponentUiFrameworkViewResource;
import io.kestros.cms.componenttypes.core.models.ComponentVariationResource;
import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves ComponentVariationResource objects.
 */
@Component(immediate = true,
           service = ComponentVariationRetrievalService.class)
public class ComponentVariationRetrievalServiceImpl
    implements ComponentVariationRetrievalService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      ComponentVariationRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public List<ComponentVariation> getComponentVariations(
      ComponentUiFrameworkView componentUiFrameworkView)
      throws ComponentVariationRetrievalException {
    String tracker = startPerformanceTracking();
    List<ComponentVariation> variationsList = new ArrayList<>();
    if (componentUiFrameworkView instanceof ComponentUiFrameworkViewResource) {
      try {
        BaseResource variationsFolder = SlingModelUtils.getChildAsBaseResource("variations",
            (ComponentUiFrameworkViewResource) componentUiFrameworkView);
        variationsList.addAll(
            getAllDescendantsOfType(variationsFolder, ComponentVariationResource.class));
      } catch (ChildResourceNotFoundException e) {
        endPerformanceTracking(tracker);
        throw new ComponentVariationRetrievalException(componentUiFrameworkView.getPath(),
            e.getMessage());
      }
    } else {
      endPerformanceTracking(tracker);
      throw new ComponentVariationRetrievalException(componentUiFrameworkView.getPath(),
          "Component view was not of type ComponentUiFrameworkViewResource.");
    }
    endPerformanceTracking(tracker);
    return variationsList;
  }

  @Override
  public String getDisplayName() {
    return "Component Variation Retrieval Service";
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

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
