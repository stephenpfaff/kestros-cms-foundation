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

package io.kestros.cms.foundation.services;

import io.kestros.cms.componenttypes.api.exceptions.InvalidComponentTypeException;
import io.kestros.cms.componenttypes.api.exceptions.InvalidScriptException;
import io.kestros.cms.filetypes.HtmlFile;
import io.kestros.cms.foundation.content.components.parentcomponent.ParentComponent;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * Provides script paths for {@link ParentComponent}.
 */
public interface ScriptProviderService extends ManagedService {

  /**
   * Retrieves an absolute path for the matching HTL scrip for the passed {@link ParentComponent}.
   *
   * @param parentComponent Component to retrieve a script from.
   * @param scriptName Script to look up.
   * @param request current SlingHttpServletRequest. Used to find script paths for referenced
   *     components.
   * @return An absolute path for the matching scriptName for the passed {@link ParentComponent}.
   * @throws InvalidScriptException Expected HTL script was not not found, or was an invalid
   *     {@link HtmlFile}
   * @throws InvalidComponentTypeException expected componentType for the request component was
   *     missing or invalid.
   */
  String getScriptPath(ParentComponent parentComponent, String scriptName,
      SlingHttpServletRequest request) throws InvalidScriptException, InvalidComponentTypeException;

}
