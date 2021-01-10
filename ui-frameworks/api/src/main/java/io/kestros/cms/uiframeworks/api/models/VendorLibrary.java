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

package io.kestros.cms.uiframeworks.api.models;

import static io.kestros.cms.uiframeworks.api.utils.DesignUtils.getAllVendorLibraries;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildAsBaseResource;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getChildrenOfType;
import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourcesAsType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.modeltypes.ThirdPartyResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import io.kestros.commons.structuredslingmodels.exceptions.ChildResourceNotFoundException;
import io.kestros.commons.structuredslingmodels.exceptions.ResourceNotFoundException;
import io.kestros.commons.uilibraries.UiLibrary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modular UI Libraries containing CSS, JS, assets and HTL templates, which can be compiled into any
 * number of UiFrameworks.
 */
@KestrosModel(docPaths = {"/content/guide-articles/kestros/ui-frameworks/vendor-libraries",
    "/content/guide-articles/kestros/ui-frameworks/assigning-htl-templates",
    "/content/guide-articles/kestros/ui-frameworks/ui-frameworks",
    "/content/guide-articles/kestros/ui-frameworks/themes"})
@Model(adaptables = Resource.class,
       resourceType = {"kes:VendorLibrary"})
public class VendorLibrary extends UiLibrary implements ThirdPartyResource, IconResource {

  private static final Logger LOG = LoggerFactory.getLogger(VendorLibrary.class);

  private static final String PN_EXTERNALIZED_FILES = "externalizedFiles";

  @Override
  public List<UiLibrary> getDependencies() {
    final List<UiLibrary> dependencies = new ArrayList<>();

    for (final String dependencyPath : getDependencyPaths()) {
      try {
        for (VendorLibrary vendorLibrary : getAllVendorLibraries(getResourceResolver(), true,
            true)) {
          if (vendorLibrary.getName().equals(dependencyPath) || vendorLibrary.getPath().equals(
              dependencyPath)) {
            dependencies.add(vendorLibrary);
          }
        }
      } catch (ResourceNotFoundException e) {
        LOG.warn("Unable to find vendor libraries root resource. {}.", e.getMessage());
      }
    }

    return dependencies;
  }

  /**
   * Documentation url for the current VendorLibrary.
   *
   * @return Documentation url for the current VendorLibrary.
   */
  @Override
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
      final BaseResource templatesRootResource = getChildAsBaseResource("templates", this);
      htlTemplateFiles.addAll(getChildrenOfType(templatesRootResource, HtlTemplateFile.class));
    } catch (final ChildResourceNotFoundException exception) {
      LOG.debug("Unable to find template files for {} due to missing templates directory.",
          getPath());
    }
    htlTemplateFiles.sort(Comparator.comparing(HtlTemplateFile::getTitle));
    return htlTemplateFiles;
  }

  /**
   * List of CDN JavaScript script file URL that need to be included as their own script files.
   *
   * @return List of CDN JavaScript script file URL that need to be included as their own script
   *     files.
   */
  public List<String> getIncludedCdnJsScripts() {
    return Arrays.asList(getProperty("includedCdnJsScripts", new String[]{}));
  }

  /**
   * List of CDN CSS script file URL that need to be included as their own script files.
   *
   * @return List of CDN CSS script file URL that need to be included as their own script files.
   */
  public List<String> getIncludedCdnCssScripts() {
    return Arrays.asList(getProperty("includedCdnCssScripts", new String[]{}));
  }

  /**
   * List of files that should be externalized (fonts, images, etc).
   *
   * @return List of files that should be externalized (fonts, images, etc).
   */
  @Nonnull
  public List<BaseResource> getExternalizedFiles() {
    return getResourcesAsType(getExternalizedFilesProperty(), getResourceResolver(),
        BaseResource.class);
  }

  private List<String> getExternalizedFilesProperty() {
    return Arrays.asList(getProperties().get(PN_EXTERNALIZED_FILES, new String[]{}));
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
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
}