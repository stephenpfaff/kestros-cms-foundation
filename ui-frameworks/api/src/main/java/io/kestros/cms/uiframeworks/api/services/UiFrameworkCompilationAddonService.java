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
import io.kestros.commons.structuredslingmodels.exceptions.InvalidResourceTypeException;
import io.kestros.commons.uilibraries.filetypes.ScriptType;
import javax.annotation.Nonnull;

/**
 * Service which can append additional CSS and JS to a UiFramework's compiled output.
 */
public interface UiFrameworkCompilationAddonService extends ManagedService {

  /**
   * Retrieves CSS and JS to append to a UiFramework's compiled output.
   *
   * @param uiFramework UiFramework to append.
   * @param scriptType ScriptType.
   * @return CSS and JS to append to a UiFramework's compiled output.
   * @throws InvalidResourceTypeException Specified UiFramework was invalid.
   */
  String getAppendedOutput(@Nonnull final UiFramework uiFramework,
      @Nonnull final ScriptType scriptType) throws InvalidResourceTypeException;

}
