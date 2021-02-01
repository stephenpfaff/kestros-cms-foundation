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
import io.kestros.commons.uilibraries.api.exceptions.NoMatchingCompilerException;
import io.kestros.commons.uilibraries.api.models.ScriptType;
import io.kestros.commons.uilibraries.api.services.UiLibraryCompilationService;
import java.util.List;

/**
 * Compiles CSS and JS for {@link Theme}.
 */
public interface ThemeOutputCompilationService extends UiLibraryCompilationService, ManagedService {

  /**
   * Retrieves all ScriptTypes that a Theme will need to compile.
   *
   * @param theme UiFramework.
   * @param scriptType Baseline ScriptType (css/js).
   * @return all ScriptTypes that a UiFramework will need to compile.
   */
  List<ScriptType> getThemeScriptTypes(Theme theme, ScriptType scriptType);

  /**
   * Css or Javascript output prior to being compiled or minified.
   *
   * @param theme Theme.
   * @param scriptType ScriptType to retrieve.
   * @return Css or Javascript output prior to being compiled or minified.
   * @throws InvalidResourceTypeException Thrown when a referenced dependency could not be
   *     adapted to UiLibrary.
   * @throws NoMatchingCompilerException No matching compiler for the scriptType was found.
   */
  String getThemeSource(Theme theme, ScriptType scriptType)
      throws NoMatchingCompilerException, InvalidResourceTypeException;
}
