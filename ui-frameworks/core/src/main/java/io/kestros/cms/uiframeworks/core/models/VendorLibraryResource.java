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

package io.kestros.cms.uiframeworks.core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.uiframeworks.api.exceptions.HtlTemplateFileRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for {@link VendorLibrary}.
 */
@KestrosModel
@Model(adaptables = Resource.class,
       resourceType = "kes:VendorLibrary")
public class VendorLibraryResource extends BaseUiFrameworkLibraryResource implements VendorLibrary {

  private static final Logger LOG = LoggerFactory.getLogger(VendorLibraryResource.class);

  @OSGiService
  @Optional
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @Override
  protected HtlTemplateFileRetrievalService getHtlTemplateFileRetrievalService() {
    return htlTemplateFileRetrievalService;
  }

  @Override
  @JsonIgnore
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site" + " Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fas fa-shapes",
                   configurable = true,
                   sampleValue = "fas fa-shapes")
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fas fa-shapes");
  }

  @Override
  @JsonIgnore
  @KestrosProperty(description = "URL for third part documentation.",
                   jcrPropertyName = "documentationUrl",
                   defaultValue = "",
                   configurable = true,
                   sampleValue = "https://kestros.io")
  public String getDocumentationUrl() {
    return getProperties().get("documentationUrl", StringUtils.EMPTY);
  }


  /**
   * HTL Template Files associated to the current Vendor Library.
   *
   * @return HTL Template Files associated to the current Vendor Library.
   */
  @Nonnull
  public List<HtlTemplateFile> getTemplateFiles() {
    final List<HtlTemplateFile> htlTemplateFiles = new ArrayList<>();
    try {
      htlTemplateFiles.addAll(
          getHtlTemplateFileRetrievalService().getHtlTemplatesFromVendorLibrary(this));
      htlTemplateFiles.sort(Comparator.comparing(HtlTemplateFile::getTitle));
    } catch (HtlTemplateFileRetrievalException e) {
      LOG.warn(e.getMessage());
    }
    return htlTemplateFiles;
  }

  @Override
  public Class getManagingResourceType() {
    return ManagedVendorLibraryResource.class;
  }
}