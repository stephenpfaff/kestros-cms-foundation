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

package io.kestros.cms.uiframeworks.refactored.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.commons.uilibraries.core.UiLibraryResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model for {@link Theme}.
 */
@Model(adaptables = Resource.class,
       resourceType = "kes:Theme")
public class ThemeResource extends UiLibraryResource implements Theme {

  private static final Logger LOG = LoggerFactory.getLogger(ThemeResource.class);

  @OSGiService
  @Optional
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @Override
  public UiFramework getUiFramework() {
    if (uiFrameworkRetrievalService != null) {
      try {
        return uiFrameworkRetrievalService.getUiFramework(this);
      } catch (UiFrameworkRetrievalException e) {
        LOG.warn(e.getMessage());
      }
    }
    return null;
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  @JsonIgnore
  public String getFontAwesomeIcon() {
    return getProperty("fontAwesomeIcon", "fas fa-paint-brush");
  }

}