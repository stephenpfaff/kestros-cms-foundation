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
import io.kestros.cms.uiframeworks.api.models.ManagedVendorLibrary;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.cms.versioning.api.services.VersionService;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Sling Model for {@link ManagedVendorLibrary}.
 */
@Model(adaptables = Resource.class,
       resourceType = "kes:ManagedVendorLibrary")
public class ManagedVendorLibraryResource extends BaseResource implements ManagedVendorLibrary {

  @OSGiService
  @Optional
  private VersionService versionService;

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

  @Override
  public Class getVersionResourceType() {
    return VendorLibraryResource.class;
  }

  @Override
  public List getVersions() {
    if (versionService != null) {
      return versionService.getVersionHistory(this);
    }
    return Collections.emptyList();
  }

  @Override
  public VersionResource getCurrentVersion() {
    if (versionService != null) {
      return versionService.getCurrentVersion(this);
    }
    return null;
  }
}
