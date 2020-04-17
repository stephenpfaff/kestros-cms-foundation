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

package io.kestros.cms.foundation.design.vendorlibrary;

import static io.kestros.commons.structuredslingmodels.utils.FileModelUtils.adaptToFileType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenAsBaseResource;

import io.kestros.cms.foundation.design.htltemplate.HtlTemplate;
import io.kestros.cms.foundation.design.htltemplate.HtlTemplateFile;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.UiLibrary;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone UiLibraries, which can be compiled inside of a UiFramework.
 */
@KestrosModel(validationService = VendorLibraryValidationService.class,
              docPaths = {
                     "/content/guide-articles/kestros/ui-frameworks/create-a-new-vendor-library",
                     "/content/guide-articles/kestros/ui-frameworks/create-a-new-ui-framework"})
@Model(adaptables = Resource.class,
       resourceType = {"kes:VendorLibrary"})
@Exporter(name = "jackson",
          selector = "vendor-library",
          extensions = "json")
public class VendorLibrary extends UiLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(VendorLibrary.class);

  /**
   * Documentation url for the current VendorLibrary.
   *
   * @return Documentation url for the current VendorLibrary.
   */
  public String getDocumentationUrl() {
    return getProperties().get("documentationUrl", StringUtils.EMPTY);
  }

  /**
   * List of HTL Templates associated to the current Vendor Library.
   *
   * @return List of HTL Templates associated to the current Vendor Library.
   */
  public List<HtlTemplate> getTemplates() {
    final List<HtlTemplate> templates = new ArrayList<>();

    for (final HtlTemplateFile htlTemplateFile : getTemplateFiles()) {
      templates.addAll(htlTemplateFile.getTemplates());
    }

    return templates;
  }

  /**
   * HTL Template Files associated to the current Vendor Library.
   *
   * @return HTL Template Files associated to the current Vendor Library.
   */
  public List<HtlTemplateFile> getTemplateFiles() {
    final List<HtlTemplateFile> htlTemplateFiles = new ArrayList<>();
    try {
      final BaseResource templatesRootResource = getChildAsBaseResource("templates", this);
      for (final BaseResource templateFileResource : getChildrenAsBaseResource(
          templatesRootResource)) {
        try {
          htlTemplateFiles.add(adaptToFileType(templateFileResource, HtlTemplateFile.class));
        } catch (final InvalidResourceTypeException e) {
          LOG.warn("Could not add file {} to HtlTemplate. {}", templateFileResource.getPath(),
              e.getMessage());
        }
      }
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("Unable to find template files for {} due to missing templates directory.",
          getPath());
    }
    return htlTemplateFiles;
  }
}
