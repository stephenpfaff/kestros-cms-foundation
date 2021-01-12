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

import io.kestros.cms.uiframeworks.api.models.Theme;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;

/**
 * Compiles CSS and JS for {@link Theme}.
 */
public interface ThemeOutputCompilationService extends ManagedService {

  /**
   * Compiled CSS or JS for a given {@link Theme}.
   *
   * @param theme Theme to get script for.
   * @param scriptType Script type.
   * @param minify Whether to minify the output.
   * @return Compiled CSS or JS for a given {@link Theme}.
   * @throws InvalidResourceTypeException Theme or parent UiFramework was invalid.
   */
  String getThemeOutput(Theme theme, ScriptType scriptType, Boolean minify)
      throws InvalidResourceTypeException;

}
