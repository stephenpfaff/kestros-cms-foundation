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
import io.kestros.cms.versioning.api.models.VersionResource;
import io.kestros.commons.structuredslingmodels.BaseResource;
import io.kestros.commons.uilibraries.api.models.FrontendLibrary;
import java.util.List;

/**
 * Frontend Library which compiles any number of VendorLibraries. Can also include its own css/js
 * files.
 */
public interface UiFramework extends FrontendLibrary, IconResource, VersionResource {

  /**
   * UI Framework Code. Used when resolving component requests.
   *
   * @return UI Framework Code.
   */
  String getFrameworkCode();

  /**
   * All {@link VendorLibrary} instances are compiled into the UiFramework.
   *
   * @return All {@link VendorLibrary} instances are compiled into the UiFramework.
   */
  List<VendorLibrary> getVendorLibraries();

  /**
   * All child {@link Theme} instances.
   *
   * @return All child {@link Theme} instances.
   */
  List<Theme> getThemes();

  /**
   * Whether the framework will look into /etc when building its VendorLibrary list.
   *
   * @return Whether the framework will look into /etc when building its VendorLibrary list.
   */
  Boolean isIncludeEtcVendorLibraries();

  /**
   * Whether the framework will look into /libs when building its VendorLibrary list.
   *
   * @return Whether the framework will look into /libs when building its VendorLibrary list.
   */
  Boolean isIncludeLibsVendorLibraries();

  /**
   * Path to compiled HTL Templates file.
   *
   * @return Path to compiled HTL Templates file.
   */
  String getTemplatesPath();

  /**
   * HTL Template Files associated to the current UiFramework.
   *
   * @return HTL Template Files associated to the current UiFramework.
   */
  List<HtlTemplateFile> getTemplateFiles();

  /**
   * List files to externalize. (assets, icons, fonts, etc).
   *
   * @return List files to externalize. (assets, icons, fonts, etc).
   */
  List<BaseResource> getExternalizedFiles();

}

