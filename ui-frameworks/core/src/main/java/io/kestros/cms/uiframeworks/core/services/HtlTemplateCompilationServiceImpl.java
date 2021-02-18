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
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateCompilationService;
import java.io.IOException;
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
 * Compiles a {@link UiFramework} HTL Template files into a single file.
 */
@Component(immediate = true,
           service = HtlTemplateCompilationService.class)
public class HtlTemplateCompilationServiceImpl
    implements HtlTemplateCompilationService, PerformanceService {

  private static final Logger LOG = LoggerFactory.getLogger(
      HtlTemplateCompilationServiceImpl.class);

  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
             policyOption = ReferencePolicyOption.GREEDY)
  private PerformanceTrackerService performanceTrackerService;

  @Override
  public String getDisplayName() {
    return "HTL Template Compilation Service";
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
  public String getCompiledHtlTemplateFileOutput(List<HtlTemplateFile> htlTemplateFileList)
      throws IOException {
    String tracker = startPerformanceTracking();
    StringBuilder compilationOutput = new StringBuilder();
    for (HtlTemplateFile htlTemplateFile : htlTemplateFileList) {
      if (htlTemplateFile != null) {
        compilationOutput.append(htlTemplateFile.getFileContent());
        compilationOutput.append("\n");
      }
    }
    endPerformanceTracking(tracker);
    return compilationOutput.toString();
  }

  @Override
  public PerformanceTrackerService getPerformanceTrackerService() {
    return performanceTrackerService;
  }
}
