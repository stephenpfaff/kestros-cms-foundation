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

package io.kestros.cms.uiframeworks.refactored.services;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.getChildrenOfFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;

import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.refactored.models.HtlTemplateFileResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import java.util.ArrayList;
import java.util.List;
import org.apache.felix.hc.api.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieves {@link HtlTemplateFileResource} objects.
 */
@Component(immediate = true,
           service = HtlTemplateFileRetrievalService.class)
public class HtlTemplateFileRetrievalServiceImpl implements HtlTemplateFileRetrievalService {

  private static Logger LOG = LoggerFactory.getLogger(HtlTemplateFileRetrievalServiceImpl.class);

  @Override
  public List<HtlTemplateFile> getHtlTemplates(FrontendLibrary library)
      throws HtlTemplateFileRetrievalException {
    List<HtlTemplateFile> htlTemplateFileList = new ArrayList<>();
    try {
      BaseResource templatesFolderResource = getChildAsBaseResource("templates",
          library.getResource());
      htlTemplateFileList.addAll(
          getChildrenOfFileType(templatesFolderResource, HtlTemplateFileResource.class));
      return htlTemplateFileList;
    } catch (ChildResourceNotFoundException e) {
      throw new HtlTemplateFileRetrievalException(
          String.format("Failed to retrieve HTL Template for %s %s. Templates folder not found.",
              library.getClass().getSimpleName(), library.getPath()));
    }
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
}
