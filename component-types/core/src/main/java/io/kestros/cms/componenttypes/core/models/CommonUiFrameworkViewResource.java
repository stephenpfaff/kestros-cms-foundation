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

package io.kestros.cms.componenttypes.core.models;

import io.kestros.cms.componenttypes.api.models.CommonUiFrameworkView;
import io.kestros.cms.versioning.api.exceptions.VersionFormatException;
import io.kestros.cms.versioning.api.models.Version;
import io.kestros.cms.versioning.api.models.VersionableResource;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sling Model for {@link CommonUiFrameworkView}.
 */
@Model(adaptables = Resource.class)
public class CommonUiFrameworkViewResource extends ComponentUiFrameworkViewResource
    implements CommonUiFrameworkView {

  @Override
  public String getTitle() {
    return "Common";
  }

  @Override
  public String getFontAwesomeIcon() {
    return "fas fa-palette";
  }

  @Override
  public Class getManagingResourceType() {
    return null;
  }

  @Override
  public Boolean isDeprecated() {
    return Boolean.FALSE;
  }

  @Override
  public Version getVersion() throws VersionFormatException {
    return null;
  }

  @Override
  public VersionableResource getRootResource() throws NoValidAncestorException {
    return null;
  }
}
