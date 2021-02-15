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

import io.kestros.cms.componenttypes.api.models.ManagedComponentUiFrameworkView;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sling Model for {@link ManagedComponentUiFrameworkView}.
 */
@Model(adaptables = Resource.class)
public class ManagedComponentUiFrameworkViewResource extends BaseResource
    implements ManagedComponentUiFrameworkView {

  @Override
  public String getFontAwesomeIcon() {
    return null;
  }

  @Override
  public Class getVersionResourceType() {
    return ComponentUiFrameworkViewResource.class;
  }

  @Override
  public List getVersions() {
    return null;
  }

  @Override
  public VersionResource getCurrentVersion() {
    return null;
  }
}
