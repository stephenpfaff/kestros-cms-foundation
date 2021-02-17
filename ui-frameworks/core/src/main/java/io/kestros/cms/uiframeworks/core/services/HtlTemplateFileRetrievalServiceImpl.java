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

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildrenOfFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;

import io.kestros.cms.performanceservices.api.services.PerformanceService;
import io.kestros.cms.performanceservices.api.services.PerformanceTrackerService;
import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.core.models.HtlTemplateFileResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
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
 * Retrieves {@link HtlTemplateFileResource} objects.
 */
@Component(immediate = true,
           service = HtlTemplateFileRetrievalService.class)
public class HtlTemplateFileRetrievalServiceImpl
    implements HtlTemplateFileRetrievalService, PerformanceService {

  private static Logger LOG = LoggerFactory.getLogger(HtlTemplateFileRetrievalServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public List<HtlTemplateFile> getHtlTemplatesFromUiFramework(UiFramework uiFramework) {
    String tracker = startPerformanceTracking();
    List<HtlTemplateFile> htlTemplateFileList = new ArrayList<>();
    for (VendorLibrary vendorLibrary : uiFramework.getVendorLibraries()) {
      try {
        htlTemplateFileList.addAll(getHtlTemplatesFromVendorLibrary(vendorLibrary));
      } catch (HtlTemplateFileRetrievalException e) {
        LOG.debug(e.getMessage());
      }
    }

    try {
      BaseResource templatesFolderResource = getChildAsBaseResource("templates",
          uiFramework.getResource());
      htlTemplateFileList.addAll(
          getChildrenOfFileType(templatesFolderResource, HtlTemplateFileResource.class));
    } catch (ChildResourceNotFoundException e) {
      //      throw new HtlTemplateFileRetrievalException(
      //          String.format("Failed to retrieve HTL Template for %s %s. Templates folder not 
      //          found.",
      //              library.getClass().getSimpleName(), library.getPath()));
    }
    endPerformanceTracking(tracker);
    return htlTemplateFileList;
  }

  @Override
  public List<HtlTemplateFile> getHtlTemplatesFromVendorLibrary(VendorLibrary vendorLibrary)
      throws HtlTemplateFileRetrievalException {
    String tracker = startPerformanceTracking();
    List<HtlTemplateFile> htlTemplateFileList = new ArrayList<>();
    try {
      BaseResource templatesFolderResource = getChildAsBaseResource("templates",
          vendorLibrary.getResource());
      htlTemplateFileList.addAll(
          getChildrenOfFileType(templatesFolderResource, HtlTemplateFileResource.class));
    } catch (ChildResourceNotFoundException e) {
      endPerformanceTracking(tracker);
      throw new HtlTemplateFileRetrievalException(
          String.format("Failed to retrieve HTL Template for %s %s. Templates folder not found.",
              vendorLibrary.getClass().getSimpleName(), vendorLibrary.getPath()));
    }
    endPerformanceTracking(tracker);
    return htlTemplateFileList;
  }

  @Override
  public String getDisplayName() {
    return "HTL Template File Retrieval Service";
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
