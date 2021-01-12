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

package io.kestros.cms.uiframeworks.api.services;

import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import java.util.List;

/**
 * Compiles CSS and JS for {@link UiFramework}.
 */
public interface UiFrameworkOutputCompilationService extends ManagedService {

  /**
   * Compiled CSS or JS for a given {@link UiFramework}.
   *
   * @param uiFramework UiFramework to get script for.
   * @param scriptType Script type.
   * @return Compiled CSS or JS for a given {@link UiFramework}.
   */
  String getUiFrameworkOutput(UiFramework uiFramework, ScriptType scriptType);

  /**
   * List of services which can add on CSS or JS to compiled output.
   *
   * @return List of services which can add on CSS or JS to compiled output.
   */
  List<UiFrameworkCompilationAddonService> getAddonServices();

}
