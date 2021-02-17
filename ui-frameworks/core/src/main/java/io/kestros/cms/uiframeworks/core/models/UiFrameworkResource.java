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

import static io.kestros.cms.uiframeworks.core.DesignConstants.PN_UI_FRAMEWORK_CODE;
import static io.kestros.cms.uiframeworks.core.DesignConstants.PN_VENDOR_LIBRARIES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.models.VendorLibrary;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateCacheService;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.api.services.ThemeRetrievalService;
import io.kestros.cms.uiframeworks.api.services.VendorLibraryRetrievalService;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * Sling Model for {@link UiFramework}.
 */
@Model(adaptables = Resource.class,
       resourceType = "kes:UiFramework")
public class UiFrameworkResource extends BaseUiFrameworkLibraryResource implements UiFramework {

  private static final Logger LOG = LoggerFactory.getLogger(UiFrameworkResource.class);

  @OSGiService
  @Optional
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @OSGiService
  @Optional
  private VendorLibraryRetrievalService vendorLibraryRetrievalService;

  @OSGiService
  @Optional
  private ThemeRetrievalService themeRetrievalService;

  @OSGiService
  @Optional
  private HtlTemplateCacheService htlTemplateCacheService;

  @Override
  public String getFrameworkCode() {
    VersionableResource versionableResource = null;
    try {
      versionableResource = getRootResource();
    } catch (NoValidAncestorException e) {
      LOG.trace("Could not find versionable resource for UiFramework {}.", getPath());
    }
    if (versionableResource != null && versionableResource instanceof ManagedUiFrameworkResource) {
      return ((ManagedUiFrameworkResource) versionableResource).getFrameworkCode();
    }
    return getProperty(PN_UI_FRAMEWORK_CODE, StringUtils.EMPTY);
  }

  @Override
  public List<VendorLibrary> getVendorLibraries() {
    List<VendorLibrary> vendorLibraryList = new ArrayList<>();

    if (vendorLibraryRetrievalService != null) {
      for (String vendorLibrary : getIncludedVendorLibraryNames()) {
        try {
          vendorLibraryList.add(vendorLibraryRetrievalService.getVendorLibrary(vendorLibrary,
              isIncludeEtcVendorLibraries(), isIncludeLibsVendorLibraries()));
        } catch (Exception e) {
          LOG.warn("Unable to retrieve vendor library {} for UiFramework {}. {}", vendorLibrary,
              getPath(), e.getMessage());
        }
      }
    }
    return vendorLibraryList;
  }

  @Override
  public List<Theme> getThemes() {
    if (themeRetrievalService != null) {
      return themeRetrievalService.getThemes(this);
    }
    return Collections.emptyList();
  }

  @Override
  public Boolean isIncludeEtcVendorLibraries() {
    return getProperty("includeEtcVendorLibraries", Boolean.TRUE);
  }

  @Override
  public Boolean isIncludeLibsVendorLibraries() {
    return getProperty("includeLibsVendorLibraries", Boolean.FALSE);
  }

  @Override
  public String getTemplatesPath() {
    try {
      return htlTemplateCacheService.getCompiledTemplateFilePath(this);
    } catch (ResourceNotFoundException e) {
      LOG.error(e.getMessage());
    }
    return StringUtils.EMPTY;
  }

  @Override
  @JsonIgnore
  @KestrosProperty(description = "Font awesome icon class, used in the Kestros Site Admin UI",
                   jcrPropertyName = "fontAwesomeIcon",
                   defaultValue = "fas fa-palette",
                   configurable = true,
                   sampleValue = "fas fa-palette")
  public String getFontAwesomeIcon() {
    VersionableResource versionableResource = null;
    try {
      versionableResource = getRootResource();
    } catch (NoValidAncestorException e) {
      LOG.trace("Could not find versionable resource for UiFramework {}.", getPath());
    }
    if (versionableResource != null && versionableResource instanceof ManagedUiFrameworkResource) {
      return ((ManagedUiFrameworkResource) versionableResource).getFontAwesomeIcon();
    }
    return getProperty("fontAwesomeIcon", "fas fa-palette");
  }

  @Override
  public Class getManagingResourceType() {
    return ManagedUiFrameworkResource.class;
  }

  @Override
  public List<HtlTemplateFile> getTemplateFiles() {
    final List<HtlTemplateFile> htlTemplateFiles = new ArrayList<>();
    for (VendorLibrary vendorLibrary : getVendorLibraries()) {
      htlTemplateFiles.addAll(vendorLibrary.getTemplateFiles());
    }
    htlTemplateFiles.addAll(
        getHtlTemplateFileRetrievalService().getHtlTemplatesFromUiFramework(this));
    htlTemplateFiles.sort(Comparator.comparing(HtlTemplateFile::getTitle));

    return htlTemplateFiles;
  }

  @Override
  protected HtlTemplateFileRetrievalService getHtlTemplateFileRetrievalService() {
    return htlTemplateFileRetrievalService;
  }

  /**
   * Vendor Libraries that are to be compiled.
   *
   * @return Vendor Libraries that are to be compiled.
   */
  @Nonnull
  @KestrosProperty(description = "Vendor libraries to compile in the UiFramework.",
                   jcrPropertyName = PN_VENDOR_LIBRARIES,
                   defaultValue = "[]",
                   configurable = true)
  public List<String> getIncludedVendorLibraryNames() {
    return Arrays.asList(getProperties().get(PN_VENDOR_LIBRARIES, new String[]{}));
  }
}
