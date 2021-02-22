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

import static io.kestros.commons.structuredslingmodels.utils.SlingModelUtils.getResourcesAsType;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Baseline logic for {@link VendorLibraryResource} and {@link UiFrameworkResource}.
 */
@SuppressFBWarnings("RI_REDUNDANT_INTERFACES")
public abstract class BaseUiFrameworkLibraryResource extends UiLibraryResource
    implements FrontendLibrary, VersionResource {

  private static final Logger LOG = LoggerFactory.getLogger(BaseUiFrameworkLibraryResource.class);
  private static final String PN_EXTERNALIZED_FILES = "externalizedFiles";

  protected abstract HtlTemplateFileRetrievalService getHtlTemplateFileRetrievalService();

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

  protected List<String> getExternalizedFilesProperty() {
    return Arrays.asList(getProperties().get(PN_EXTERNALIZED_FILES, new String[]{}));
  }

}
