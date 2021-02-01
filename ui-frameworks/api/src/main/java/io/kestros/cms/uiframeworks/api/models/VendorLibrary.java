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

import io.kestros.cms.modeltypes.IconResource;
import io.kestros.cms.modeltypes.ThirdPartyResource;
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import java.util.List;

/**
 * Modular frontend library. Compiled within {@link UiFramework} requests.
 */
public interface VendorLibrary
    extends FrontendLibrary, ThirdPartyResource, IconResource, VersionResource {

  /**
   * HTL Template files contained within the library.
   *
   * @return HTL Template files contained within the library.
   */
  List<HtlTemplateFile> getTemplateFiles();

  /**
   * Additional JavaScript endpoints to be included into an implementing page's source code.
   *
   * @return Additional JavaScript endpoints to be included into an implementing page's source code.
   */
  List<String> getIncludedCdnJsScripts();

  /**
   * Additional CSS endpoints to be included into an implementing page's source code.
   *
   * @return Additional JavaScript endpoints to be included into an implementing page's source code.
   */
  List<String> getIncludedCdnCssScripts();

  /**
   * List files to externalize. (assets, icons, fonts, etc).
   *
   * @return List files to externalize. (assets, icons, fonts, etc).
   */
  List<BaseResource> getExternalizedFiles();

}
